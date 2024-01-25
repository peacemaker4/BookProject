package org.itstep.bookproject.controllers;

import lombok.extern.slf4j.Slf4j;
import org.itstep.bookproject.entities.*;
import org.itstep.bookproject.helpers.HelperMath;
import org.itstep.bookproject.models.*;
import org.itstep.bookproject.services.*;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@Slf4j
public class BookController {

    private static String UPLOADED_FOLDER_COVERS = "D:\\Data\\Images\\Covers\\";
    private static String UPLOADED_FOLDER_USER_COVERS = "D:\\Data\\Images\\CoversRequest\\";
    private static String UPLOADED_FOLDER_FILES = "D:\\Data\\Files\\Books\\";
    private static String UPLOADED_FOLDER_USER_FILES = "D:\\Data\\Files\\BooksRequest\\";
    private static String UPLOADED_FOLDER_USER_FILES_STR = "D:\\\\Data\\\\Files\\\\BooksRequest\\\\";
    private static String UPLOADED_FOLDER_FILES_STR = "D:\\\\Data\\\\Files\\\\Books\\\\";

    private final UserService userService;
    private final BookService bookService;
    private final BookRequestService bookRequestService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final RateService rateService;
    private final RateRequestService rateRequestService;
    private final ScoreService scoreService;
    private final AuthorSuggestService authorSuggestService;
    private final FavoriteBookService favoriteBookService;


    public BookController(UserService userService, BookService bookService, BookRequestService bookRequestService, AuthorService authorService, TagService tagService, RateService rateService, RateRequestService rateRequestService, ScoreService scoreService, AuthorSuggestService authorSuggestService, FavoriteBookService favoriteBookService) {
        this.userService = userService;
        this.bookService = bookService;
        this.bookRequestService = bookRequestService;
        this.authorService = authorService;
        this.tagService = tagService;
        this.rateService = rateService;
        this.rateRequestService = rateRequestService;
        this.scoreService = scoreService;
        this.authorSuggestService = authorSuggestService;
        this.favoriteBookService = favoriteBookService;
    }

    @GetMapping(value = "/")
    public String indexPage(Model model){
        return "redirect:/home";
    }

    @GetMapping(value = "/home")
    public String homePage(Model model) throws IOException {
        List<Book> lastBooks = bookService.getLast3();
        model.addAttribute("lastBooks", lastBooks);

        List<PictureModel> pictureModels = new ArrayList<>();

        for(var d : lastBooks){
            String profilePicturePath = d.getCover();
            String picFormat = null;
            String content = null;

            if(!profilePicturePath.isEmpty()){
                if(profilePicturePath.endsWith("png")){
                    picFormat = "png";
                }
                else if(profilePicturePath.endsWith("jpg")){
                    picFormat = "jpg";
                }
                content = getImageWithMediaType(profilePicturePath);
            }
            pictureModels.add(new PictureModel(picFormat, content));
        }
        model.addAttribute("pictureModels", pictureModels);

        List<Double> scores = new ArrayList<>();

        for(var b: lastBooks){
            Double scoring = 0d;
            Integer scoreAmount = 0;
            List<Score> book_scores = scoreService.getScoresByBook(b.getId());
            for(var r : book_scores){
                scoreAmount+=1;
                scoring+=r.getScore();
            }
            scores.add(HelperMath.round(scoring/scoreAmount,1));
        }

        model.addAttribute("scores", scores);

        List<Tag> tags = tagService.getTags();
        List<Tag> rand_tags = new ArrayList<>();
        Integer amount = 5;
        for(var t = 0; t < amount;){
            Random rand = new Random();
            Tag rand_tag = tags.get(rand.nextInt(tags.size()));
            if(!rand_tags.contains(rand_tag)){
                rand_tags.add(rand_tag);
                t++;
            }
        }
        model.addAttribute("rand_tags", rand_tags);

        return "home";
    }

