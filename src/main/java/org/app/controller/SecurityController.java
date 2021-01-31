package org.app.controller;

import org.app.repository.UserRepository;
import org.app.service.impl.impl.MyBCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    @GetMapping("/login")
    public String login(@RequestParam(defaultValue = "false") String error, Model model) {
        model.addAttribute("error", error.equalsIgnoreCase("true"));

        return "login";
    }
}
