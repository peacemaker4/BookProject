package org.itstep.bookproject.controllers;

import org.itstep.bookproject.entities.*;
import org.itstep.bookproject.helpers.HelperMath;
import org.itstep.bookproject.models.PictureModel;
import org.itstep.bookproject.models.UserDetailsModel;
import org.itstep.bookproject.services.*;
import org.springframework.data.repository.query.Param;
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
import java.util.*;

@Controller
public class UserController {

    private static String UPLOADED_FOLDER_USERS = "D:\\Data\\Images\\Users\\";
    private static String UPLOADED_FOLDER_WALLPAPERS = "D:\\Data\\Images\\Wallpapers\\";

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final RelationshipService relationshipService;
    private final RateService rateService;
    private final RateRequestService rateRequestService;
    private final BookService bookService;
    private final BookRequestService bookRequestService;
    private final FavoriteBookService favoriteBookService;

    public UserController(UserService userService, UserDetailsService userDetailsService, RelationshipService relationshipService, RateService rateService, RateRequestService rateRequestService, BookService bookService, BookRequestService bookRequestService, FavoriteBookService favoriteBookService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.relationshipService = relationshipService;
        this.rateService = rateService;
        this.rateRequestService = rateRequestService;
        this.bookService = bookService;
        this.bookRequestService = bookRequestService;
        this.favoriteBookService = favoriteBookService;
    }

    @GetMapping(value = "users/{id}")
    public String UserProfile(Model model, @PathVariable Long id) throws IOException {
        model.addAttribute("user", getUser());
        DbUser userProfile = userService.getUser(id);
        if(userProfile != null){
            model.addAttribute("userProfile", userProfile);
            String profilePicturePath = userProfile.getDetails().getProfilePicture();
            if(profilePicturePath != null){
                if(profilePicturePath.endsWith("png")){
                    model.addAttribute("picFormat", "png");
                }
                else if(profilePicturePath.endsWith("jpg")){
                    model.addAttribute("picFormat", "jpg");
                }
                String content = getImageWithMediaType(profilePicturePath);
                model.addAttribute("profilePicture", content);
            }
            String profileWallpaperPath = userProfile.getDetails().getProfileWallpaper();
            if(profileWallpaperPath != null){
                if(profileWallpaperPath.endsWith("png")){
                    model.addAttribute("wallpaperFormat", "png");
                }
                else if(profileWallpaperPath.endsWith("jpg")){
                    model.addAttribute("wallpaperFormat", "jpg");
                }
                String wallpaper = getImageWithMediaType(profileWallpaperPath);
                model.addAttribute("profileWallpaper", wallpaper);
            }
            Relationship old_rel = relationshipService.findRelationship(getUser().getId(), userProfile.getId());
            model.addAttribute("relationship", old_rel);

            List<Rate> userRatings = rateService.getTop3UserRatings(userProfile.getId());
            model.addAttribute("ratings", userRatings);

            List<Relationship> rel_followers = relationshipService.findAllByFollowing(userProfile.getId());
            List<Relationship> rel_following = relationshipService.findAllByFollower(userProfile.getId());
            model.addAttribute("followers", rel_followers.size());
            model.addAttribute("following", rel_following.size());

            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }
        return "users/profile";
    }

    @PostMapping(value = "users/{id}/following")
    public String UserFollow(Model model, @PathVariable Long id) throws IOException {
        DbUser followUser = userService.getUser(id);
        if(followUser != null){
            Relationship old_rel = relationshipService.findRelationship(getUser().getId(), followUser.getId());
            if(old_rel == null){
                Relationship rel = new Relationship(getUser().getId(), followUser.getId(), LocalDateTime.now());
                relationshipService.updateRelationship(rel);
            }
            else{
                relationshipService.deleteRelationship(old_rel.getId());
            }
        }

        return "redirect:/users/" + id;
    }


