package org.app.controller.admin;

import org.app.entity.User;
import org.app.enums.Role;
import org.app.repository.UserRepository;
import org.app.service.impl.PaginationService;
import org.app.service.impl.ValidationMessagesService;
import org.app.service.impl.impl.MyBCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyBCryptPasswordEncoder myBCryptPasswordEncoder;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    public String findAllActiveUsers(
            @PageableDefault(value = 5, sort = "username") Pageable pageable,
            Model model
    ) {
        Page<User> users = userRepository.findAllByActiveIsTrue(pageable);
        paginationService.addToModelWithPagination(model, users, pageable);

        return "admin/user/start";
    }

    @GetMapping("/archive")
    public String findAllArchiveUsers(
            @PageableDefault(value = 4, sort = "username") Pageable pageable,
            Model model
    ) {
        Page<User> users = userRepository.findAllByActiveIsFalse(pageable);
        paginationService.addToModelWithPagination(model, users, pageable);

        return "admin/user/archive";
    }

    @GetMapping("/add")
    public String adding(Model model){
        User newUser = new User();
        newUser.getRoles().add(Role.ROLE_USER);
        model.addAttribute("newUser", newUser);
        model.addAttribute("roles", Role.values());
        model.addAttribute("errors", new HashMap<>());

        return "admin/user/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute User newUser, BindingResult bindingResult, Model model) throws Exception {
        User sameName = userRepository.findByUsername(newUser.getUsername());
        if (sameName != null) {
            bindingResult.addError(
                    new FieldError("user", "username", "Пользователь с таким именем уже существует")
            );
        }

        User sameMail = userRepository.findByLogin(newUser.getLogin());
        if (sameMail != null) {
            bindingResult.addError(
                    new FieldError("user", "login", "Пользователь с таким Логином электронной почты уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();
            validationMessagesService.createValidationMessages(bindingResult, errors);

            newUser.getRoles().add(Role.ROLE_USER);

            model.addAttribute("errors", errors);
            model.addAttribute("newUser", newUser);
            model.addAttribute("roles", Role.values());

            return "admin/user/add";
        }

        newUser.getRoles().add(Role.ROLE_USER);
        newUser.setPassword(
                myBCryptPasswordEncoder.encode(newUser.getPassword())
        );

        userRepository.save(newUser);
        userRepository.flush();

        return "redirect:/admin/user/start";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") User user,
            Model model
    ) {
        user.getRoles().add(Role.ROLE_USER);
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("errors", new HashMap<>());

        return "admin/user/edit";
    }

    @PostMapping("/edit/{id}")
    public String save(
            @PathVariable(name = "id") User user,
            @Valid @ModelAttribute User editedUser,
            BindingResult bindingResult,
            Model model
    ) {
        User sameMail = userRepository.findByLogin(editedUser.getLogin());
        if (sameMail != null && !user.getLogin().equals(editedUser.getLogin())) {
            bindingResult.addError(
                    new FieldError("editedUser", "login", "Пользователь с таким логином уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();
            validationMessagesService.createValidationMessages(bindingResult, errors);

            checkAndCreatenewUser(user, editedUser);

            model.addAttribute("errors", errors);
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("user", editedUser);
            return "admin/user/edit";
        }

        checkAndCreatenewUser(user, editedUser);
        user.setUsername(editedUser.getUsername());
        user.setLogin(editedUser.getLogin());
        user.setPassword(editedUser.getPassword());
        user.setActive(editedUser.getActive());
        user.setPosition(editedUser.getPosition());

        for (Role role : editedUser.getRoles()) {
            user.getRoles().add(role);
        }
        user.getRoles().add(Role.ROLE_USER);

        userRepository.save(user);
        userRepository.flush();

        return "redirect:/admin/user/start";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") User user
    ) {
        if (user == null) {
            throw new RuntimeException("Такого пользователя не найдено");
        }

        user.setActive(false);

        userRepository.save(user);
        userRepository.flush();

        return "redirect:/admin/user/start";
    }

    @GetMapping("/return/{id}")
    public String retur(@PathVariable(name = "id") User user
    ) {
        if (user == null) {
            throw new RuntimeException("Такого пользователя не найдено");
        }

        user.setActive(true);

        userRepository.save(user);
        userRepository.flush();

        return "redirect:/admin/user/archive";
    }

    @GetMapping("/finish-delete/{id}")
    public String finishDelete(@PathVariable(name = "id") User user
    ) {
        if (user == null) {
            throw new RuntimeException("Такого пользователя не найдено");
        }

        userRepository.delete(user);
        userRepository.flush();

        return "redirect:/admin/user/archive";
    }

    private void checkAndCreatenewUser(User user, User editedUser) {
        if (editedUser.getUsername().equals("admin")) {
            editedUser.getRoles().add(Role.ROLE_ADMIN);
        }

        user.setUsername(editedUser.getUsername());
        user.setPassword(editedUser.getPassword());
        user.setLogin(editedUser.getLogin());
        user.getRoles().clear();
        user.setPosition(editedUser.getPosition());
        for (Role role : editedUser.getRoles()) {
            user.getRoles().add(role);
        }
        user.getRoles().add(Role.ROLE_USER);
    }
}
