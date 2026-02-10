package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO for managing Announcements.
 * Handles fetching all announcements.
 * 
 * @author Gururaj Shetty
 */
public class AnnouncementDAO {
    public ResultSet getAllAnnouncements() throws Exception {

        String sql = """
                    SELECT title, content, posted_date
                    FROM announcements
                    ORDER BY posted_date DESC
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

}
