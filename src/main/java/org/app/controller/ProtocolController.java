package org.app.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.app.entity.*;
import org.app.repository.*;
import org.app.service.impl.ExcelService;
import org.app.service.impl.PaginationService;
import org.app.service.impl.ProtocolService;
import org.app.service.impl.ValidationMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("findingPlates")
@RequestMapping("/protocol")
public class ProtocolController {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private DepartmentBossRepository departmentBossRepository;

    @Autowired
    private LaboratoryBossRepository laboratoryBossRepository;

    @Autowired
    private PlateRepository plateRepository;

    @Autowired
    private WhatForRepository whatForRepository;

    @Autowired
    private ValidationMessagesService validationMessagesService;

    @GetMapping("/start")
    private String findAllProtocols(
            @PageableDefault(value = 10, sort = "date", direction = Sort.Direction.DESC)Pageable pageable,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "login";
        }
        Page<Protocol> protocols = protocolRepository.findAll(pageable);
        paginationService.addToModelWithPagination(model, protocols, pageable);
        model.addAttribute("currentUsername", principal.getName());
        addPopoverWithPlates(model);

        return "protocol/start";
    }

    @GetMapping("/add")
    private String addingNewProtocol(Principal principal,
           Model model
   ) {
        if (principal == null) {
            return "login";
        }
        Protocol newProtocol = new Protocol();
        newProtocol.setDate(new Date());
        newProtocol.setNumber(protocolService.createNewNumber());
        newProtocol.setUser(userRepository.findByUsername(principal.getName()));

        List<LaboratoryBoss> laboratoryBosses = laboratoryBossRepository.findAll(Sort.by("name"));
        List<DepartmentBoss> departmentBosses = departmentBossRepository.findAll(Sort.by("name"));
        List<Producer> producers = producerRepository.findAll(Sort.by("name"));

        model.addAttribute("newProtocol", newProtocol);
        model.addAttribute("laboratoryBosses", laboratoryBosses);
        model.addAttribute("departmentBosses", departmentBosses);
        model.addAttribute("producers", producers);
        model.addAttribute("errors", new HashMap<>());

        return "protocol/add";
    }

    @PostMapping("/add")
    private String addNewProtocol(
            @Valid @ModelAttribute Protocol newProtocol,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            validationMessagesService.createValidationMessages(bindingResult, errors);
            List<LaboratoryBoss> laboratoryBosses = laboratoryBossRepository.findAllByActiveTrueOrderByName();
            List<DepartmentBoss> departmentBosses = departmentBossRepository.findAllByActiveTrueOrderByName();
            List<Producer> producers = producerRepository.findAll(Sort.by("name"));

            model.addAttribute("errors", errors);
            model.addAttribute("newProtocol", newProtocol);
            model.addAttribute("laboratoryBosses", laboratoryBosses);
            model.addAttribute("departmentBosses", departmentBosses);
            model.addAttribute("producers", producers);

            return "protocol/add";
        }

        newProtocol.setDate(new Date());
        newProtocol.setNumber(protocolService.createNewNumber());
        newProtocol.setUser(userRepository.findByUsername(principal.getName()));

        protocolRepository.save(newProtocol);
        protocolRepository.flush();

        return "redirect:/protocol/start";
    }

    @GetMapping("/edit/{protocolId}")
    private String edit(@PathVariable(name = "protocolId") Protocol protocol,
            Model model
    ) {
        List<LaboratoryBoss> laboratoryBosses = laboratoryBossRepository.findAllByActiveTrueOrderByName();
        List<DepartmentBoss> departmentBosses = departmentBossRepository.findAllByActiveTrueOrderByName();
        List<Producer> producers = producerRepository.findAll(Sort.by("name"));
        List<WhatFor> whatFors = whatForRepository.findAll(Sort.by("type"));

        model.addAttribute("protocol", protocol);
        model.addAttribute("laboratoryBosses", laboratoryBosses);
        model.addAttribute("departmentBosses", departmentBosses);
        model.addAttribute("producers", producers);
        model.addAttribute("whatFors", whatFors);

        return "protocol/edit";
    }

    @PostMapping("/save/{protocolId}")
    private String save(
            @PathVariable(name = "protocolId") Protocol protocol,
            @ModelAttribute Protocol editedProtocol
    ) {
        protocol.setNumber(editedProtocol.getNumber());
        protocol.setDate(editedProtocol.getDate());
        protocol.setDepartmentBoss(editedProtocol.getDepartmentBoss());
        protocol.setLaboratoryBoss(editedProtocol.getLaboratoryBoss());
        protocol.setProducer(editedProtocol.getProducer());
        protocol.setUser(editedProtocol.getUser());
        protocol.setNote(editedProtocol.getNote());
        protocol.setPlates(editedProtocol.getPlates());

        protocolRepository.save(protocol);
        protocolRepository.flush();

        return "redirect:/protocol/start";
    }

    @GetMapping("/delete/{protocolId}")
    private String deletePlate (
            @PathVariable(name = "protocolId") Protocol protocol
    ) {
        if (protocol == null) {
            throw new RuntimeException("Такого протокола не найдено");
        }
        List<Plate> plates = protocol.getPlates();

        for (Plate plate : plates) {
            plate.setProtocol(null);
            plate.setInStorage(true);
            plateRepository.save(plate);
        }
        plateRepository.flush();

        protocolRepository.delete(protocol);
        protocolRepository.flush();

        return "redirect:/protocol/start/";
    }

    @GetMapping("/add-plate/{protocolId}")
    private String addPlate(
            @PathVariable(name = "protocolId") Protocol protocol,
            Model model
    ) {
        model.addAttribute("protocol", protocol);

       return "protocol/add-plate";
    }

    @PostMapping("/add-plate/{protocolId}")
    private String addPlatePost(
            @PathVariable(name = "protocolId") Protocol protocol,
            @RequestParam(name = "findingPlate") String findingPlate,
            Model model
    ) {
        if (protocol == null) {
            throw new RuntimeException("Такого протокола не найдено");
        }
        List<Plate> plates = plateRepository
                .findAllByInStorageTrueAndCrashFalseAndNumberContainingOrderByNumber(findingPlate);
        model.addAttribute("findingPlates", plates);
        model.addAttribute("protocol", protocol);

        return "redirect:/protocol/founded-plate/" + protocol.getId();
    }

    @GetMapping("/founded-plate/{protocolId}")
    private String foundedPlate(
            @PathVariable(name = "protocolId") Protocol protocol,
            Model model
    ) {
        model.addAttribute("protocol", protocol);

        return "protocol/founded-plate";
    }

    @GetMapping("/founded-platePost/{protocolId}/{plateId}")
    private String foundedPlatePost(
            @PathVariable(name = "protocolId") Protocol protocol,
            @PathVariable(name = "plateId") Plate plate
    ) {
        List<Plate> plates = protocol.getPlates();
        plate.setProtocol(protocol);
        plate.setInStorage(false);
        plates.add(plate);
        plateRepository.save(plate);
        plateRepository.flush();

        protocol.setPlates(plates);
        protocolRepository.save(protocol);
        protocolRepository.flush();

        return "redirect:/protocol/edit/" + protocol.getId();
    }

    @GetMapping("/return-plate/{plateId}")
    private String returnPlate(
            @PathVariable(name = "plateId") Plate plate
    ) {
        Protocol protocol = plate.getProtocol();
        List<Plate> newPlates = protocol.getPlates();
        newPlates.remove(plate);
        protocol.setPlates(newPlates);
        protocolRepository.save(protocol);
        protocolRepository.flush();

        plate.setProtocol(null);
        plate.setInStorage(true);
        plateRepository.save(plate);
        plateRepository.flush();

        return "redirect:/protocol/edit/" + protocol.getId();
    }

    @GetMapping("/view/{protocolId}")
    private String view(
            @PathVariable(name = "protocolId") Protocol protocol,
            Model model
    ) {
        model.addAttribute("protocol", protocol);

        return "protocol/view";
    }

    @GetMapping("/view-create/{protocolId}")
    public void getExcelFile(
            @PathVariable(name = "protocolId") Protocol protocol,
            HttpServletResponse response
    ) {
        XSSFWorkbook workbook = protocolService.createReportInExcel(protocol);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + protocol.getNumber() + ".xlsx");

        try (OutputStream outputStream = response.getOutputStream()
        ) {
            workbook.write(outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPopoverWithPlates(Model model){
        List<Protocol> protocols = protocolRepository.findAll();
        Map<String,List<String>> platesInProtocol = new HashMap<>();
        for (Protocol protocol : protocols) {
            List<String> plates = protocol.getPlates()
                    .stream()
                    .sorted(Comparator.comparing(x -> x.getType().getName()))
                    .map(x -> x.getNumber() + " - " + x.getType().getName())
                    .collect(Collectors.toList());

            platesInProtocol.put(protocol.getId().toString(), plates);
        }

        model.addAttribute("plates_protocol", platesInProtocol);
    }
}