    @GetMapping("user/edit")
    public String getUserProfileEdit(Model model) throws IOException {
        DbUser dbUser = getUser();
        DbUserDetails userDetails = dbUser.getDetails();
        UserDetailsModel userDetailsModel = new UserDetailsModel(dbUser.getId(), dbUser.getUsername(), userDetails.getAbout(), userDetails.getProfilePicture(), userDetails.getProfileWallpaper());

        String profilePicturePath = userDetails.getProfilePicture();
        if(profilePicturePath != null){
            if(profilePicturePath.endsWith("png")){
                model.addAttribute("picFormat", "png");
            }
            else if(profilePicturePath.endsWith("jpg")){
                model.addAttribute("picFormat", "jpg");
            }
            String content = getImageWithMediaType(profilePicturePath);
            model.addAttribute("profilePicture", content);
        }
        String profileWallpaperPath = userDetails.getProfileWallpaper();
        if(profileWallpaperPath != null){
            if(profileWallpaperPath.endsWith("png")){
                model.addAttribute("wallpaperFormat", "png");
            }
            else if(profileWallpaperPath.endsWith("jpg")){
                model.addAttribute("wallpaperFormat", "jpg");
            }
            String wallpaper = getImageWithMediaType(profileWallpaperPath);
            model.addAttribute("profileWallpaper", wallpaper);
        }

        model.addAttribute("userDetailsModel", userDetailsModel);

        return "users/profile-edit";
    }

    @PostMapping("user/edit")
    public String updateUserProfileEdit(Model model, @ModelAttribute UserDetailsModel userDetailsModel) throws IOException {
        DbUser dbUser = getUser();
        DbUserDetails userDetails = dbUser.getDetails();
        dbUser.setUsername(userDetailsModel.getUsername());
        userDetails.setAbout(userDetailsModel.getAbout());
        userService.updateUser(dbUser);
        userDetailsService.updateUserDetails(userDetails);

        model.addAttribute("message", "User info has been successfully updated!");

        String profilePicturePath = userDetails.getProfilePicture();
        if(profilePicturePath != null){
            if(profilePicturePath.endsWith("png")){
                model.addAttribute("picFormat", "png");
            }
            else if(profilePicturePath.endsWith("jpg")){
                model.addAttribute("picFormat", "jpg");
            }
            String content = getImageWithMediaType(profilePicturePath);
            model.addAttribute("profilePicture", content);
        }

//        return "users/profile-edit";
        return "redirect:/users/" + dbUser.getId();
    }

    @PostMapping("user/edit/photo")
    public String userImageUpload(@RequestParam("file") MultipartFile file,
                                  Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
//            return "users/profile-edit";
            return "redirect:/user/edit";
        }
        if(!file.getContentType().startsWith("image/")){
            model.addAttribute("error", "You can upload only image type file");
//            return "users/profile-edit";
            return "redirect:/user/edit";
        }

        DbUser user = getUser();
        DbUserDetails details = user.getDetails();

        if(details.getProfilePicture() != null){
            File f= new File(details.getProfilePicture());
            if(f.delete()){
                details.setProfilePicture(null);
                userDetailsService.updateUserDetails(details);
            }
        }

        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER_USERS + user.getId().toString() + "." + file.getContentType().replaceAll("image/", ""));
        Files.write(path, bytes);

        details.setProfilePicture(path.toString());
        userDetailsService.updateUserDetails(details);

        model.addAttribute("message",
                "You successfully uploaded '" + file.getOriginalFilename() + "'");

