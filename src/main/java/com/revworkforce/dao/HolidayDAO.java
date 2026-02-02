package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;
import java.sql.*;

public class HolidayDAO {

    public void addHoliday(String name, Date date, int year) throws Exception {
        String sql = "INSERT INTO holidays (holiday_name, holiday_date, year) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDate(2, date);
            ps.setInt(3, year);
            ps.executeUpdate();
        }
    }

    public ResultSet getHolidays(int year) throws Exception {
        String sql = "SELECT * FROM holidays WHERE year = ? ORDER BY holiday_date";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, year);
        return ps.executeQuery();
    }
}
