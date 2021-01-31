package org.app.service.impl.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.app.repository.ProtocolRepository;
import org.app.entity.Plate;
import org.app.entity.Protocol;
import org.app.service.impl.ExcelService;
import org.app.service.impl.GeneralService;
import org.app.service.impl.ProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ProtocolSeviceImpl implements ProtocolService {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    public ProtocolSeviceImpl(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    @Override
    public synchronized String createNewNumber() {
        String result = "";
        List<Protocol> existingProtocols = protocolRepository.findAll(Sort.by("date").descending());
        SimpleDateFormat dateFormat = new SimpleDateFormat(".yy");
        String currentDate = dateFormat.format(new Date());

        if (existingProtocols.size() > 0) {
            Protocol lastProtocol = existingProtocols.get(0);

            if (!currentDate.substring(2, 3).equals(lastProtocol.getNumber().substring(lastProtocol.getNumber().length() - 1))) {
                result = "001" + currentDate;
            } else {
                int newNumber = Integer.parseInt(lastProtocol.getNumber().substring(0, 3)) + 1;

                if (newNumber < 10) {
                    result = "00" + newNumber + currentDate;
                } else if (newNumber < 100) {
                    result = "0" + newNumber + currentDate;
                } else {
                    result = newNumber + currentDate;
                }
            }
        } else {
            result = "001" + currentDate;
        }

        return result;
    }

    @Override
    public XSSFWorkbook createReportInExcel(Protocol protocol) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Протокол");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7)); // Протокол № дата
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7)); // передачи ситалла
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7)); // note

        //Заголовок
        Row row0 = sheet.createRow(0);
        Cell protocolNumber = row0.createCell(0);
        protocolNumber.setCellStyle(ExcelService.headerStyleCenter(workbook, 14));
        String protocolDate = GeneralService.transferDateIntoString(protocol.getDate());
        String numberString = "Акт № " + protocol.getNumber() + " от " + protocolDate;
        protocolNumber.setCellValue(numberString);

        // Кому передали
        Row row1 = sheet.createRow(1);
        Cell producer = row1.createCell(0);
        producer.setCellStyle(ExcelService.headerStyleCenter(workbook, 14));
        String producerString = "передачи материала на " + protocol.getProducer().getName();
        producer.setCellValue(producerString);

        // note
        if (protocol.getNote() != null) {
            Row row2 = sheet.createRow(2);
            Cell note = row2.createCell(0);
            note.setCellStyle(ExcelService.headerStyleCenter(workbook, 14));
            String noteString = protocol.getNote();
            note.setCellValue(noteString);
        }

        //Заголовок таблицы
        Row row3 = sheet.createRow(3);
        Cell numberPer = row3.createCell(0);
        numberPer.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        numberPer.setCellValue("№ п/п");
        Cell plateNumber = row3.createCell(1);
        plateNumber.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        plateNumber.setCellValue("№ Заготовки");
        Cell length = row3.createCell(2);
        length.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        length.setCellValue("Длина");
        Cell width = row3.createCell(3);
        width.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        width.setCellValue("Ширина");
        Cell height = row3.createCell(4);
        height.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        height.setCellValue("Толщина");

        sheet.addMergedRegion(new CellRangeAddress(3, 3, 5, 7));
        Cell whatFor = row3.createCell(5);
        whatFor.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        whatFor.setCellValue("Применение");
        Cell whatFor2 = row3.createCell(6);
        whatFor2.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
        Cell whatFor3 = row3.createCell(7);
        whatFor3.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));

        //Таблица
        int rowNumber = 4;
        for(Plate plate : protocol.getPlates()) {
            Row currentRow = sheet.createRow(rowNumber);
            Cell numberPerValue = currentRow.createCell(0);
            numberPerValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            numberPerValue.setCellValue(rowNumber - 3);

            Cell plateNumberValue = currentRow.createCell(1);
            plateNumberValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            plateNumberValue.setCellValue(plate.getNumber());

            Cell lengthValue = currentRow.createCell(2);
            lengthValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            lengthValue.setCellValue(plate.getLength());

            Cell widthValue = currentRow.createCell(3);
            widthValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            widthValue.setCellValue(plate.getWidth());

            Cell heightValue = currentRow.createCell(4);
            heightValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            heightValue.setCellValue(plate.getHeight());

            sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 5, 7));
            Cell whatForValue = currentRow.createCell(5);
            whatForValue.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            whatForValue.setCellValue(plate.getWhatFor().getType() + " - " + plate.getWhatFor().getQuantity() + " шт.");
            Cell whatForValue2 = currentRow.createCell(6);
            whatForValue2.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
            Cell whatForValue3 = currentRow.createCell(7);
            whatForValue3.setCellStyle(ExcelService.bodyStyle(workbook, 12, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));

            rowNumber++;
        }
        // Начальник отдела
        Row bottomRow1 = sheet.createRow(++rowNumber);
        Cell departmentBoss = bottomRow1.createCell(0);
        departmentBoss.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        departmentBoss.setCellValue("Начальник отдела");
        Cell departmentBossValue = bottomRow1.createCell(6);
        departmentBossValue.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        departmentBossValue.setCellValue(protocol.getDepartmentBoss().getName());

        // Начальник лаборатории
        Row bottomRow2 = sheet.createRow(++rowNumber);
        Cell laboratoryBoss = bottomRow2.createCell(0);
        laboratoryBoss.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        laboratoryBoss.setCellValue("Начальник лаборатории");
        Cell laboratoryBossValue = bottomRow2.createCell(6);
        laboratoryBossValue.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        laboratoryBossValue.setCellValue(protocol.getLaboratoryBoss().getName());

        // Исполнитель
        Row bottomRow3 = sheet.createRow(++rowNumber);
        Cell ingenegr = bottomRow3.createCell(0);
        ingenegr.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        ingenegr.setCellValue(protocol.getUser().getPosition());
        Cell ingenegrValue = bottomRow3.createCell(6);
        ingenegrValue.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        ingenegrValue.setCellValue(protocol.getUser().getLogin());

        Row bottomRow4 = sheet.createRow(++rowNumber);
        Cell prodName = bottomRow4.createCell(0);
        prodName.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        prodName.setCellValue(protocol.getProducer().getPosition());
        Cell prodNameValue = bottomRow4.createCell(6);
        prodNameValue.setCellStyle(ExcelService.bodyStyleLeft(workbook, 12));
        prodNameValue.setCellValue(protocol.getProducer().getSurname());

        //Ширина ячеек
        sheet.setColumnWidth(0, 1800);
        for (int i = 1; i < 9; i++) {
            sheet.autoSizeColumn(i, true);
        }

        return workbook;
    }
}
