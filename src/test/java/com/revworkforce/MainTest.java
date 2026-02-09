package com.revworkforce;

import com.revworkforce.menu.MainMenu;
import com.revworkforce.util.DBConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MainTest {

    @Test
    void testMain() {
        try (MockedStatic<DBConnection> mockDb = mockStatic(DBConnection.class);
                MockedStatic<MainMenu> mockMenu = mockStatic(MainMenu.class);
                MockedStatic<com.revworkforce.util.PasswordUtil> mockPass = mockStatic(
                        com.revworkforce.util.PasswordUtil.class)) {

            Connection mockCon = mock(Connection.class);
            PreparedStatement mockPs = mock(PreparedStatement.class);

            mockDb.when(DBConnection::getConnection).thenReturn(mockCon);
            when(mockCon.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            mockPass.when(() -> com.revworkforce.util.PasswordUtil.hashPassword("password")).thenReturn("hashed");

            // Execute main
            Main.main(new String[] {});

            // Verify
            mockDb.verify(DBConnection::getConnection);
            mockMenu.verify(MainMenu::start);
            verify(mockPs).executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMain_DBConnectionFail() {
        try (MockedStatic<DBConnection> mockDb = mockStatic(DBConnection.class)) {
            mockDb.when(DBConnection::getConnection).thenThrow(new RuntimeException("DB Down"));

            // Should not throw exception, just log error
            Main.main(new String[] {});

        }
    }
}
