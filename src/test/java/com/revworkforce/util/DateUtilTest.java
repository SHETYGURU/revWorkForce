package com.revworkforce.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

class DateUtilTest {

    @Test
    void testGetCurrentDate() {
        Date date = DateUtil.getCurrentDate();
        Assertions.assertNotNull(date);
        Assertions.assertEquals(LocalDate.now().toString(), date.toString());
    }

    @Test
    void testGetCurrentTimestamp() {
        Timestamp timestamp = DateUtil.getCurrentTimestamp();
        Assertions.assertNotNull(timestamp);
    }

    @Test
    void testFormatDate() {
        Date date = Date.valueOf("2023-10-01");
        String formatted = DateUtil.formatDate(date);
        Assertions.assertEquals("2023-10-01", formatted);
    }

    @Test
    void testFormatDate_Null() {
        String formatted = DateUtil.formatDate(null);
        Assertions.assertEquals("N/A", formatted);
    }

    @Test
    void testFormatTimestamp() {
        Timestamp ts = Timestamp.valueOf("2023-10-01 12:30:45");
        String formatted = DateUtil.formatTimestamp(ts);
        Assertions.assertEquals("2023-10-01 12:30:45", formatted);
    }

    @Test
    void testFormatTimestamp_Null() {
        String formatted = DateUtil.formatTimestamp(null);
        Assertions.assertEquals("N/A", formatted);
    }

    @ParameterizedTest
    @ValueSource(strings = { "2023-10-01", "2024-02-29", "2000-01-01" })
    void testParseDate_Valid(String dateStr) {
        Date date = DateUtil.parseDate(dateStr);
        Assertions.assertEquals(dateStr, date.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = { "01-10-2023", "2023/10/01", "invalid-date", "", " " })
    void testParseDate_Invalid(String dateStr) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtil.parseDate(dateStr));
    }
}
