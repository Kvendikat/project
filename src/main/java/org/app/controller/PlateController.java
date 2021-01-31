package org.app.controller;

import org.app.entity.*;
import org.app.repository.MaterialRepository;
import org.app.repository.PlateRepository;
import org.app.service.impl.PaginationService;
import org.app.service.impl.ValidationMessagesService;
import org.app.repository.TypeRepository;
import org.app.service.impl.PlateService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("findingPlates")
@RequestMapping("/plate")
public class PlateController {

    @Autowired
    private PlateRepository plateRepository;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private PlateService plateService;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    private String findAllNormalPlates(
            Model model
    ) {
        List<Plate> plates = plateRepository.findAllByInStorageTrueAndCrashFalse();
        model.addAttribute("plates", plates);

        return "plate/start";
    }

    @GetMapping("/add")
    private String addingNewPlate(Model model) {
        Plate newPlate = new Plate();
        List<Type> types = typeRepository.findAllByActiveIsTrueOrderByName();
        List<Material> materials = materialRepository.findAllByActiveIsTrueOrderById();

        model.addAttribute("newPlate", newPlate);
        model.addAttribute("types", types);
        model.addAttribute("materials", materials);
        model.addAttribute("errors", new HashMap<>());

        return "plate/add";
    }

    @PostMapping("/add")
    private String addNewPlate(
            @Valid @ModelAttribute Plate newPlate,
            BindingResult bindingResult,
            Model model
    ) {
        Plate samePlate = plateRepository.findByNumber(newPlate.getNumber());
        if (samePlate != null) {
            bindingResult.addError(
                    new FieldError("plate", "number", "Такой номер уже существует")
            );
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            validationMessagesService.createValidationMessages(bindingResult, errors);
            List<Type> types = typeRepository.findAllByActiveIsTrueOrderByName();
            List<Material> materials = materialRepository.findAllByActiveIsTrueOrderById();

            model.addAttribute("errors", errors);
            model.addAttribute("newPlate", newPlate);
            model.addAttribute("types", types);
            model.addAttribute("materials", materials);

            return "plate/add";
        }

        plateRepository.saveAndFlush(newPlate);

        return "redirect:/plate/add";
    }

    @GetMapping("/edit/{id}")
    private String editPlate(
            @PathVariable(name = "id") Plate plate,
            Model model
    ) {
        Map<String, List<String>> errors = new HashMap<>();
        List<Type> types = typeRepository.findAllByActiveIsTrueOrderByName();
        List<Material> materials = materialRepository.findAllByActiveIsTrueOrderById();

        model.addAttribute("plate", plate);
        model.addAttribute("types", types);
        model.addAttribute("materials", materials);
        model.addAttribute("errors", errors);

        return "plate/edit";
    }

    @PostMapping("/save/{id}")
    private String saveEditedPlate(
            @PathVariable(name = "id") Plate plate,
            @Valid @ModelAttribute Plate editedPlate,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();
            validationMessagesService.createValidationMessages(bindingResult, errors);

            List<Type> types = typeRepository.findAllByActiveIsTrueOrderByName();
            List<Material> materials = materialRepository.findAllByActiveIsTrueOrderById();

            model.addAttribute("errors", errors);
            model.addAttribute("plate", editedPlate);
            model.addAttribute("types", types);
            model.addAttribute("materials", materials);

            return "plate/edit";
        }
        plate.setId(editedPlate.getId());
        plate.setNumber(editedPlate.getNumber());
        plate.setLength(editedPlate.getLength());
        plate.setWidth(editedPlate.getWidth());
        plate.setHeight(editedPlate.getHeight());
        plate.setParamOne(editedPlate.getParamOne());
        plate.setParamTwo(editedPlate.getParamTwo());
        plate.setHasCrack(editedPlate.getHasCrack());
        plate.setMaterial(editedPlate.getMaterial());
        plate.setNote(editedPlate.getNote());
        plate.setInStorage(editedPlate.getInStorage());
        plate.setWhatFor(editedPlate.getWhatFor());
        plate.setType(editedPlate.getType());

        plateRepository.save(plate);
        plateRepository.flush();

