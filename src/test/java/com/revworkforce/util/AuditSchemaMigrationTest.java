package com.revworkforce.util;

import com.revworkforce.util.DBConnection;
import com.revworkforce.util.AuditSchemaMigration;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuditSchemaMigrationTest {

    @Test
    void testMain_Success() {
        try (MockedStatic<DBConnection> mockDb = mockStatic(DBConnection.class)) {
            Connection mockCon = mock(Connection.class);
            Statement mockStmt = mock(Statement.class);

            mockDb.when(DBConnection::getConnection).thenReturn(mockCon);
            when(mockCon.createStatement()).thenReturn(mockStmt);
            when(mockStmt.executeUpdate(anyString())).thenReturn(1);

            assertDoesNotThrow(() -> AuditSchemaMigration.main(new String[] {}));

            verify(mockStmt).executeUpdate(anyString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMain_AlreadyExists() {
        try (MockedStatic<DBConnection> mockDb = mockStatic(DBConnection.class)) {
            Connection mockCon = mock(Connection.class);
            Statement mockStmt = mock(Statement.class);

            mockDb.when(DBConnection::getConnection).thenReturn(mockCon);
            when(mockCon.createStatement()).thenReturn(mockStmt);
            when(mockStmt.executeUpdate(anyString()))
                    .thenThrow(new SQLException("ORA-01430: column being added already exists in table"));

            assertDoesNotThrow(() -> AuditSchemaMigration.main(new String[] {}));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMain_OtherError() {
        try (MockedStatic<DBConnection> mockDb = mockStatic(DBConnection.class)) {
            mockDb.when(DBConnection::getConnection).thenThrow(new SQLException("Connection failed"));

            assertDoesNotThrow(() -> AuditSchemaMigration.main(new String[] {}));
        }
    }

    @Test
    void testPrivateConstructor() {
        assertDoesNotThrow(() -> {
            java.lang.reflect.Constructor<AuditSchemaMigration> constructor = AuditSchemaMigration.class
                    .getDeclaredConstructor();
            constructor.setAccessible(true);
            assertNotNull(constructor.newInstance());
        });
    }
}
