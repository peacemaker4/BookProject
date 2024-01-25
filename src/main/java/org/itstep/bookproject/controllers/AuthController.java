package org.itstep.bookproject.controllers;

import org.itstep.bookproject.configs.StaticConfig;
import org.itstep.bookproject.entities.DbUser;
import org.itstep.bookproject.entities.DbUserDetails;
import org.itstep.bookproject.entities.Role;
import org.itstep.bookproject.models.UserModel;
import org.itstep.bookproject.services.UserDetailsService;
import org.itstep.bookproject.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    public AuthController(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping(value = "login")
    public String loginPage(){
        return "auth/login";
    }

    @GetMapping(value = "register")
    public String registerPage(Model model) {
        model.addAttribute("userModel", new UserModel());
        return "auth/register";
    }

    @PostMapping(value="register")
    public String registerUser(@ModelAttribute UserModel userModel){
        if(userModel.getPassword().equals(userModel.getConfirmPassword()) && userService.getUser(userModel.getEmail())==null && userService.getUser(userModel.getUsername())==null){
            List<Role> roles = new ArrayList<Role>();
            roles.add(StaticConfig.ROLE_USER);
            var dbUserDetails = userDetailsService.updateUserDetails(new DbUserDetails());
            var dbUser = new DbUser(userModel.getEmail(), userModel.getUsername(), userModel.getPassword(), roles, dbUserDetails);
            userService.registerUser(dbUser);
            return "redirect:/login";
        }
        else if(userService.getUser(userModel.getUsername()) != null){
            return "redirect:/register?error=3";
        }
        else if(userService.getUser(userModel.getEmail()) != null){
            return "redirect:/register?error=2";
        }
        else{
            return "redirect:/register?error=1";
        }
    }
}
