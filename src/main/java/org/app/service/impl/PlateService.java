package org.app.service.impl;

import org.app.entity.Plate;

import java.util.Date;
import java.util.List;

public interface PlateService {
    List<Plate> findControllingThisMonth();

    List<Plate> findPlatesInDateInterval(Date date1, Date date2);
}
