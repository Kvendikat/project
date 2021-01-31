package org.app.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelService {

    public static CellStyle headerStyleCenter (
            XSSFWorkbook workbook,
            int textSize
    ) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setFont(timesNewRomanFontBold(workbook, textSize));

        return  style;
    }
    
    public static CellStyle headerStyleCenterBorder (
             XSSFWorkbook workbook, 
             int textSize,
             BorderStyle borderStyle,
             short indexColor
    ) {
        CellStyle style = headerStyleCenter(workbook, textSize);

        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
        style.setBorderTop(borderStyle);

        style.setBottomBorderColor(indexColor);
        style.setLeftBorderColor(indexColor);
        style.setRightBorderColor(indexColor);
        style.setTopBorderColor(indexColor);

        return  style;
    }

    public static CellStyle bodyStyleLeft (
            XSSFWorkbook workbook,
            int textSize
    ) {
        CellStyle style = headerStyleCenter(
                workbook,
                textSize
        );
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(timesNewRomanFont(workbook, textSize));

        return style;
    }


    public static CellStyle bodyStyle (
            XSSFWorkbook workbook,
            int textSize
    ) {
        CellStyle style = headerStyleCenter(
                workbook,
                textSize
        );
        style.setFont(timesNewRomanFont(workbook, textSize));

        return style;
    }

    public static CellStyle bodyStyle (
            XSSFWorkbook workbook, 
            int textSize,
            BorderStyle borderStyle,
            short indexColor
    ) {
        CellStyle style = headerStyleCenterBorder(
                workbook,
                textSize,
                borderStyle,
                indexColor
        );
        style.setFont(timesNewRomanFont(workbook, textSize));

        return  style;
    }

    public static XSSFFont timesNewRomanFont(XSSFWorkbook workbook, int textSize) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) textSize);
        font.setFontName("Times New Roman");

        return(font);
    }

    public static XSSFFont timesNewRomanFontBold(XSSFWorkbook workbook, int textSize) {
        XSSFFont font = timesNewRomanFont(workbook, textSize);
        font.setBold(true);

        return font;
    }

    public static void createNewExcelFile(String path, XSSFWorkbook workbook) {
        try {
            FileOutputStream fOut = new FileOutputStream(path);
            workbook.write(fOut);
            workbook.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
