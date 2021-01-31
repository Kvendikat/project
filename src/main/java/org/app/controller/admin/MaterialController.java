package org.app.controller.admin;

import org.app.entity.Material;
import org.app.repository.MaterialRepository;
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
@RequestMapping("/admin/material")
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    public String findAllActiveMaterials(
            @PageableDefault(value = 10, sort = "name") Pageable pageable,
            Model model
    ) {
        Page<Material> materials = materialRepository.findAllByActiveIsTrue(pageable);
        paginationService.addToModelWithPagination(model, materials, pageable);

        return "admin/material/start";
    }

    @GetMapping("/archive")
    public String findAllArchiveIngredients(
            @PageableDefault(value = 10, sort = "name")Pageable pageable,
            Model model
    ) {
        Page<Material> materials = materialRepository.findAllByActiveIsFalse(pageable);
        paginationService.addToModelWithPagination(model, materials, pageable);

        return "admin/material/archive";
    }

    @GetMapping("/add")
    public String adding(Model model){
        Material newIngredient = new Material();
        model.addAttribute("newMaterial", newIngredient);
        model.addAttribute("errors", new HashMap<>());

        return "admin/material/add";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute Material newMaterial,
            BindingResult bindingResult,
            Model model
    ) throws Exception {
        Material sameMaterial = materialRepository.findByName(newMaterial.getName());
        if (sameMaterial != null) {
            bindingResult.addError(
                    new FieldError("material", "name", "Такое имя уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            validationMessagesService.createValidationMessages(bindingResult, errors);

            model.addAttribute("errors", errors);
            model.addAttribute("newMaterial", newMaterial);

            return "admin/material/add";
        }

        materialRepository.save(newMaterial);
        materialRepository.flush();

        return "redirect:/admin/material/start";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") Material material,
            Model model
    ) {
        model.addAttribute("material", material);

        return "admin/material/edit";
    }

    @PostMapping("/save/{id}")
    public String save(
            @PathVariable(name = "id") Material material,
            @ModelAttribute Material editedMaterial
    ) {
        material.setName(material.getName());
        material.setActive(material.getActive());

        materialRepository.save(material);
        materialRepository.flush();

        return "redirect:/admin/material/edit/" + material.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Material material
    ) {
        if (material == null) {
            throw new RuntimeException("Такого материала не найдено");
        }

        material.setActive(false);

        materialRepository.save(material);
        materialRepository.flush();

        return "redirect:/admin/material/start";
    }

    @GetMapping("/return/{id}")
    public String retur(@PathVariable(name = "id") Material material
    ) {
        if (material == null) {
            throw new RuntimeException("Такого материала не найдено");
        }

        material.setActive(true);

        materialRepository.save(material);
        materialRepository.flush();

        return "redirect:/admin/material/archive";
    }

    @GetMapping("/finish-delete/{id}")
    public String finishDelete(@PathVariable(name = "id") Material material
    ) {
        if (material == null) {
            throw new RuntimeException("Такого материала не найдено");
        }

        materialRepository.delete(material);
        materialRepository.flush();

        return "redirect:/admin/material/archive";
    }
}
