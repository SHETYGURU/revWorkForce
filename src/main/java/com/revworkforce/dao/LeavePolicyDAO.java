package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;
import java.sql.*;

public class LeavePolicyDAO {

    public void createLeaveType(
            String name,
            int maxPerYear, // Kept in method signature but effectively ignored or stored in description for now if schema doesn't support
            boolean carryForward
    ) throws Exception {

        // Note: Schema only has leave_type_name and description.
        // Storing meta-data in description as JSON-like or simple text for now to avoid breaking schema.
        String description = "Max: " + maxPerYear + ", CarryForward: " + carryForward;

        String sql = """
            INSERT INTO leave_types
            (leave_type_name, description)
            VALUES (?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
        }
    }
}
