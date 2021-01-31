package org.app.controller.admin;

import org.app.entity.WhatFor;
import org.app.repository.WhatForRepository;
import org.app.service.impl.PaginationService;
import org.app.service.impl.ValidationMessagesService;
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
@RequestMapping("/admin/what-for")
public class WhatForController {

    @Autowired
    private WhatForRepository whatForRepository;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    public String findAllActiveWhatFor(
            @PageableDefault(value = 10, sort = "type") Pageable pageable,
            Model model
    ) {
        Page<WhatFor> whatFors = whatForRepository.findAllByActiveTrue(pageable);
        paginationService.addToModelWithPagination(model, whatFors, pageable);

        return "admin/what-for/start";
    }

    @GetMapping("/archive")
    public String findAllArchiveWhatFor(
            @PageableDefault(value = 10, sort = "type") Pageable pageable,
            Model model
    ) {
        Page<WhatFor> whatFors = whatForRepository.findAllByActiveFalse(pageable);
        paginationService.addToModelWithPagination(model, whatFors, pageable);

        return "admin/what-for/archive";
    }

    @GetMapping("/add")
    public String adding(Model model) {
        WhatFor newWhatFor = new WhatFor();
        model.addAttribute("newWhatFor", newWhatFor);
        model.addAttribute("errors", new HashMap<>());

        return "admin/what-for/add";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute WhatFor newWhatFor,
            BindingResult bindingResult,
            Model model
    ) {
        WhatFor sameWhatFor = whatForRepository.findByType(newWhatFor.getType());
        if (sameWhatFor != null) {
            bindingResult.addError(
                    new FieldError("whatFor", "type", "Такое имя уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            validationMessagesService.createValidationMessages(bindingResult, errors);

            model.addAttribute("errors", errors);
            model.addAttribute("newWhatFor", newWhatFor);

            return "admin/what-for/add";
        }

        whatForRepository.saveAndFlush(newWhatFor);

        return "redirect:/admin/what-for/start";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") WhatFor whatFor,
            Model model
    ) {
        model.addAttribute("whatFor", whatFor);
        model.addAttribute("errors", new HashMap<>());

        return "admin/what-for/edit";
    }

    @PostMapping("/save/{id}")
    public String save(
            @PathVariable(name = "id") WhatFor whatFor,
            @Valid @ModelAttribute WhatFor editedWhatFor,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();
            validationMessagesService.createValidationMessages(bindingResult, errors);

            model.addAttribute("errors", errors);
            model.addAttribute("whatFor", editedWhatFor);

            return "admin/what-for/edit";
        }
        whatFor.setId(editedWhatFor.getId());
        whatFor.setType(editedWhatFor.getType());
        whatFor.setQuantity(editedWhatFor.getQuantity());
        whatFor.setActive(editedWhatFor.getActive());

        whatForRepository.saveAndFlush(whatFor);

        return "redirect:/admin/what-for/start";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") WhatFor whatFor
    ) {
        if (whatFor == null) {
            throw new RuntimeException("Такого применения не найдено");
        }

        whatFor.setActive(false);

        whatForRepository.saveAndFlush(whatFor);

        return "redirect:/admin/what-for/start";
    }

    @GetMapping("/return/{id}")
    public String retur(@PathVariable(name = "id") WhatFor whatFor
    ) {
        if (whatFor == null) {
            throw new RuntimeException("Такого применения не найдено");
        }

        whatFor.setActive(true);

        whatForRepository.saveAndFlush(whatFor);

        return "redirect:/admin/what-for/archive";
    }

    @GetMapping("/finish-delete/{id}")
    public String finishDelete(@PathVariable(name = "id") WhatFor whatFor
    ) {
        if (whatFor == null) {
            throw new RuntimeException("Такого применения не найдено");
        }

        whatForRepository.delete(whatFor);
        whatForRepository.flush();

        return "redirect:/admin/what-for/archive";
    }
}
