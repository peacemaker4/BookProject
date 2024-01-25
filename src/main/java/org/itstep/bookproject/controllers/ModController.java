package org.itstep.bookproject.controllers;

import lombok.extern.slf4j.Slf4j;
import org.itstep.bookproject.entities.*;
import org.itstep.bookproject.models.AuthorModel;
import org.itstep.bookproject.models.BookModel;
import org.itstep.bookproject.models.DeclineModel;
import org.itstep.bookproject.models.PictureModel;
import org.itstep.bookproject.services.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@Slf4j
public class ModController {
    private static String UPLOADED_FOLDER_AUTHORS = "D:\\Data\\Images\\Authors\\";
    private static String UPLOADED_FOLDER_COVERS = "D:\\Data\\Images\\Covers\\";
    private static String UPLOADED_FOLDER_USERS_COVERS = "D:\\\\Data\\\\Images\\\\CoversRequest\\\\";
    private static String UPLOADED_FOLDER_FILES = "D:\\Data\\Files\\Books\\";
    private static String UPLOADED_FOLDER_USER_FILES_STR = "D:\\\\Data\\\\Files\\\\BooksRequest\\\\";

    private final AuthorService authorService;
    private final AuthorSuggestService authorSuggestService;
    private final UserService userService;
    private final RateRequestService rateRequestService;
    private final RateService rateService;
    private final BookRequestService bookRequestService;
    private final BookService bookService;
    private final TagService tagService;
    private final TagSuggestService tagSuggestService;

    public ModController(AuthorService authorService, AuthorSuggestService authorSuggestService, UserService userService, RateRequestService rateRequestService, RateService rateService, BookRequestService bookRequestService, BookService bookService, TagService tagService, TagSuggestService tagSuggestService) {
        this.authorService = authorService;
        this.authorSuggestService = authorSuggestService;
        this.userService = userService;
        this.rateRequestService = rateRequestService;
        this.rateService = rateService;
        this.bookRequestService = bookRequestService;
        this.bookService = bookService;
        this.tagService = tagService;
        this.tagSuggestService = tagSuggestService;
    }

    @GetMapping("moderation")
    public String modPage(Model model) throws IOException {
        return "mod/mod-page";
    }

    @GetMapping("edit/reviews-requests")
    public String booksReviewsRequests(Model model,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {
        List<RateRequest> ratings = rateRequestService.getRatingsRequestsNotApproved(false, page, pageSize);

        model.addAttribute("ratings", ratings);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("ratingsCount", rateRequestService.getRateRequestsCountNotApproved(false));
        model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));

