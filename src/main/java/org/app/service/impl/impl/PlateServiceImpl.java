package org.app.service.impl.impl;

import org.app.repository.PlateRepository;
import org.app.service.impl.PlateService;
import org.app.entity.Plate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlateServiceImpl implements PlateService {
    private PlateRepository plateRepository;

    public PlateServiceImpl(PlateRepository plateRepository) {
        this.plateRepository = plateRepository;
    }

    @Override
    public List<Plate> findControllingThisMonth() {
        List<Plate> platesInStorage = plateRepository.findAllByInStorageTrueAndCrashFalse();
        SimpleDateFormat dateFormat = new SimpleDateFormat(".MM.yy");
        String currentDate = dateFormat.format(new Date());
        List<Plate> result = new ArrayList<>();

        for (Plate plate : platesInStorage) {
            if (plate.getNumber().matches(currentDate)) {
                result.add(plate);
            }
        }
        result.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getNumber().substring(0, o.getNumber().length() - 7))));

        return result;
    }

    @Override
    public List<Plate> findPlatesInDateInterval(Date date1, Date date2) {
        List<Plate> result = new ArrayList<>();
        String [] startDateStr = transferDateInString(date1).split(":");
        String [] endDateStr = transferDateInString(date2).split(":");

        int startMonth = Integer.parseInt(startDateStr[0]);
        int endMonth = Integer.parseInt(endDateStr[0]);
        int startYear = Integer.parseInt(startDateStr[1]);
        int endYear = Integer.parseInt(endDateStr[1]);
        if (startYear > endYear) {
            throw new RuntimeException("Начальный год больше конечного");
        } else if (startYear == endYear && startMonth > endMonth) {
            throw new RuntimeException("Начальный месяц больше конечного");
        }

        for (int i = startYear; i < endYear; i++) {
            for (int j = startMonth; j < endMonth; j++) {
                String tempNumber = "0";
                if (j < 10) {
                    tempNumber += j;
                } else {
                    tempNumber = Integer.valueOf(j).toString();
                }
                List<Plate> tempList = plateRepository.findAllByNumberContainingOrderByNumber(tempNumber + "." + i);
                result.addAll(tempList);
            }
        }
        result = result
                .stream()
                .distinct()
                .sorted(Comparator.comparing(Plate::getNumber))
                .collect(Collectors.toList());

        return result;
    }

    private static String transferDateInString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM:yy");

        return dateFormat.format(date);
    }


}
