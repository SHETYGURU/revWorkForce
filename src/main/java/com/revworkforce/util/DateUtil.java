package com.revworkforce.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for Date and Time operations.
 */
public class DateUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Date getCurrentDate() {
        return Date.valueOf(LocalDate.now());
    }

    public static Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static String formatDate(Date date) {
        if (date == null) return "N/A";
        return date.toLocalDate().format(DATE_FMT);
    }

    public static String formatTimestamp(Timestamp ts) {
        if (ts == null) return "N/A";
        return ts.toLocalDateTime().format(DATETIME_FMT);
    }

    public static Date parseDate(String dateStr) {
        try {
            return Date.valueOf(LocalDate.parse(dateStr, DATE_FMT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
    }
}
