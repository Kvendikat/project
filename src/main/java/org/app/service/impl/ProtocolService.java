package org.app.service.impl;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.app.entity.Protocol;

public interface ProtocolService {
    String createNewNumber();

    XSSFWorkbook createReportInExcel(Protocol protocol);
}
