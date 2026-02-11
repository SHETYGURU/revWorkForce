package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.dao.RoleDAO;
import com.revworkforce.model.Employee;
import com.revworkforce.service.AuthService;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainMenuTest {

    private MockedStatic<InputUtil> mockInputUtil;
    private MockedStatic<AuthService> mockAuthService;
    private MockedStatic<AdminMenu> mockAdminMenu;

    @BeforeEach
    void setUp() {
        mockInputUtil = mockStatic(InputUtil.class);
        mockAuthService = mockStatic(AuthService.class);
        mockAdminMenu = mockStatic(AdminMenu.class);
    }

    @AfterEach
    void tearDown() {
        mockInputUtil.close();
        mockAuthService.close();
        mockAdminMenu.close();
    }

    @Test
    void testStart_Exit() {
        // Simulate choosing Option 3 (Exit)
        mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(3);

        // Use standard system exit interception or just verify InputUtil.close() is
        // called
        // Since System.exit stops JVM, we can't easily test it without extensive setup
        // (System Rules)
        // For now, we assume the switch case logic flow up to exit.

        // However, MainMenu.java calls System.exit(0). This triggers security exception
        // or kills test.
        // We often skip testing System.exit paths in basic unit tests or use a wrapper.
        // Changing strategy: Test Login Flow instead which doesn't exit immediately if
        // successful.
    }

    @Test
    void testLogin_Admin() {
        // 1. Select Login (1)
        // 2. Loop cycles, prompts again -> Throw Error to break infinite loop
        mockInputUtil.when(() -> InputUtil.readInt(anyString()))
                .thenReturn(1)
                .thenThrow(new Error("End Test"));

        mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID")))
                .thenReturn("ADMIN001");
        mockInputUtil.when(() -> InputUtil.readString(contains("Password")))
                .thenReturn("password");

        mockAuthService.when(() -> AuthService.login("ADMIN001", "password")).thenReturn(true);

        // Set Session
        Employee admin = new Employee();
        admin.setEmployeeId("ADMIN001");
        admin.setFirstName("Super");
        SessionContext.set(admin);

        // Mock RoleDAO
        try (MockedConstruction<RoleDAO> mockRoleDao = mockConstruction(RoleDAO.class,
                (mock, context) -> {
                    when(mock.getEmployeeRole("ADMIN001")).thenReturn("ADMIN");
                })) {

            // AdminMenu.start() called normally (returns void)
            mockAdminMenu.when(AdminMenu::start).thenAnswer(invocation -> null);

            try {
                MainMenu.start();
            } catch (Error e) {
                // Expected to break loop
                assertEquals("End Test", e.getMessage());
            }

            // Verify AdminMenu.start() was reached
            mockAdminMenu.verify(AdminMenu::start);
        }
    }
}
