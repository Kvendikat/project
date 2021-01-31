package org.app.controller.admin;

import org.app.entity.Producer;
import org.app.repository.ProducerRepository;
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
@RequestMapping("/admin/producer")
public class ProducerController {

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    public String findAllActiveMaterials(
            @PageableDefault(value = 10, sort = "name") Pageable pageable,
            Model model
    ) {
        Page<Producer> producers = producerRepository.findAllByActiveIsTrue(pageable);
        paginationService.addToModelWithPagination(model, producers, pageable);

        return "admin/producer/start";
    }

    @GetMapping("/archive")
    public String findAllArchiveIngredients(
            @PageableDefault(value = 10, sort = "name") Pageable pageable,
            Model model
    ) {
        Page<Producer> producers = producerRepository.findAllByActiveIsFalse(pageable);
        paginationService.addToModelWithPagination(model, producers, pageable);

        return "admin/producer/archive";
    }

    @GetMapping("/add")
    public String adding(Model model) {
        Producer newProducer = new Producer();
        model.addAttribute("newProducer", newProducer);
        model.addAttribute("errors", new HashMap<>());

        return "admin/producer/add";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute Producer newProducer,
            BindingResult bindingResult,
            Model model
    ) throws Exception {
        Producer sameProducer = producerRepository.findByName(newProducer.getName());
        if (sameProducer != null) {
            bindingResult.addError(
                    new FieldError("producer", "name", "Такое имя уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            validationMessagesService.createValidationMessages(bindingResult, errors);

            model.addAttribute("errors", errors);
            model.addAttribute("newProducer", newProducer);

            return "admin/producer/add";
        }

        producerRepository.save(newProducer);
        producerRepository.flush();

        return "redirect:/admin/producer/start";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable(name = "id") Producer producer,
            Model model
    ) {
        model.addAttribute("producer", producer);
        model.addAttribute("errors", new HashMap<>());

        return "admin/producer/edit";
    }

    @PostMapping("/save/{id}")
    public String save(
            @PathVariable(name = "id") Producer producer,
            @Valid @ModelAttribute Producer editedProducer,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();
            validationMessagesService.createValidationMessages(bindingResult, errors);

            model.addAttribute("errors", errors);
            model.addAttribute("producer", editedProducer);

            return "admin/producer/edit";
        }

        producer.setId(editedProducer.getId());
        producer.setName(editedProducer.getName());
        producer.setSurname(editedProducer.getSurname());
        producer.setPosition(editedProducer.getPosition());
        producer.setActive(editedProducer.getActive());

        producerRepository.save(producer);
        producerRepository.flush();

        return "redirect:/admin/producer/start";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Producer producer
    ) {
        if (producer == null) {
            throw new RuntimeException("Такого изготовителя не найдено");
        }

        producer.setActive(false);

        producerRepository.save(producer);
        producerRepository.flush();

        return "redirect:/admin/producer/start";
    }

    @GetMapping("/return/{id}")
    public String retur(@PathVariable(name = "id") Producer producer
    ) {
        if (producer == null) {
            throw new RuntimeException("Такого изготовителя не найдено");
        }

        producer.setActive(true);

        producerRepository.save(producer);
        producerRepository.flush();

        return "redirect:/admin/producer/archive";
    }

    @GetMapping("/finish-delete/{id}")
    public String finishDelete(@PathVariable(name = "id") Producer producer
    ) {
        if (producer == null) {
            throw new RuntimeException("Такого изготовителя не найдено");
        }

        producerRepository.delete(producer);
        producerRepository.flush();

        return "redirect:/admin/producer/archive";
    }
}