//        return "users/profile-edit";
        return "redirect:/user/edit";
    }

    @PostMapping("user/edit/wallpaper")
    public String userWallpaperUpload(@RequestParam("file") MultipartFile file,
                                  Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
            return "redirect:/user/edit";
        }
        if(!file.getContentType().startsWith("image/")){
            model.addAttribute("error", "You can upload only image type file");
            return "redirect:/user/edit";
        }

        DbUser user = getUser();

        byte[] bytes = file.getBytes();

        Path path = Paths.get(UPLOADED_FOLDER_WALLPAPERS + user.getId().toString() + "." + file.getContentType().replaceAll("image/", ""));
        Files.write(path, bytes);

        DbUserDetails details = user.getDetails();
        details.setProfileWallpaper(path.toString());
        userDetailsService.updateUserDetails(details);

        model.addAttribute("message",
                "You successfully uploaded '" + file.getOriginalFilename() + "'");

        return "redirect:/user/edit";
    }

    public String getImageWithMediaType(String path) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(content);
    }

    @PostMapping("user/delete/picture")
    public String userPictureDelete(Model model) throws IOException {
        DbUser user = getUser();
        DbUserDetails details = user.getDetails();

        if(!details.getProfilePicture().isEmpty()){
            File f= new File(details.getProfilePicture());
            if(f.delete()){
                details.setProfilePicture(null);
                userDetailsService.updateUserDetails(details);
            }
        }

        return "redirect:/user/edit";
    }

    @GetMapping("users/me")
    public String userRedirectProfile(Model model){
        return "redirect:/users/" + getUser().getId();
    }

    @GetMapping("users/me/creation")
    public String userRedirectRequests(Model model){
        return "redirect:/users/" + getUser().getId() + "/creation";
    }

    @GetMapping("users/{id}/creation")
    public String userRequestsPage(Model model, @PathVariable Long id){
        DbUser user = userService.getUser(id);
        if(user != null){
            if(user.getId() != getUser().getId()){
                return "redirect:/users/" + getUser().getId() + "/requests";
            }
            model.addAttribute("user", user);
        }
        return "users/user-creation";
    }

    @PostMapping("user/delete/wallpaper")
    public String userWallpaperDelete(Model model) throws IOException {
        DbUser user = getUser();
        DbUserDetails details = user.getDetails();

        if(!details.getProfileWallpaper().isEmpty()){
            File f= new File(details.getProfileWallpaper());
            if(f.delete()){
                details.setProfileWallpaper(null);
                userDetailsService.updateUserDetails(details);
            }
        }

        return "redirect:/user/edit";
    }

    @GetMapping(value = "users")
    public String UsersList(Model model,
                            @RequestParam(defaultValue = "0") Integer page,
                            @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {
        List<DbUser> users = userService.getUsers(page, pageSize);
        model.addAttribute("users", users);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("usersCount", userService.getUsersCount());

        List<PictureModel> pictureModels = new ArrayList<>();

        for(var d : users){
            String profilePicturePath = d.getDetails().getProfilePicture();
            String picFormat = null;
            String content = null;

            if(profilePicturePath != null){
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


        return "users/users-list";
    }

    @RequestMapping(value = "users")
    public String usersSearch(Model model, @Param("keyword") String keyword) throws IOException {
        if(keyword.replaceAll(" ", "").isEmpty()){
            return "redirect:/users";
        }

        List<DbUser> users = userService.findUsers(keyword);

        model.addAttribute("users", users);
        model.addAttribute("usersCount", userService.getUsersCount());

        List<PictureModel> pictureModels = new ArrayList<>();

        for(var d : users){
            String profilePicturePath = d.getDetails().getProfilePicture();
            String picFormat = null;
            String content = null;

            if(profilePicturePath != null){
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

        return "users/users-list";
    }

    @GetMapping(value = "users/{id}/following")
    public String UsersFollowing(Model model, @PathVariable Long id) throws IOException {
        DbUser profileUser = userService.getUser(id);

        if(profileUser != null){
            List<Relationship> rel_following = relationshipService.findAllByFollower(profileUser.getId());
            List<DbUser> followingUsers = new ArrayList<>();
            if(rel_following != null){
                for(var f : rel_following){
                    followingUsers.add(userService.getUser(f.getFollowingId()));
                }
            }

            model.addAttribute("users", followingUsers);

            List<PictureModel> pictureModels = new ArrayList<>();

            for(var d : followingUsers){
                String profilePicturePath = d.getDetails().getProfilePicture();
                String picFormat = null;
                String content = null;

                if(profilePicturePath != null){
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
        }

        return "users/following";
    }

    @GetMapping(value = "users/{id}/followers")
    public String UsersFollowers(Model model, @PathVariable Long id) throws IOException {
        DbUser profileUser = userService.getUser(id);

        if(profileUser != null) {
            List<Relationship> rel_followers = relationshipService.findAllByFollowing(profileUser.getId());
            List<DbUser> followersUsers = new ArrayList<>();
            if (rel_followers != null) {
                for (var f : rel_followers) {
                    followersUsers.add(userService.getUser(f.getFollowerId()));
                }
            }

            model.addAttribute("users", followersUsers);

            List<PictureModel> pictureModels = new ArrayList<>();

            for (var d : followersUsers) {
                String profilePicturePath = d.getDetails().getProfilePicture();
                String picFormat = null;
                String content = null;

                if (profilePicturePath != null) {
                    if (profilePicturePath.endsWith("png")) {
                        picFormat = "png";
                    } else if (profilePicturePath.endsWith("jpg")) {
                        picFormat = "jpg";
                    }
                    content = getImageWithMediaType(profilePicturePath);
                }
                pictureModels.add(new PictureModel(picFormat, content));
            }
            model.addAttribute("pictureModels", pictureModels);
        }

        return "users/followers";
    }

    @GetMapping("users/{id}/reviews")
    public String userReviews(Model model, @PathVariable Long id,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {

        DbUser user = userService.getUser(id);

        if(user != null){
            List<Rate> ratings = rateService.getAllUserRatings(user.getId(), page, pageSize);
            model.addAttribute("user", user);
            boolean is_user = false;
            if(user.getId() == getUser().getId()){
                is_user = true;
            }
            else{
                List<Role> roles = getUser().getRoles();
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
            }
            model.addAttribute("is_user", is_user);
            model.addAttribute("ratings", ratings);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("ratingsCount", rateService.getAllUserRatings(id).size());
            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }


        return "users/reviews";
    }

    @GetMapping("users/{id}/reviews-requests")
    public String userReviewRequests(Model model, @PathVariable Long id,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {

        DbUser user = userService.getUser(id);

        if(user != null){
            List<RateRequest> ratings = rateRequestService.getAllUserRatingsRequestNotApproved(user.getId(), false, page, pageSize);
            model.addAttribute("user", user);
            model.addAttribute("ratings", ratings);

            boolean is_user = false;
            if(user.getId() == getUser().getId()){
                is_user = true;
            }
            else{
                List<Role> roles = getUser().getRoles();
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
            }
            if(!is_user){
                return "redirect:/users/"+user.getId()+"/reviews";
            }

            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("ratingsCount", rateRequestService.getAllUserRatingsRequestNotApproved(user.getId(), false).size());
            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }


        return "users/reviews-requests";
    }

    @GetMapping("users/{id}/reviews-requests/declined")
    public String userReviewRequestsDeclined(Model model, @PathVariable Long id,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {

        DbUser user = userService.getUser(id);

        if(user != null){
            List<RateRequest> ratings = rateRequestService.getAllUserRatingsRequestNotApproved(user.getId(), true, page, pageSize);
            model.addAttribute("user", user);
            model.addAttribute("ratings", ratings);

            boolean is_user = false;
            if(user.getId() == getUser().getId()){
                is_user = true;
            }
            else{
                List<Role> roles = getUser().getRoles();
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
            }
            if(!is_user){
                return "redirect:/users/"+user.getId()+"/reviews";
            }

            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("ratingsCount", rateRequestService.getAllUserRatingsRequestNotApproved(user.getId(), true).size());
            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }


        return "users/reviews-requests-declined";
    }

    @GetMapping("users/{id}/books")
    public String userBooks(Model model, @PathVariable Long id,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "4") Integer pageSize) throws IOException {

        DbUser user = userService.getUser(id);

        if(user != null) {
            model.addAttribute("user", user);
            boolean is_user = false;
            if(user.getId() == getUser().getId()){
                is_user = true;
            }
            else{
                List<Role> roles = getUser().getRoles();
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
            }
            if(!is_user){
                return "redirect:/users/"+user.getId()+"/books";
            }

            List<Book> books = bookService.getAllUserBooks(user.getId(), page, pageSize);

            model.addAttribute("books", books);
            model.addAttribute("is_user", is_user);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("booksCount", bookService.getAllUserBooks(user.getId()).size());
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
        }


        return "users/books";
    }

    @GetMapping("users/{id}/books-requests")
    public String userBooksRequests(Model model, @PathVariable Long id,
                                @RequestParam(defaultValue = "0") Integer page,
                                @RequestParam(defaultValue = "4") Integer pageSize) throws IOException {

        DbUser user = userService.getUser(id);

        if(user != null) {
            model.addAttribute("user", user);
            boolean is_user = false;
            if(user.getId() == getUser().getId()){
                is_user = true;
            }
            else{
                List<Role> roles = getUser().getRoles();
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
            }
            if(!is_user){
                return "redirect:/users/"+user.getId()+"/books";
            }

            List<BookRequest> books = bookRequestService.getAllUserBooksRequestNotApproved(user.getId(),false, page, pageSize);

            model.addAttribute("books", books);
            model.addAttribute("is_user", is_user);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("booksCount", bookRequestService.getAllUserBooksRequestNotApproved(user.getId(), false).size());
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
        }


        return "users/book-requests";
    }

    @GetMapping("users/{id}/books-requests/declined")
    public String userBooksRequestsDeclined(Model model, @PathVariable Long id,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "4") Integer pageSize) throws IOException {

        DbUser user = userService.getUser(id);

        if(user != null) {
            model.addAttribute("user", user);
            boolean is_user = false;
            if(user.getId() == getUser().getId()){
                is_user = true;
            }
            else{
                List<Role> roles = getUser().getRoles();
                for(var r: roles){
                    if(Objects.equals(r.getRole(), "ROLE_MOD")){
                        is_user = true;
                        break;
                    }
                }
            }
            if(!is_user){
                return "redirect:/users/"+user.getId()+"/books";
            }

            List<BookRequest> books = bookRequestService.getAllUserBooksRequestNotApproved(user.getId(),true, page, pageSize);

            model.addAttribute("books", books);
            model.addAttribute("is_user", is_user);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("booksCount", bookRequestService.getAllUserBooksRequestNotApproved(user.getId(), false).size());
            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }


        return "users/book-requests-declined";
    }

    @GetMapping("users/users-activity")
    public String usersActivity(Model model){
        DbUser user = getUser();
        if(user != null){
            List<Relationship> relationships = relationshipService.findAllByFollower(user.getId());
            List<DbUser> following = new ArrayList<>();
            List<Rate> following_ratings = new ArrayList<>();

            for(var r: relationships){
                DbUser ruser = userService.getUser(r.getFollowingId());

                for(var u: rateService.getAllUserRatings(ruser.getId())){
                    if(u.getCreated_at().isAfter(r.getCreated_at())){
                        following_ratings.add(u);
                    }
                }
            }
            following_ratings.sort(Comparator.comparing(BaseEntity::getId).reversed());
            model.addAttribute("user", user);
            model.addAttribute("ratings", following_ratings);
            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));

        }

        return "users/users-activity";
    }

    @GetMapping("users/users-activity/books")
    public String usersActivityBooks(Model model) throws IOException {
        DbUser user = getUser();
        if(user != null){
            List<Relationship> relationships = relationshipService.findAllByFollower(user.getId());
            List<Book> following_books = new ArrayList<>();

            for(var r: relationships){
                DbUser ruser = userService.getUser(r.getFollowingId());

                for(var b: bookService.getAllUserBooks(ruser.getId())){
                    if(b.getCreated_at().isAfter(r.getCreated_at())){
                        following_books.add(b);
                    }
                }
            }
            following_books.sort(Comparator.comparing(BaseEntity::getId).reversed());

            List<PictureModel> pictureModels = new ArrayList<>();

            for(var d : following_books){
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

            model.addAttribute("user", user);
            model.addAttribute("books", following_books);
            model.addAttribute("dateFormat", DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));

        }

        return "users/users-activity-books";
    }

    @PostMapping(value = "books/{id}/add/favorites")
    public String addFavoriteBook(Model model, @PathVariable Long id){
        DbUser user = getUser();
        Book book = bookService.getBook(id);
        if(book != null){
            if(user != null){
                FavoriteBook favoriteBook = favoriteBookService.getFavoriteBookByUserAndBookId(user.getId(), book.getId());
                if(favoriteBook == null){
                    FavoriteBook new_favorite = new FavoriteBook(user, book, LocalDateTime.now());
                    favoriteBookService.updateFavorite(new_favorite);
                }
                else{
                    favoriteBookService.removeFavoriteBook(favoriteBook.getId());
                }
                return "redirect:/books/" + book.getId();
            }
        }

        return "redirect:/books/"+id;
    }



//    @GetMapping("users/settings")
//    public String userSettings(Model model) throws IOException {
//        DbUser user = getUser();
//
//        if(user != null) {
//            model.addAttribute("user", user);
//            model.addAttribute("userModel", new PasswordChangeModel());
//        }
//        return "users/settings";
//    }

//    @RequestMapping("users/settings/update/password")
//    public String userUpdatePassword(@ModelAttribute PasswordChangeModel userModel) throws IOException {
//        DbUser user = getUser();
//
//        if(user != null) {
//            if(userService.comparePasswords(user.getId(), userModel.getOldPassword())){
//                return "redirect:/home/";
//            }
//        }
//        return "users/settings";
//    }

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