package ru.practicum.mainservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {
    public final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String dateToString(LocalDateTime date) {
        return date.format(DATE_TIME_FORMATTER);
    }

    public LocalDateTime stringToDate(String date) {
        if (!StringUtils.hasLength(date)) {
            return null;
        }

        return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
    }
}