    @RequestMapping(value = "books")
    public String booksSearch(Model model, @Param("keyword") String keyword) throws IOException {
        if(keyword.replaceAll(" ", "").isEmpty()){
            return "redirect:/books";
        }

        List<Book> books = bookService.findBooks(keyword);

        model.addAttribute("books", books);
        model.addAttribute("booksCount", books.size());

        List<PictureModel> pictureModels = new ArrayList<>();

        for(var d : books){
            String profilePicturePath = d.getCover();
            String picFormat = null;
            String content = null;

            if(!profilePicturePath.isEmpty()){
                if(profilePicturePath.endsWith("png")){
                    picFormat = "png";
                }
                else if(profilePicturePath.endsWith("jpg")){
                    picFormat = "jpg";
                }
                content = getImageWithMediaType(profilePicturePath);
            }
            pictureModels.add(new PictureModel(picFormat, content));
        }
        model.addAttribute("pictureModels", pictureModels);

        List<Double> scores = new ArrayList<>();

        for(var b: books){
            Double scoring = 0d;
            Integer scoreAmount = 0;
            List<Score> book_scores = scoreService.getScoresByBook(b.getId());
            for(var r : book_scores){
                scoreAmount+=1;
                scoring+=r.getScore();
            }
            scores.add(HelperMath.round(scoring/scoreAmount,1));
        }

        model.addAttribute("scores", scores);

        return "books/books-list";
    }

    @GetMapping(value = "/books/add")
    public String addBook(Model model){
        model.addAttribute("bookModel", new BookModel());
        model.addAttribute("authors", authorService.getAuthors());
        model.addAttribute("tags", tagService.getTags());
        return "books/add-book";
    }

    @PostMapping(value = "/books/add")
    public String addBook(Model model, @ModelAttribute BookModel bookModel){
        if(bookModel != null){
            List<Author> authors = new ArrayList<>();
            var authors_ids = bookModel.getAuthors_ids();
            if(!authors_ids.isEmpty()){
                for(var id: authors_ids){
                    Author a = authorService.getAuthor(id);
                    if(a != null){
                        authors.add(a);
                    }
                }
            }
            List<Tag> tags = new ArrayList<>();
            var tags_ids = bookModel.getTags_ids();
            if(!tags_ids.isEmpty()){
                for(var id: tags_ids){
                    Tag tag = tagService.getTag(id);
                    if(tag != null){
                        tags.add(tag);
                    }
                }
            }

            List<Role> roles = getUser().getRoles();
            boolean is_user = false;
            for(var r: roles){
                if(Objects.equals(r.getRole(), "ROLE_MOD")){
                    is_user = true;
                    break;
                }
            }
            if(is_user){
                Book new_book = new Book(bookModel.getName(), bookModel.getDescription(), authors, bookModel.getPublisher(), bookModel.getPages(), bookModel.getYear(), "", getUser(), tags, "", null, LocalDateTime.now());
                Book loaded_book = bookService.updateBook(new_book);
                return "redirect:/books/" + loaded_book.getId();
            }
            else{
                BookRequest new_book_request =new BookRequest(bookModel.getName(), bookModel.getDescription(), authors, bookModel.getPublisher(), bookModel.getPages(), bookModel.getYear(), "", getUser(), tags, "", null, LocalDateTime.now(), false, "");
                BookRequest loaded_book_request =bookRequestService.updateBookRequest(new_book_request);
                return "redirect:/books-requests/" + loaded_book_request.getId();
            }

        }
        return "books/add-book";
    }

    @PostMapping(value = "/ratings/add")
    public String addRating(Model model, @ModelAttribute RateModel rateModel){
        if(rateModel != null){
            List<Role> roles = getUser().getRoles();
            boolean is_user = false;
            for(var r: roles){
                if(Objects.equals(r.getRole(), "ROLE_MOD")){
                    is_user = true;
                    break;
                }
            }
            if(is_user){
                Rate rate = new Rate(rateModel.getTitle(), rateModel.getDescription(), bookService.getBook(rateModel.getBook_id()), getUser(), LocalDateTime.now());
                rateService.updateRate(rate);
                return "redirect:/books/" + rateModel.getBook_id();

            }
            else{
                RateRequest rateRequest = new RateRequest(rateModel.getTitle(), rateModel.getDescription(), bookService.getBook(rateModel.getBook_id()), getUser(), LocalDateTime.now(), false, "");
                rateRequestService.updateRateRequest(rateRequest);
                return "redirect:/users/" + getUser().getId() + "/reviews-requests";
            }

        }
        return "books";
    }

