package org.recognition.controller;

import org.recognition.entity.UserEntity;
import org.recognition.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(Model model) {
        //model.addAttribute("userForm", new UserEntity());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") UserEntity userForm, Model model) {
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            System.out.println(userForm.getPassword() + " " + userForm.getPasswordConfirm());
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }

        return "redirect:/";
    }

//    @RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
//    public ModelAndView loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
//        System.out.println(SecurityContextHolder.getContext());
//        System.out.println(error);
//        return new ModelAndView("login");
//    }
}