        return "redirect:/plate/start";
    }

    // Архив
    @GetMapping("/archive")
    private String findAllNotInStoragePlates(
            @PageableDefault(value = 10) Pageable pageable,
            Model model
    ) {
        Page<Plate> plates = plateRepository.findAllByInStorageFalseAndCrashFalse(pageable);
        paginationService.addToModelWithPagination(model, plates, pageable);
        model.addAttribute("plates", plates);

        return "plate/archive";
    }

    @GetMapping("/push-in-archive/{plateId}")
    private String archivePlate(
            @PathVariable(name = "plateId") Plate plate
    ) {
        if (plate == null) {
            throw new RuntimeException("Такой заготовки не найдено");
        }

        plate.setArchive(true);

        plateRepository.save(plate);
        plateRepository.flush();

        return "redirect:/plate/archive/";
    }

    @GetMapping("/return-in-main/{plateId}")
    private String returnPlateFromArchive(
            @PathVariable(name = "plateId") Plate plate
    ) {
        if (plate == null) {
            throw new RuntimeException("Такой заготовки не найдено");
        }

        plate.setInStorage(true);

        plateRepository.save(plate);
        plateRepository.flush();

        return "redirect:/plate/start/";
    }

    // Изолятор брака
    @GetMapping("/crash")
    private String findAllCrashedPlates(
            @PageableDefault(value = 10) Pageable pageable,
            Model model
    ) {
        Page<Plate> plates = plateRepository.findAllByCrashTrue(pageable);
        paginationService.addToModelWithPagination(model, plates, pageable);
        model.addAttribute("plates", plates);

        return "plate/crash";
    }

    @GetMapping("/delete/{plateId}")
    private String deletePlate(
            @PathVariable(name = "plateId") Plate plate
    ) {
        if (plate == null) {
            throw new RuntimeException("Такой заготовки не найдено");
        }

        plate.setCrash(true);

        plateRepository.save(plate);
        plateRepository.flush();

        return "redirect:/plate/crash/";
    }

    @GetMapping("/return/{plateId}")
    private String returnPlate(
            @PathVariable(name = "plateId") Plate plate
    ) {
        if (plate == null) {
            throw new RuntimeException("Такой заготовки не найдено");
        }

        plate.setCrash(false);
        plate.setProtocol(null);

        plateRepository.save(plate);
        plateRepository.flush();

        return "redirect:/plate/start/";
    }

    @GetMapping("/find")
    private String findPlates(
    ) {
        return "/plate/find";
    }

    @PostMapping("/find")
    private String findingPlates(
            @RequestParam(name = "findingPlate") String findingPlate,
            Model model) {
        List<Plate> plates = plateRepository.findAllByNumberContainingOrderByNumber(findingPlate);

        model.addAttribute("findingPlates", plates);

        return "redirect:/plate/found";
    }

    @GetMapping("/found")
    private String foundPlates() {
        return "/plate/found";
    }

    @PostMapping("/what-for/{protocolId}/{plateId}")
    private String whatFor(
            @PathVariable(name = "plateId") Plate plate,
            @PathVariable(name = "protocolId") Protocol protocol,
            @RequestParam(name = "whatFor") WhatFor whatFor
            ) {
        if (whatFor != null) {
            plate.setWhatFor(whatFor);
        }
        plateRepository.saveAndFlush(plate);

        return "redirect:/protocol/edit/" + protocol.getId();
    }

    @GetMapping("/find-for-date-interval")
    private String findPlatesForDateInterval (
    ) {
        return "plate/find-for-date-interval";
    }

    @PostMapping("/find-for-date-interval")
    private String findingPlatesForDateInterval(
            @RequestParam(name = "startDate") Date startDate,
            @RequestParam(name = "endDate`") Date endDate,
            Model model) {

        List<Plate> plates = plateService.findPlatesInDateInterval(startDate, endDate);

        for (Plate plate : plates) {
            System.out.println(plate.getNumber());
        }

        if (plates.size() == 0) {
            throw new RuntimeException("Заготовки с таким номером не найдено");
        }

        model.addAttribute("findingPlates", plates);

        return "redirect:/plate/found-for-date-interval";
    }
}