    @GetMapping(value = "/books")
    public String bookList(Model model,
                           @RequestParam(defaultValue = "0") Integer page,
                           @RequestParam(defaultValue = "4") Integer pageSize) throws IOException {
        List<Book> books = bookService.getBooks(page, pageSize);
        model.addAttribute("books", books);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("booksCount", bookService.getBooksCount());

        List<PictureModel> pictureModels = new ArrayList<>();

        for(var d : books){
            String profilePicturePath = d.getCover();
            String picFormat = null;
            String content = null;

            if(!profilePicturePath.isEmpty()){
                if(profilePicturePath.endsWith("png")){
                    picFormat = "png";
                }
                else if(profilePicturePath.endsWith("jpg")){
                    picFormat = "jpg";
                }
                content = getImageWithMediaType(profilePicturePath);
            }
            pictureModels.add(new PictureModel(picFormat, content));
        }
        model.addAttribute("pictureModels", pictureModels);

        List<Double> scores = new ArrayList<>();
        for(var b: books){
            Double scoring = 0d;
            Integer scoreAmount = 0;

            List<Score> book_scores = scoreService.getScoresByBook(b.getId());
            for(var r : book_scores){
                scoreAmount+=1;
                scoring+=r.getScore();
            }
            scores.add(HelperMath.round(scoring/scoreAmount,1));
        }

        model.addAttribute("scores", scores);

        return "books/books-list";
    }

    @GetMapping(value = "books/{id}")
    public String bookPage(Model model, @PathVariable Long id) throws IOException {
        model.addAttribute("rateModel", new RateModel());

        Book book = bookService.getBook(id);
        DbUser dbUser = getUser();
        model.addAttribute("user", dbUser);
        if(book != null){
            model.addAttribute("book", book);

            String bookCoverPath = book.getCover();
            if(!bookCoverPath.isEmpty()){
                if(bookCoverPath.endsWith("png")){
                    model.addAttribute("picFormat", "png");
                }
                else if(bookCoverPath.endsWith("jpg")){
                    model.addAttribute("picFormat", "jpg");
                }
                String content = getImageWithMediaType(bookCoverPath);
                model.addAttribute("bookCover", content);
            }
            List<PictureModel> pictureModels = new ArrayList<>();
            for(var a: book.getAuthors()){
                var authorPicture = a.getAuthorPicture();
                String picFormat = null;
                String content = null;
                if(authorPicture!=null){
                    if(authorPicture.endsWith("png")){
                        picFormat = "png";
                    }
                    else if(authorPicture.endsWith("jpg")){
                        picFormat = "jpg";
                    }
                    content = getImageWithMediaType(authorPicture);
                }
                pictureModels.add(new PictureModel(picFormat, content));
            }
            model.addAttribute("authorsPictures", pictureModels);

            List<Rate> ratings = rateService.getAllBookRatings(book.getId());
            ratings.sort(Comparator.comparing(BaseEntity::getId).reversed());
            model.addAttribute("ratings", ratings);

            Double scoring = 0d;
            Integer scoreAmount = 0;
            List<Score> book_scores = scoreService.getScoresByBook(book.getId());

            for(var r : book_scores){
                scoreAmount+=1;
                scoring+=r.getScore();
            }

            model.addAttribute("bookScore", HelperMath.round(scoring/scoreAmount,1));

            Score userScore = scoreService.findScoreByBookIdAndUserId(book.getId(), getUser().getId());

            Integer userScoreVal = null;

            if(userScore != null)
                userScoreVal = userScore.getScore();

            model.addAttribute("userScore", userScoreVal);

            List<Role> roles = dbUser.getRoles();
            boolean is_user = false;
            for(var r: roles){
                if(Objects.equals(r.getRole(), "ROLE_MOD")){
                    is_user = true;
                    break;
                }
            }

            model.addAttribute("is_user", is_user);

            FavoriteBook favoriteBook = favoriteBookService.getFavoriteBookByUserAndBookId(dbUser.getId(), book.getId());
            if(favoriteBook != null)
                model.addAttribute("favorite", true);
            else
                model.addAttribute("favorite", false);

            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }

        return "books/book";
    }



