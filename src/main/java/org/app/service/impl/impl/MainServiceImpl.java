package org.app.service.impl.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.app.projections.PlateControllingThisMonthGroupProjection;
import org.app.projections.PlateMaterialTypeCountGroupProjections;
import org.app.projections.PlateToProducersGroupProjection;
import org.app.service.impl.MainService;
import org.springframework.stereotype.Service;
import org.app.service.impl.ExcelService;
import org.app.service.impl.GeneralService;

import java.util.Date;
import java.util.List;

@Service
public class MainServiceImpl implements MainService {

    @Override
    public XSSFWorkbook createMainExcelView(
            Integer inStorageInt,
            Integer inStorageGroup1Int,
            Integer inStorageGroup2Int,
            List<PlateMaterialTypeCountGroupProjections> countPlatesWithoutProducerToGroups,
            List<PlateToProducersGroupProjection> countTakeToProducerThisMonth,
            List<PlateControllingThisMonthGroupProjection> platesInThisMonthToGroups
            ) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчет");

        //Заголовок
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11)); // Данные о наличии заготовок
        Row row1 = sheet.createRow(0);
        Cell name = row1.createCell(0);
        String currentDate = GeneralService.transferDateIntoString(new Date());
        name.setCellValue("Данные о наличии заготовок на " + currentDate);
        name.setCellStyle(ExcelService.headerStyleCenter(workbook, 16));
        createCellStyleHeader(1, 11, row1, workbook, 14);

        // Заготовки на складе и разбиение по группам (Наименование)
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3)); // Заготовки на складе
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 7));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 8, 11));
        Row row2 = sheet.createRow(2);
        Cell inStorageName = row2.createCell(0);
        inStorageName.setCellValue("Заготовки на складе, шт.");
        inStorageName.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex()));
        createCellStyleHeaderBorder(1, 3, row2, workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());
        Cell inStorageGroup1Name = row2.createCell(4);
        inStorageGroup1Name.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex()));
        inStorageGroup1Name.setCellValue("Заготовки группы 1, шт.");
        createCellStyleHeaderBorder(5, 7, row2, workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());
        Cell inStorageGroup2Name = row2.createCell(8);
        inStorageGroup2Name.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex()));
        inStorageGroup2Name.setCellValue("Заготовки группы 2, шт.");
        createCellStyleHeaderBorder(9, 11, row2, workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());

        // Заготовки на складе и разбиение по группам (значения)
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 7));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 8, 11));
        Row row3 = sheet.createRow(3);
        Cell inStorageValue = row3.createCell(0);
        inStorageValue.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex()));
        inStorageValue.setCellValue(inStorageInt);
        createCellStyleHeaderBorder(1, 3, row3, workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());
        Cell inStorageGroup1Value = row3.createCell(4);
        inStorageGroup1Value.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex()));
        inStorageGroup1Value.setCellValue(inStorageGroup1Int);
        createCellStyleHeaderBorder(4, 7, row3, workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());
        Cell inStorageGroup2Value = row3.createCell(8);
        inStorageGroup2Value.setCellValue(inStorageGroup2Int);
        inStorageGroup2Value.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex()));
        createCellStyleHeaderBorder(8, 11, row3, workbook, 12, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());

        // Заготовки в наличии
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 4)); // Заготовки в наличии
        Row row4 = sheet.createRow(5);
        Cell plateName = row4.createCell(0);
        plateName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        plateName.setCellValue("Заготовки в наличии");
        createCellStyleHeaderBorder(1, 4, row4, workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex());

        Row row5 = sheet.createRow(6);
        Cell numberName = row5.createCell(0);
        numberName.setCellValue("№ п/п");
        numberName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        Cell materialName = row5.createCell(1);
        materialName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        materialName.setCellValue("Материал");
        Cell typeName = row5.createCell(2);
        typeName.setCellValue("Размер, мм");
        typeName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        Cell groupName = row5.createCell(3);
        groupName.setCellValue("Группа");
        groupName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        Cell quantityName = row5.createCell(4);
        quantityName.setCellValue("Кол-во, шт.");
        quantityName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));

        // Передано за текущий месяц
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 6, 11)); // Передано за месяц

        if (countTakeToProducerThisMonth.size() > 0) {
            Cell plateNameTake = row4.createCell(6);
            plateNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            plateNameTake.setCellValue("Передано за месяц");
            createCellStyleHeaderBorder(7, 11, row4, workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex());

            Cell numberNameTake = row5.createCell(6);
            numberNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            numberNameTake.setCellValue("№ п/п");
            Cell producerNameTake = row5.createCell(7);
            plateNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            producerNameTake.setCellValue("Изготовитель");
            producerNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            Cell materialNameTake = row5.createCell(8);
            materialNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            materialNameTake.setCellValue("Материал");
            Cell typeNameTake = row5.createCell(9);
            typeNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            typeNameTake.setCellValue("Размер, мм");
            Cell groupNameTake = row5.createCell(10);
            groupNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            groupNameTake.setCellValue("Группа");
            Cell quantityNameTake = row5.createCell(11);
            quantityNameTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            quantityNameTake.setCellValue("Кол-во, шт.");
        } else {
            Cell plateNameTake = row4.createCell(6);
            plateNameTake.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            plateNameTake.setCellValue("В текущем месяце заготовки не передавались");
            createCellStyleBodyBorder(7, 11, row4, workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex());
        }

        int rowNumber = 7;
        for (int i = 0;
             i < Math.max(
                countPlatesWithoutProducerToGroups.size(), countTakeToProducerThisMonth.size());
            i++
        ) {
            Row row = sheet.createRow(rowNumber);
            if (countPlatesWithoutProducerToGroups.size() > i) {
                PlateMaterialTypeCountGroupProjections plate1 = countPlatesWithoutProducerToGroups.get(i);
                Cell numberValue = row.createCell(0);
                numberValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                numberValue.setCellValue(i + 1);
                Cell materialValue = row.createCell(1);
                materialValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                materialValue.setCellValue(plate1.getMaterialName());
                Cell typeValue = row.createCell(2);
                typeValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                typeValue.setCellValue(plate1.getTypeName());
                Cell groupValue = row.createCell(3);
                groupValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                groupValue.setCellValue(plate1.getGroupName().equals("true") ? "I" : "II");
                Cell quantityValue = row.createCell(4);
                quantityValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                quantityValue.setCellValue(plate1.getCountPlates());
            }

            if (countTakeToProducerThisMonth.size() > i && countPlatesWithoutProducerToGroups.size() > 0) {
                PlateToProducersGroupProjection plate2 = countTakeToProducerThisMonth.get(i);
                Cell numberValueTake = row.createCell(6);
                numberValueTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                numberValueTake.setCellValue(i + 1);
                Cell producerValueTake = row.createCell(7);
                producerValueTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                producerValueTake.setCellValue(plate2.getProducerName());
                Cell materialValueTake = row.createCell(8);
                materialValueTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                materialValueTake.setCellValue(plate2.getMaterialName());
                Cell typeValueTake = row.createCell(9);
                typeValueTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                typeValueTake.setCellValue(plate2.getTypeName());
                Cell groupValueTake = row.createCell(10);
                groupValueTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                groupValueTake.setCellValue(plate2.getGroupName().equals("true") ? "I" : "II");
                Cell quantityValueTake = row.createCell(11);
                quantityValueTake.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                quantityValueTake.setCellValue(plate2.getCountPlates());
            }
            rowNumber++;
        }

        // Заготовки, проконтролированные за текущий месяц
        rowNumber++;
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 4));
        Row rowNameControl = sheet.createRow(rowNumber);
        Cell controlName = rowNameControl.createCell(0);

        if (platesInThisMonthToGroups.size() > 0) {
            controlName.setCellValue("Проконтролированно за месяц");
            controlName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            createCellStyleHeaderBorder(1, 4, rowNameControl, workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex());

            rowNumber++;
            Row rowControl = sheet.createRow(rowNumber);
            Cell numberControlName = rowControl.createCell(0);
            numberControlName.setCellValue("№ п/п");
            numberControlName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            Cell materialControlName = rowControl.createCell(1);
            materialControlName.setCellValue("Материал");
            materialControlName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            Cell typeControlName = rowControl.createCell(2);
            typeControlName.setCellValue("Размер, мм");
            typeControlName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            Cell groupControlName = rowControl.createCell(3);
            groupControlName.setCellValue("Группа");
            groupControlName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            Cell quantityControlName = rowControl.createCell(4);
            quantityControlName.setCellValue("Кол-во, шт.");
            quantityControlName.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));

            rowNumber++;
            int number = 1;
            for (PlateControllingThisMonthGroupProjection plate : platesInThisMonthToGroups) {
                Row row = sheet.createRow(rowNumber);
                Cell numberValue = row.createCell(0);
                numberValue.setCellValue(number);
                numberValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                Cell materialValue = row.createCell(1);
                materialValue.setCellValue(plate.getMaterialName());
                materialValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                Cell typeValue = row.createCell(2);
                typeValue.setCellValue(plate.getTypeName());
                typeValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                Cell groupValue = row.createCell(3);
                groupValue.setCellValue(plate.getGroupName().equals("true") ? "I" : "II");
                groupValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                Cell quantityValue = row.createCell(4);
                quantityValue.setCellValue(plate.getCountPlates());
                quantityValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
                rowNumber++;
                number++;
            }
        } else {
            controlName.setCellValue("В текущем месяце контроль не проводился");
            controlName.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            createCellStyleBodyBorder(1, 4, rowNameControl, workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex());
        }

        //Ширина ячеек
        sheet.setColumnWidth(0, 1800);
        for (int i = 1; i < 12; i++) {
            sheet.autoSizeColumn(i, true);
        }

        //Линии печати
        sheet.setPrintGridlines(true);

        return workbook;
    }

    private void createCellStyleHeader(int startRange, int endRange, Row row, XSSFWorkbook workbook, int textSize) {
        for (int i = startRange; i <= endRange; i++) {
            Cell tempCell = row.createCell(i);
            tempCell.setCellStyle(ExcelService.headerStyleCenter(workbook, 14));
        }
    }

    private void createCellStyleHeaderBorder (
            int startRange,
            int endRange,
            Row row,
            XSSFWorkbook workbook,
            int textSize,
            BorderStyle borderStyle,
            short indexColor
    ) {
        for (int i = startRange; i <= endRange; i++) {
            Cell tempCell = row.createCell(i);
            tempCell.setCellStyle(ExcelService.headerStyleCenterBorder(workbook, textSize, borderStyle, indexColor));
        }
    }

    private void createCellStyleBodyBorder(
            int startRange,
            int endRange,
            Row row,
            XSSFWorkbook workbook,
            int textSize,
            BorderStyle borderStyle,
            short indexColor
    ) {
        for (int i = startRange; i <= endRange; i++) {
            Cell tempCell = row.createCell(i);
            tempCell.setCellStyle(ExcelService.bodyStyle(workbook, textSize, borderStyle, indexColor));
        }
    }
}
