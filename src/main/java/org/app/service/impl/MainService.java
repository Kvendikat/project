package org.app.service.impl;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.app.projections.PlateControllingThisMonthGroupProjection;
import org.app.projections.PlateMaterialTypeCountGroupProjections;
import org.app.projections.PlateToProducersGroupProjection;

import java.util.List;

public interface MainService {
    XSSFWorkbook createMainExcelView(
            Integer inStorageInt,
            Integer inStorageGroup1Int,
            Integer inStorageGroup2Int,
            List<PlateMaterialTypeCountGroupProjections> countPlatesWithoutProducerToGroups,
            List<PlateToProducersGroupProjection> countTakeToProducerThisMonth,
            List<PlateControllingThisMonthGroupProjection> platesInThisMonthToGroups
    );
}
