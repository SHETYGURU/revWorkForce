package com.revworkforce.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtilTest {

    @Test
    public void testGetCurrentDate() {
        Date date = DateUtil.getCurrentDate();
        Assertions.assertNotNull(date);
        Assertions.assertEquals(LocalDate.now().toString(), date.toString());
    }

    @Test
    public void testGetCurrentTimestamp() {
        Timestamp timestamp = DateUtil.getCurrentTimestamp();
        Assertions.assertNotNull(timestamp);
    }

    @Test
    public void testFormatDate() {
        Date date = Date.valueOf("2023-10-01");
        String formatted = DateUtil.formatDate(date);
        Assertions.assertEquals("2023-10-01", formatted);
    }

    @Test
    public void testFormatDate_Null() {
        String formatted = DateUtil.formatDate(null);
        Assertions.assertEquals("N/A", formatted);
    }

    @Test
    public void testFormatTimestamp() {
        Timestamp ts = Timestamp.valueOf("2023-10-01 12:30:45");
        String formatted = DateUtil.formatTimestamp(ts);
        Assertions.assertEquals("2023-10-01 12:30:45", formatted);
    }

    @Test
    public void testFormatTimestamp_Null() {
        String formatted = DateUtil.formatTimestamp(null);
        Assertions.assertEquals("N/A", formatted);
    }

    @Test
    public void testParseDate_Valid() {
        Date date = DateUtil.parseDate("2023-10-01");
        Assertions.assertEquals("2023-10-01", date.toString());
    }

    @Test
    public void testParseDate_Invalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtil.parseDate("01-10-2023"));
    }
}
