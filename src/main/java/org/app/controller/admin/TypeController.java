package org.app.controller.admin;

import org.app.repository.TypeRepository;
import org.app.service.impl.PaginationService;
import org.app.service.impl.ValidationMessagesService;
import org.app.entity.Type;
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
@RequestMapping("/admin/type")
public class TypeController {

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    public String findAllActiveTypes(
            @PageableDefault(value = 10, sort = "name") Pageable pageable,
            Model model
    ) {
        Page<Type> types = typeRepository.findAllByActiveIsTrue(pageable);
        paginationService.addToModelWithPagination(model, types, pageable);

        return "admin/type/start";
    }

    @GetMapping("/archive")
    public String findAllArchiveIngredients(
            @PageableDefault(value = 10, sort = "name")Pageable pageable,
            Model model
    ) {
        Page<Type> types = typeRepository.findAllByActiveIsFalse(pageable);
        paginationService.addToModelWithPagination(model, types, pageable);

        return "admin/type/archive";
    }

    @GetMapping("/add")
    public String adding(Model model){
        Type newIngredient = new Type();
        model.addAttribute("newType", newIngredient);
        model.addAttribute("errors", new HashMap<>());

        return "admin/type/add";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute Type newType,
            BindingResult bindingResult,
            Model model
    ) throws Exception {
        Type sameType = typeRepository.findByName(newType.getName());
        if (sameType != null) {
            bindingResult.addError(
                    new FieldError("type", "name", "Такое имя уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            validationMessagesService.createValidationMessages(bindingResult, errors);

            model.addAttribute("errors", errors);
            model.addAttribute("newType", newType);

            return "admin/type/add";
        }

        typeRepository.save(newType);
        typeRepository.flush();

        return "redirect:/admin/type/start";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") Type type,
            Model model
    ) {
        model.addAttribute("type", type);

        return "admin/type/edit";
    }

    @PostMapping("/save/{id}")
    public String save(
            @PathVariable(name = "id") Type type,
            @ModelAttribute Type editedType
    ) {
        type.setName(type.getName());
        type.setActive(type.getActive());

        typeRepository.save(type);
        typeRepository.flush();

        return "redirect:/admin/type/edit/" + type.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Type type
    ) {
        if (type == null) {
            throw new RuntimeException("Такого материала не найдено");
        }

        type.setActive(false);

        typeRepository.save(type);
        typeRepository.flush();

        return "redirect:/admin/type/start";
    }

    @GetMapping("/return/{id}")
    public String retur(@PathVariable(name = "id") Type type
    ) {
        if (type == null) {
            throw new RuntimeException("Такого материала не найдено");
        }

        type.setActive(true);

        typeRepository.save(type);
        typeRepository.flush();

        return "redirect:/admin/type/archive";
    }

    @GetMapping("/finish-delete/{id}")
    public String finishDelete(@PathVariable(name = "id") Type type
    ) {
        if (type == null) {
            throw new RuntimeException("Такого материала не найдено");
        }

        typeRepository.delete(type);
        typeRepository.flush();

        return "redirect:/admin/type/archive";
    }
}