        return "mod/rate-requests";
    }

    @GetMapping("edit/reviews-requests/declined")
    public String booksReviewsRequestsDeclined(Model model,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {
        List<RateRequest> ratings = rateRequestService.getRatingsRequestsNotApproved(true, page, pageSize);

        model.addAttribute("ratings", ratings);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("ratingsCount", rateRequestService.getRateRequestsCountNotApproved(true));
        model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));

        return "mod/rate-requests-declined";
    }

    @PostMapping("edit/reviews-requests/{id}/allow")
    public String allowReview(Model model, @PathVariable Long id) throws IOException {
        RateRequest request = rateRequestService.getRateRequest(id);

        if(request != null){
            Rate rate = new Rate(request.getTitle(), request.getDescription(), request.getBook(), request.getUser(), LocalDateTime.now());
            rateService.updateRate(rate);

            rateRequestService.removeRateRequest(request.getId());
        }

        return "redirect:/edit/reviews-requests";
    }

    @PostMapping("edit/reviews-requests/declined/{id}/allow")
    public String allowReviewDeclined(Model model, @PathVariable Long id) throws IOException {
        RateRequest request = rateRequestService.getRateRequest(id);

        if(request != null){
            Rate rate = new Rate(request.getTitle(), request.getDescription(), request.getBook(), request.getUser(), LocalDateTime.now());
            rateService.updateRate(rate);

            rateRequestService.removeRateRequest(request.getId());
        }

        return "redirect:/edit/reviews-requests/declined";
    }

    @PostMapping("edit/reviews-requests/{id}/decline")
    public String declineReview(@ModelAttribute DeclineModel declineModel, @PathVariable Long id) throws IOException {
        RateRequest request = rateRequestService.getRateRequest(id);

        if(request != null){
            request.setNotApproved(true);
            request.setCommentary(declineModel.getCommentary());
            rateRequestService.updateRateRequest(request);
        }

        return "redirect:/edit/reviews-requests";
    }

    @PostMapping("edit/books-requests/{id}/allow")
    public String allowBook(Model model, @PathVariable Long id) throws IOException {
        BookRequest request = bookRequestService.getBookRequest(id);

        if(request != null){
            List<Author> authors = new ArrayList<>();

            for(var a: request.getAuthors())
                authors.add(a);

            List<Tag> tags = new ArrayList<>();

            for(var t: request.getTags())
                tags.add(t);

            Book book = new Book(request.getName(), request.getDescription(), authors, request.getPublisher(), request.getPages(), request.getYear(), request.getCover(), request.getUser(), tags, request.getFile(), request.getFileType(), LocalDateTime.now());
            bookService.updateBook(book);

            if(!book.getCover().isEmpty()){
                File f= new File(request.getCover());
                String new_path = UPLOADED_FOLDER_COVERS + book.getId().toString() + "." + request.getCover().replaceAll(UPLOADED_FOLDER_USERS_COVERS + request.getId().toString() + '.', "");
                f.renameTo(new File(new_path));
                Book new_b = bookService.getBook(book.getId());
                new_b.setCover(new_path);
                bookService.updateBook(new_b);

                f = new File(request.getCover());
                f.delete();
            }
            if(!book.getFile().isEmpty()){
                File f= new File(request.getFile());
                String new_path = UPLOADED_FOLDER_FILES + book.getId().toString() + "." + request.getFile().replaceAll(UPLOADED_FOLDER_USER_FILES_STR + request.getId().toString() + '.', "");
                f.renameTo(new File(new_path));
                Book new_b = bookService.getBook(book.getId());
                new_b.setFile(new_path);
                bookService.updateBook(new_b);

                f = new File(request.getFile());
                f.delete();
            }

            bookRequestService.removeBooksRequest(request.getId());
        }

        return "redirect:/edit/books-requests";
    }

    @PostMapping("edit/books-requests/{id}/decline")
    public String declineBook(@ModelAttribute DeclineModel declineModel, @PathVariable Long id) throws IOException {
        BookRequest request = bookRequestService.getBookRequest(id);

        if(request != null){
            if(!request.getCover().isEmpty()){
                File f = new File(request.getCover());
                f.delete();
            }
            if(!request.getFile().isEmpty()){
                File f = new File(request.getFile());
                f.delete();
            }
            request.setCover("");
            request.setFile("");
            request.setFileType(null);
            request.setCommentary(declineModel.getCommentary());
            request.setNotApproved(true);

            bookRequestService.updateBookRequest(request);
        }

        return "redirect:/edit/books-requests";
    }

    @GetMapping("edit/books-requests")
    public String booksRequests(Model model,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "4") Integer pageSize) throws IOException {
        List<BookRequest> books = bookRequestService.getBooksRequestsNotApproved(false, page, pageSize);

        model.addAttribute("books", books);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("booksCount", bookRequestService.getBooksRequestsCountNotApproved(false));
        model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));

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

        return "mod/book-requests";
    }

    @PostMapping("edit/authors/{id}/add/picture")
    public String authorPictureUpload(@RequestParam("file") MultipartFile file,
                                      Model model, @PathVariable Long id) throws IOException {
        Author a = authorService.getAuthor(id);

        if(a != null){
            if(a.getUserId().equals(getUser().getId())){
                byte[] bytes = file.getBytes();
                if(file.getContentType().contains("image/")){
                    Path path = Paths.get(UPLOADED_FOLDER_AUTHORS + a.getId().toString() + "." + file.getContentType().replaceAll("image/", ""));
                    Files.write(path, bytes);

                    a.setAuthorPicture(path.toString());
                    authorService.updateAuthor(a);
                }

            }
        }

        return "redirect:/edit/authors/"+a.getId();
    }

    @GetMapping("edit/authors/{id}")
    public String authorEdit(Model model, @PathVariable Long id) throws IOException {
        Author author = authorService.getAuthor(id);
        if(author != null){
            if(!author.getUserId().equals(getUser().getId())){
                return "redirect:/authors/" + author.getId();
            }
            model.addAttribute("author", author);
            AuthorModel authorModel = new AuthorModel();
            authorModel.setFullName(author.getFullName());
            authorModel.setDescription(author.getDescription());

            model.addAttribute("authorModel", authorModel);

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
        return "authors/edit-author";
    }

    @PostMapping(value = "edit/authors/{id}")
    public String editAuthor(@ModelAttribute AuthorModel authorModel, @PathVariable Long id){
        if(authorModel != null){
            Author a = authorService.getAuthor(id);
            if(a != null){
                if(a.getUserId().equals(getUser().getId())){
                    a.setFullName(authorModel.getFullName());
                    a.setDescription(authorModel.getDescription());
                    authorService.updateAuthor(a);
                }
            }
            return "redirect:/edit/authors/"+a.getId();
        }
        return "authors/edit-author";
    }

    @PostMapping("edit/authors/{id}/delete/picture")
    public String authorPictureUpload(Model model, @PathVariable Long id) throws IOException {
        Author a = authorService.getAuthor(id);

        if(a != null){
            if(a.getUserId().equals(getUser().getId())){
                if(a.getAuthorPicture() != null){
                    File f= new File(a.getAuthorPicture());
                    if(f.delete()){
                        a.setAuthorPicture(null);
                        authorService.updateAuthor(a);
                    }
                }
            }
        }

        return "redirect:/edit/authors/"+a.getId();
    }

    @GetMapping(value = "edit/authors/add")
    public String authorAddPage(Model model) throws IOException {
        model.addAttribute("authorModel", new AuthorModel());

        return "authors/add-author";
    }

    @PostMapping(value = "edit/authors/add")
    public String addAuthor(Model model, @ModelAttribute AuthorModel authorModel){
        if(authorModel != null){
            Author author = new Author(authorModel.getFullName(), authorModel.getDescription(), null, getUser().getId());
            Author added = authorService.updateAuthor(author);

            return "redirect:/edit/authors/" + added.getId();
        }
        return "authors/add-author";
    }


    @GetMapping("edit/books/{id}")
    public String getBookEdit(Model model, @PathVariable Long id) throws IOException {
        Book book = bookService.getBook(id);
        if(book != null){
            model.addAttribute("book", book);
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

        return "books/edit-book";
    }

    @PostMapping(value = "edit/books/{id}")
    public String editBook(@ModelAttribute BookModel bookModel, @PathVariable Long id){
        if(bookModel != null){
            Book b = bookService.getBook(id);
            if(b != null){
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

                    bookService.updateBook(b);
            }
            return "redirect:/books/"+b.getId();
        }
        return "books/edit-book";
    }

    @PostMapping("edit/books/{id}/add/cover")
    public String bookCoverUpload(@RequestParam("file") MultipartFile file,
                                  Model model, @PathVariable Long id) throws IOException {
        Book b = bookService.getBook(id);

        if(b != null){

                byte[] bytes = file.getBytes();
                if(file.getContentType().contains("image/")){
                    if(!b.getCover().isEmpty()){
                        File f= new File(b.getCover());
                        if(f.delete()){
                            b.setCover("");
                            bookService.updateBook(b);
                        }
                    }
                    Path path = Paths.get(UPLOADED_FOLDER_COVERS + b.getId().toString() + "." + file.getContentType().replaceAll("image/", ""));
                    Files.write(path, bytes);

                    b.setCover(path.toString());
                    bookService.updateBook(b);
                }

        }

        return "redirect:/edit/books/"+b.getId();
    }

    @PostMapping("edit/books/{id}/delete/cover")
    public String bookCoverUpload(Model model, @PathVariable Long id) throws IOException {
        Book b = bookService.getBook(id);

        if(b != null){
                if(!b.getCover().isEmpty()){
                    File f= new File(b.getCover());
                    if(f.delete()){
                        b.setCover("");
                        bookService.updateBook(b);
                    }
                }
        }

        return "redirect:/edit/books/"+b.getId();
    }

    @PostMapping("edit/books/{id}/delete")
    public String deleteBook(Model model, @PathVariable Long id){
        Book b = bookService.getBook(id);
            if(!b.getCover().isEmpty()){
                File f= new File(b.getCover());
                if(f.delete()){
                    b.setCover("");
                    bookService.updateBook(b);
                }
            }
            if(!b.getFile().isEmpty()){
                File f = new File(b.getFile());
                f.delete();
            }

            bookService.deleteBook(id);
        return "redirect:/books";
    }

    @GetMapping(value = "edit/tags-suggestions")
    public String tagsSuggestionsList(Model model){
        List<TagSuggest> tagSuggests = tagSuggestService.getTagsSuggestions();
        model.addAttribute("tags", tagSuggests);

        return "mod/tags-suggestions";
    }

    @PostMapping(value = "edit/tags-suggestions/{id}/add")
    public String tagSuggestionAdd(Model model, @PathVariable Long id){
        TagSuggest tagSuggest = tagSuggestService.getTagSuggestion(id);
        Tag new_tag = new Tag(tagSuggest.getName(), tagSuggest.getDescription());
        tagService.updateTag(new_tag);
        tagSuggestService.deleteTagSuggest(id);

        return "redirect:/edit/tags-suggestions";
    }

    @PostMapping(value = "edit/tags-suggestions/{id}/delete")
    public String tagSuggestionDelete(Model model, @PathVariable Long id){
        TagSuggest tagSuggest = tagSuggestService.getTagSuggestion(id);
        tagSuggestService.deleteTagSuggest(id);

        return "redirect:/edit/tags-suggestions";
    }

    @GetMapping(value = "edit/authors-suggestions")
    public String authorsSuggestionsList(Model model){
        List<AuthorSuggest> authorSuggests = authorSuggestService.getAuthorsSuggestions();
        model.addAttribute("authors", authorSuggests);

        return "mod/authors-suggestions";
    }

    @PostMapping(value = "edit/authors-suggestions/{id}/add")
    public String authorSuggestionAdd(Model model, @PathVariable Long id){
        AuthorSuggest authorSuggest = authorSuggestService.getAuthorSuggestions(id);
        Author new_author = new Author(authorSuggest.getFullName(), authorSuggest.getDescription(), null, getUser().getId());
        authorService.updateAuthor(new_author);
        authorSuggestService.deleteAuthorSuggest(id);

        return "redirect:/edit/authors-suggestions";
    }

    @PostMapping(value = "edit/authors-suggestions/{id}/delete")
    public String authorSuggestionDelete(Model model, @PathVariable Long id){
        AuthorSuggest authorSuggest = authorSuggestService.getAuthorSuggestions(id);
        authorSuggestService.deleteAuthorSuggest(id);

        return "redirect:/edit/authors-suggestions";
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
