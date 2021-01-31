package org.app.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.app.entity.Protocol;
import org.app.projections.*;
import org.app.repository.PlateRepository;
import org.app.service.impl.ExcelService;
import org.app.service.impl.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private PlateRepository plateRepository;

    @Autowired
    private MainService mainService;

    @GetMapping("/")
    private String getMainPage() {
        return "index";
    }

    @GetMapping("/denied")
    private String getDeniedPage() {
        return "denied";
    }

    @GetMapping("/view")
    private String generalView(Model model) {
        addGeneralParamInModel(model);
        List<PlateMaterialTypeCountGroupProjections> countPlatesWithoutProducerToGroups =
                plateRepository.findAllPlateMaterialAndTypeAndGroup();
        List<PlateToProducersGroupProjection> countTakeToProducerThisMonthToGroups =
                plateRepository.findAllActivePlateByProducerForThisMonthToGroup();
        List<PlateControllingThisMonthGroupProjection> platesInThisMonthToGroups =
                plateRepository.findAllControllingThisMonthToGroup();

        model.addAttribute("countPlatesWithoutProducerToGroups", countPlatesWithoutProducerToGroups);
        model.addAttribute("countTakeToProducerThisMonthToGroups", countTakeToProducerThisMonthToGroups);
        model.addAttribute("platesInThisMonthToGroups", platesInThisMonthToGroups);

        return "view";
    }

    @GetMapping("/general-view-create")
    private void getGeneralExcelReport(HttpServletResponse response) {
        int countAllPlatesInStorage = plateRepository.countAllByInStorageTrue();
        int countPlatesWithoutStrain = plateRepository.countAllByInStorageTrueAndHasCrackFalse();
        int countPlatesWithStrain = plateRepository.countAllByInStorageTrueAndHasCrackTrue();
        List<PlateMaterialTypeCountGroupProjections> countPlatesWithoutProducerToGroups =
                plateRepository.findAllPlateMaterialAndTypeAndGroup();
        List<PlateToProducersGroupProjection> countTakeToProducerThisMonth =
                plateRepository.findAllActivePlateByProducerForThisMonthToGroup();
        List<PlateControllingThisMonthGroupProjection> platesInThisMonthToGroups =
                plateRepository.findAllControllingThisMonthToGroup();

        XSSFWorkbook workbook = mainService.createMainExcelView(
                countAllPlatesInStorage,
                countPlatesWithoutStrain,
                countPlatesWithStrain,
                countPlatesWithoutProducerToGroups,
                countTakeToProducerThisMonth,
                platesInThisMonthToGroups
        );

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.yyyy");
        String reportName = "Отчет "+ dateFormat.format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + reportName + ".xlsx");

        try (OutputStream outputStream = response.getOutputStream()
        ) {
            workbook.write(outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addGeneralParamInModel(Model model) {
        Integer countAllPlatesInStorage = plateRepository.countAllByInStorageTrue();
        Integer countPlatesWithoutStrain = plateRepository.countAllByInStorageTrueAndHasCrackFalse();
        Integer countPlatesWithStrain = plateRepository.countAllByInStorageTrueAndHasCrackTrue();
        List<PlateMaterialTypeCountProjections> countMaterialsWithoutProducer =
                plateRepository.findAllPlateMaterialAndType();
        List<PlateToProducersProjection> countTakeToProducerThisMonth =
                plateRepository.findAllActivePlateByProducerForThisMonth();
        List<PlateControllingThisMonthProjection> platesInThisMonth =
                plateRepository.findAllControllingThisMonth();

        model.addAttribute("countAllPlatesInStorage", countAllPlatesInStorage);
        model.addAttribute("countPlatesWithoutStrain", countPlatesWithoutStrain);
        model.addAttribute("countPlatesWithStrain", countPlatesWithStrain);
        model.addAttribute("countMaterialsWithoutProducer", countMaterialsWithoutProducer);
        model.addAttribute("countTakeToProducerThisMonth", countTakeToProducerThisMonth);
        model.addAttribute("platesInThisMonth", platesInThisMonth);
    }
}
