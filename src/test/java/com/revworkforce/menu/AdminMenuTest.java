package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.model.Employee;
import com.revworkforce.service.AdminConfigService;
import com.revworkforce.service.AdminLeaveService;
import com.revworkforce.service.AdminService;
import com.revworkforce.service.AuditService;
import com.revworkforce.service.NotificationService;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class AdminMenuTest {

    @Test
    void testShowMenu_Exit() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(0);

            // Option 23 is Logout
            input.when(() -> InputUtil.readInt(anyString())).thenReturn(23);

            AdminMenu.start();
        }
    }

    @Test
    void testShowMenu_Flow() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<AdminService> adminService = mockStatic(AdminService.class);
                MockedStatic<AdminLeaveService> leaveService = mockStatic(AdminLeaveService.class);
                MockedStatic<AdminConfigService> configService = mockStatic(AdminConfigService.class);
                MockedStatic<AuditService> auditService = mockStatic(AuditService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(0);

            // Test option 1 (Add New Employee) then 23 (Logout)
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(1)
                    .thenReturn(23);

            AdminMenu.start();

            // Verify addEmployee is called (option 1)
            adminService.verify(AdminService::addEmployee);
        }
    }

    @Test
    void testShowMenu_MultipleSwitchCases() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<AdminService> adminService = mockStatic(AdminService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(0);

            // Test multiple options: 2, 3, 4, 5, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(2, 3, 4, 5, 23);

            AdminMenu.start();

            adminService.verify(AdminService::updateEmployee);
            adminService.verify(AdminService::viewAllEmployees);
            adminService.verify(AdminService::searchEmployees);
            adminService.verify(AdminService::assignManager);
        }
    }

    @Test
    void testShowMenu_LeaveConfigOptions() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<AdminService> adminService = mockStatic(AdminService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(0);

            // Test leave config options: 9, 10, 11, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(9, 10, 11, 23);

            AdminMenu.start();

            adminService.verify(AdminService::configureLeaveTypes);
            adminService.verify(AdminService::assignLeaveQuotas);
            adminService.verify(AdminService::adjustLeaveBalance);
        }
    }

    @Test
    void testShowMenu_SystemConfigOptions() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<AdminService> adminService = mockStatic(AdminService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(0);

            // Test system config: 15, 16, 17, 18, 19, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(15, 16, 17, 18, 19, 23);

            AdminMenu.start();

            adminService.verify(AdminService::manageDepartments);
            adminService.verify(AdminService::manageDesignations);
            adminService.verify(AdminService::configurePerformanceCycles);
            adminService.verify(AdminService::manageSystemPolicies);
            adminService.verify(AdminService::viewAuditLogs);
        }
    }

    @Test
    void testShowMenu_InvalidOption() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(0);

            // Test invalid option (999), then logout (23)
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(999, 23);

            AdminMenu.start();
        }
    }

    @Test
    void testShowMenu_NotificationsOption() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<NotificationService> notification = mockStatic(NotificationService.class)) {

            Employee mockAdmin = mock(Employee.class);
            when(mockAdmin.getEmployeeId()).thenReturn("ADMIN001");
            session.when(SessionContext::get).thenReturn(mockAdmin);

            notification.when(() -> NotificationService.getUnreadCount(anyString())).thenReturn(5);

            // Test notifications: 20, 21, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(20, 21, 23);

            AdminMenu.start();

            notification.verify(() -> NotificationService.viewNotifications("ADMIN001"));
            notification.verify(NotificationService::generateDailyNotifications);
        }
    }
}