    @PostMapping("books/{id}/set/score")
    public String setBookScore(ScoreModel model, @PathVariable Long id){
        Book b = bookService.getBook(id);
        if(b != null){
            if(b.getUser().getId() == getUser().getId()){
                return "redirect:/books/" + b.getId();
            }
            Score score = scoreService.findScoreByBookIdAndUserId(b.getId(), getUser().getId());
            if(score != null){
                score.setScore(model.getScore());
                scoreService.updateScore(score);
            }
            else{
                score = new Score(model.getScore(), b, getUser());
                scoreService.updateScore(score);
            }

        }
        return "redirect:/books/"+id;
    }

    @GetMapping(value = "books-requests/{id}")
    public String bookRequestPage(Model model, @PathVariable Long id) throws IOException {
        BookRequest book = bookRequestService.getBookRequest(id);
        DbUser dbUser = getUser();

        if(book != null){
            if(dbUser!= null){

                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if(book.getUser().getId() == dbUser.getId())
                    allowed = true;
                if(is_user)
                    allowed = true;
                if(!allowed)
                    return "redirect:/books/";

                model.addAttribute("user", dbUser);
                model.addAttribute("is_user", is_user);
                model.addAttribute("is_approved", book.isNotApproved());

                model.addAttribute("book", book);

                String bookCoverPath = book.getCover();
                if(!bookCoverPath.isEmpty()){
                    if(bookCoverPath.endsWith("png")){
                        model.addAttribute("picFormat", "png");
                    }
                    else if(bookCoverPath.endsWith("jpg")){
                        model.addAttribute("picFormat", "jpg");
                    }
                    String content = getImageWithMediaType(bookCoverPath);
                    model.addAttribute("bookCover", content);
                }
                List<PictureModel> pictureModels = new ArrayList<>();
                for(var a: book.getAuthors()){
                    var authorPicture = a.getAuthorPicture();
                    String picFormat = null;
                    String content = null;
                    if(authorPicture!=null){
                        if(authorPicture.endsWith("png")){
                            picFormat = "png";
                        }
                        else if(authorPicture.endsWith("jpg")){
                            picFormat = "jpg";
                        }
                        content = getImageWithMediaType(authorPicture);
                    }
                    pictureModels.add(new PictureModel(picFormat, content));
                }
                model.addAttribute("authorsPictures", pictureModels);


                model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));

            }
            }

        return "books/book-request";
    }

    @GetMapping("books-requests/{id}/edit")
    public String getBookRequestEdit(Model model, @PathVariable Long id) throws IOException {
        BookRequest book = bookRequestService.getBookRequest(id);
        DbUser dbUser = getUser();

        if(book != null){
            if(dbUser!= null){
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if(book.getUser().getId() == dbUser.getId())
                    allowed = true;
                if(is_user)
                    allowed = true;
                if(!allowed)
                    return "redirect:/books/";
                if(book.isNotApproved())
                    return "redirect:/books-requests/" + book.getId();
            }

            model.addAttribute("book", book);
            model.addAttribute("user", dbUser);

            BookModel bookModel = new BookModel();
            bookModel.setName(book.getName());
            bookModel.setDescription(book.getDescription());
            var authors_ids = new ArrayList<Long>();
            for(var a: book.getAuthors()){
                authors_ids.add(a.getId());
            }
            bookModel.setAuthors_ids(authors_ids);
            var tags_ids = new ArrayList<Long>();
            for(var t: book.getTags()){
                tags_ids.add(t.getId());
            }
            bookModel.setTags_ids(tags_ids);
            bookModel.setPublisher(book.getPublisher());
            bookModel.setYear(book.getYear());
            bookModel.setPages(book.getPages());
            bookModel.setCover(book.getCover());

            model.addAttribute("bookModel", bookModel);

            String bookCoverPath = book.getCover();
            if(!bookCoverPath.isEmpty()){
                if(bookCoverPath.endsWith("png")){
                    model.addAttribute("picFormat", "png");
                }
                else if(bookCoverPath.endsWith("jpg")){
                    model.addAttribute("picFormat", "jpg");
                }
                String content = getImageWithMediaType(bookCoverPath);
                model.addAttribute("bookCover", content);
            }
            model.addAttribute("authors", authorService.getAuthors());
            model.addAttribute("tags", tagService.getTags());
        }

        return "books/edit-book-request";
    }

    @PostMapping(value = "books-requests/{id}/edit")
    public String editBookRequest(@ModelAttribute BookModel bookModel, @PathVariable Long id){
        if(bookModel != null){
            BookRequest b = bookRequestService.getBookRequest(id);
            DbUser dbUser = getUser();

            if(b != null){
                if(dbUser!= null){
                    List<Role> roles = dbUser.getRoles();
                    boolean is_user = false;
                    for(var r: roles){
                        if(Objects.equals(r.getRole(), "ROLE_MOD")){
                            is_user = true;
                            break;
                        }
                    }
                    boolean allowed = false;
                    if(b.getUser().getId() == dbUser.getId())
                        allowed = true;
                    if(is_user)
                        allowed = true;
                    if(!allowed)
                        return "redirect:/books/";
                    if(b.isNotApproved())
                        return "redirect:/books-requests/" + b.getId();

                    b.setName(bookModel.getName());
                    b.setDescription(bookModel.getDescription());
                    List<Author> authors = new ArrayList<>();
                    var authors_ids = bookModel.getAuthors_ids();
                    if(!authors_ids.isEmpty()){
                        for(var author_id: authors_ids){
                            Author a = authorService.getAuthor(author_id);
                            if(a != null){
                                authors.add(a);
                            }
                        }
                    }
                    List<Tag> tags = new ArrayList<>();
                    var tags_ids = bookModel.getTags_ids();
                    if(!tags_ids.isEmpty()){
                        for(var tag_id: tags_ids){
                            Tag tag = tagService.getTag(tag_id);
                            if(tag != null){
                                tags.add(tag);
                            }
                        }
                    }
                    b.setAuthors(authors);
                    b.setTags(tags);
                    b.setPublisher(bookModel.getPublisher());
                    b.setYear(bookModel.getYear());
                    b.setPages(bookModel.getPages());

                    bookRequestService.updateBookRequest(b);
                }
            }
            return "redirect:/books-requests/"+b.getId();
        }
        return "books/edit-book-request";
    }

    @PostMapping("books-requests/{id}/add/cover")
    public String bookRequestCoverUpload(@RequestParam("file") MultipartFile file,
                                  Model model, @PathVariable Long id) throws IOException {
        BookRequest b = bookRequestService.getBookRequest(id);
        DbUser dbUser = getUser();

        if(b != null){
            if(dbUser!= null){
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if(b.getUser().getId() == dbUser.getId())
                    allowed = true;
                if(is_user)
                    allowed = true;
                if(!allowed)
                    return "redirect:/books/";
                if(b.isNotApproved())
                    return "redirect:/books-requests/" + b.getId();

                byte[] bytes = file.getBytes();
                if(file.getContentType().contains("image/")){
                    if(!b.getCover().isEmpty()){
                        File f= new File(b.getCover());
                        if(f.delete()){
                            b.setCover("");
                            bookRequestService.updateBookRequest(b);
                        }
                    }
                    Path path = Paths.get(UPLOADED_FOLDER_USER_COVERS + b.getId().toString() + "." + file.getContentType().replaceAll("image/", ""));
                    Files.write(path, bytes);

                    b.setCover(path.toString());
                    bookRequestService.updateBookRequest(b);
                }

            }
        }

        return "redirect:/books-requests/"+b.getId()+"/edit";
    }

    @PostMapping("books-requests/{id}/delete/cover")
    public String bookCoverDelete(Model model, @PathVariable Long id) throws IOException {
        BookRequest b = bookRequestService.getBookRequest(id);

        DbUser dbUser = getUser();

        if(b != null){
            if(dbUser!= null){
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if(b.getUser().getId() == dbUser.getId())
                    allowed = true;
                if(is_user)
                    allowed = true;
                if(!allowed)
                    return "redirect:/books/";
                if(b.isNotApproved())
                    return "redirect:/books-requests/" + b.getId();
                if(!b.getCover().isEmpty()){
                    File f= new File(b.getCover());
                    if(f.delete()){
                        b.setCover("");
                        bookRequestService.updateBookRequest(b);
                    }
                }
            }
        }

        return "redirect:/books-requests/"+b.getId()+"/edit";
    }

    @PostMapping("books-requests/{id}/delete")
    public String deleteBookRequest(Model model, @PathVariable Long id){
        BookRequest b = bookRequestService.getBookRequest(id);

        DbUser dbUser = getUser();

        if(b != null){
            if(dbUser!= null){
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if(b.getUser().getId() == dbUser.getId())
                    allowed = true;
                if(is_user)
                    allowed = true;
                if(!allowed)
                    return "redirect:/books/";
                if(b.isNotApproved())
                    return "redirect:/books-requests/" + b.getId();
                if(!b.getCover().isEmpty()){
                    File f= new File(b.getCover());
                    if(f.delete()){
                        b.setCover("");
                        bookRequestService.removeBooksRequest(b.getId());
                    }
                }

            bookService.deleteBook(id);
            }
        }
        return "redirect:/users/"+ dbUser.getId() +"/books-requests";
    }

    @GetMapping(value = "authors/{id}")
    public String authorPage(Model model, @PathVariable Long id) throws IOException {
        Author author = authorService.getAuthor(id);
        if(author != null){
            model.addAttribute("author", author);
            model.addAttribute("user", getUser());

            String authorPicture = author.getAuthorPicture();
            if(authorPicture!= null){
                if(authorPicture.endsWith("png")){
                    model.addAttribute("picFormat", "png");
                }
                else if(authorPicture.endsWith("jpg")){
                    model.addAttribute("picFormat", "jpg");
                }
                String content = getImageWithMediaType(authorPicture);
                model.addAttribute("authorPicture", content);
            }
        }

        return "authors/author";
    }




    @GetMapping(value = "/tags/{id}")
    public String booksByTag(Model model, @PathVariable Long id) throws IOException {
        List<Book> books = bookService.getBooks();
        List<Book> tagBooks = new ArrayList<>();

        for(var b: books){
            List<Tag> tags = b.getTags();
            for(var t: tags){
                if(t.getId() == id){
                    tagBooks.add(b);
                    break;
                }
            }
        }

        model.addAttribute("books", tagBooks);
        model.addAttribute("booksCount", bookService.getBooksCount());

        List<PictureModel> pictureModels = new ArrayList<>();

        for(var d : tagBooks){
            String profilePicturePath = d.getCover();
            String picFormat = null;
            String content = null;

            if(!profilePicturePath.isEmpty()){
                if(profilePicturePath.endsWith("png")){
                    picFormat = "png";
                }
                else if(profilePicturePath.endsWith("jpg")){
                    picFormat = "jpg";
                }
                content = getImageWithMediaType(profilePicturePath);
            }
            pictureModels.add(new PictureModel(picFormat, content));
        }
        model.addAttribute("pictureModels", pictureModels);

        List<Double> scores = new ArrayList<>();

        for(var b: books){
            Double scoring = 0d;
            Integer scoreAmount = 0;
            List<Score> book_scores = scoreService.getScoresByBook(b.getId());
            for(var r : book_scores){
                scoreAmount+=1;
                scoring+=r.getScore();
            }
            scores.add(HelperMath.round(scoring/scoreAmount,1));
        }

        model.addAttribute("scores", scores);

        return "books/books-list";
    }

    @PostMapping("edit/books/{id}/add/file")
    public String bookFileUpload(@RequestParam("file") MultipartFile file,
                                  Model model, @PathVariable Long id) throws IOException {
        Book b = bookService.getBook(id);

        if(b != null){
                byte[] bytes = file.getBytes();
                String file_type = file.getContentType();
                if(file_type.contains("application/") || file_type.contains("text/")){
                    if(!b.getFile().isEmpty()){
                        File f= new File(b.getFile());
                        if(f.delete()){
                            b.setFile("");
                            b.setFileType(null);
                            bookService.updateBook(b);
                        }
                    }
                    String st = file.getOriginalFilename();
                    Path path = Paths.get(UPLOADED_FOLDER_FILES + b.getId().toString() + "." + st.substring(st.lastIndexOf('.')+1).trim());
                    Files.write(path, bytes);

                    b.setFile(path.toString());
                    b.setFileType(file_type);
                    bookService.updateBook(b);
                }
        }

        return "redirect:/edit/books/"+b.getId();
    }

    @PostMapping("edit/books/{id}/delete/file")
    public String bookFileDelete(Model model, @PathVariable Long id) throws IOException {
        Book b = bookService.getBook(id);

        if(b != null){
            if(!b.getFile().isEmpty()){
                File f= new File(b.getFile());
                if(f.delete()){
                    b.setFile("");
                    b.setFileType(null);
                    bookService.updateBook(b);
                }
            }
        }

        return "redirect:/edit/books/"+b.getId();
    }

    @RequestMapping(value="books/{id}/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> loadBook(Model model, @PathVariable Long id) throws IOException {
        Book b = bookService.getBook(id);

        if(b != null) {
            if(!b.getFile().isEmpty()){
                File file = new File(b.getFile());
                byte[] contents = Files.readAllBytes(file.toPath());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(b.getFileType()));
                String filename = b.getFile().replaceAll(UPLOADED_FOLDER_FILES_STR, "");
                headers.setContentDispositionFormData(filename, filename);
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
                return response;
            }
        }
        return null;
    }

    @PostMapping("books-requests/{id}/add/file")
    public String bookRequestFileUpload(@RequestParam("file") MultipartFile file,
                                 Model model, @PathVariable Long id) throws IOException {
        BookRequest b = bookRequestService.getBookRequest(id);
        DbUser dbUser = getUser();
        if(b != null){
            if(dbUser!= null) {
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for (var r : roles) {
                    if (Objects.equals(r.getRole(), "ROLE_MOD")) {
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if (b.getUser().getId() == dbUser.getId())
                    allowed = true;
                if (is_user)
                    allowed = true;
                if (!allowed)
                    return "redirect:/books-requests/" + getUser().getId();
                byte[] bytes = file.getBytes();
                String file_type = file.getContentType();
                if (file_type.contains("application/") || file_type.contains("text/")) {
                    if (!b.getFile().isEmpty()) {
                        File f = new File(b.getFile());
                        if (f.delete()) {
                            b.setFile("");
                            b.setFileType(null);
                            bookRequestService.updateBookRequest(b);
                        }
                    }
                    String st = file.getOriginalFilename();
                    Path path = Paths.get(UPLOADED_FOLDER_USER_FILES + b.getId().toString() + "." + st.substring(st.lastIndexOf('.') + 1).trim());
                    Files.write(path, bytes);

                    b.setFile(path.toString());
                    b.setFileType(file_type);
                    bookRequestService.updateBookRequest(b);
                }
            }
        }

        return "redirect:/books-requests/"+b.getId() +"/edit";
    }

    @PostMapping("books-requests/{id}/delete/file")
    public String bookRequestFileDelete(Model model, @PathVariable Long id) throws IOException {
        BookRequest b = bookRequestService.getBookRequest(id);
        DbUser dbUser = getUser();
        if(b != null){
            if(dbUser!= null) {
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for (var r : roles) {
                    if (Objects.equals(r.getRole(), "ROLE_MOD")) {
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if (b.getUser().getId() == dbUser.getId())
                    allowed = true;
                if (is_user)
                    allowed = true;
                if (!allowed)
                    return "redirect:/books-requests/" + getUser().getId();
                if (!b.getFile().isEmpty()) {
                    File f = new File(b.getFile());
                    if (f.delete()) {
                        b.setFile("");
                        b.setFileType(null);
                        bookRequestService.updateBookRequest(b);
                    }
                }
            }
        }

        return "redirect:/books-requests/"+b.getId()+"/edit";
    }

    @RequestMapping(value="books-requests/{id}/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> loadBookRequest(Model model, @PathVariable Long id) throws IOException {
        BookRequest b = bookRequestService.getBookRequest(id);
        DbUser dbUser = getUser();
        if(b != null) {
            if (dbUser != null) {
                List<Role> roles = dbUser.getRoles();
                boolean is_user = false;
                for (var r : roles) {
                    if (Objects.equals(r.getRole(), "ROLE_MOD")) {
                        is_user = true;
                        break;
                    }
                }
                boolean allowed = false;
                if (b.getUser().getId() == dbUser.getId())
                    allowed = true;
                if (is_user)
                    allowed = true;
                if (!allowed)
                    return null;
                if (!b.getFile().isEmpty()) {
                    File file = new File(b.getFile());
                    byte[] contents = Files.readAllBytes(file.toPath());

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(b.getFileType()));
                    String filename = b.getFile().replaceAll(UPLOADED_FOLDER_USER_FILES_STR, "");
                    headers.setContentDispositionFormData(filename, filename);
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
                    return response;
                }
            }
        }
        return null;
    }

    @GetMapping(value = "authors")
    public String tagsList(Model model){
        List<Author> authors = authorService.getAuthors();
        model.addAttribute("authors", authors);

        return "authors/authors";
    }

    @GetMapping(value = "authors/suggest")
    public String suggestBook(Model model){
        model.addAttribute("authorModel", new AuthorModel());
        return "authors/suggest-author";
    }

    @PostMapping(value = "authors/suggest")
    public String suggestBook(Model model, @ModelAttribute AuthorModel authorModel){
        if(authorModel != null){
            AuthorSuggest authorSuggest = new AuthorSuggest(authorModel.getFullName(), authorModel.getDescription(), null, getUser().getId());
            authorSuggestService.updateAuthorSuggest(authorSuggest);

            return "redirect:/authors/";
        }

        return "authors/suggest-author";
    }

    @GetMapping(value = "/users/{id}/favorite")
    public String bookList(Model model, @PathVariable Long id) throws IOException {

        DbUser dbUser = userService.getUser(id);

        if(dbUser != null){
            List<FavoriteBook> favoriteBooks = favoriteBookService.getFavoriteBooksOfUser(dbUser.getId());
            List<Book> books = new ArrayList<>();

            for(var f: favoriteBooks)
                books.add(f.getBook());

            model.addAttribute("user", dbUser);
            model.addAttribute("books", books);

            List<PictureModel> pictureModels = new ArrayList<>();

            for(var d : books){
                String profilePicturePath = d.getCover();
                String picFormat = null;
                String content = null;

                if(!profilePicturePath.isEmpty()){
                    if(profilePicturePath.endsWith("png")){
                        picFormat = "png";
                    }
                    else if(profilePicturePath.endsWith("jpg")){
                        picFormat = "jpg";
                    }
                    content = getImageWithMediaType(profilePicturePath);
                }
                pictureModels.add(new PictureModel(picFormat, content));
            }
            model.addAttribute("pictureModels", pictureModels);

            List<Double> scores = new ArrayList<>();
            for(var b: books){
                Double scoring = 0d;
                Integer scoreAmount = 0;

                List<Score> book_scores = scoreService.getScoresByBook(b.getId());
                for(var r : book_scores){
                    scoreAmount+=1;
                    scoring+=r.getScore();
                }
                scores.add(HelperMath.round(scoring/scoreAmount,1));
            }

            model.addAttribute("scores", scores);
        }

        return "users/favorite-books";
    }

    public String getImageWithMediaType(String path) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(content);
    }

    private DbUser getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User securityUser = (User) authentication.getPrincipal();
            DbUser user = userService.getUser(securityUser.getUsername());
            return user;
        }
        return null;
    }
}
