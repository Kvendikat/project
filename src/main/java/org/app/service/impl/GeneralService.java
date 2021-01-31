package org.app.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralService {
    public static String transferDateIntoString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }
}
