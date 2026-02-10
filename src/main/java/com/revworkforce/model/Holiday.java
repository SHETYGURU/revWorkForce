package com.revworkforce.model;

import java.sql.Date;

/**
 * Represents a company holiday.
 * Maps to the 'holidays' table.
 * 
 * @author Gururaj Shetty
 */
public class Holiday {

    private int holidayId;
    private String holidayName;
    private Date holidayDate;
    private int year;

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
