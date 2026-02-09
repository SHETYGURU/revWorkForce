package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.service.EmployeeService;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class EmployeeMenuTest {

    @Test
    void testShowMenu() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<EmployeeService> service = mockStatic(EmployeeService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                        com.revworkforce.service.NotificationService.class)) {

            // Mock SessionContext
            com.revworkforce.model.Employee emp = new com.revworkforce.model.Employee();
            emp.setEmployeeId("EMP1");
            emp.setFirstName("Worker");

            session.when(SessionContext::get).thenReturn(emp);

            // Navigate: 1 (View Profile) -> 17 (Logout)
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(1)
                    .thenReturn(17);

            EmployeeMenu.start();

            service.verify(() -> EmployeeService.viewProfile("EMP1"));
        }
    }

    @Test
    void testShowMenu_LeaveOptions() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<com.revworkforce.service.LeaveService> leaveService = mockStatic(
                        com.revworkforce.service.LeaveService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                        com.revworkforce.service.NotificationService.class)) {

            com.revworkforce.model.Employee emp = new com.revworkforce.model.Employee();
            emp.setEmployeeId("EMP1");
            emp.setFirstName("Worker");

            session.when(SessionContext::get).thenReturn(emp);

            // Test leave options: 4, 5, 6, 7, 8, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(4, 5, 6, 7, 8, 17);

            EmployeeMenu.start();

            leaveService.verify(() -> com.revworkforce.service.LeaveService.viewLeaveBalance("EMP1"));
            leaveService.verify(() -> com.revworkforce.service.LeaveService.applyLeave("EMP1"));
            leaveService.verify(() -> com.revworkforce.service.LeaveService.viewMyLeaves("EMP1"));
            leaveService.verify(() -> com.revworkforce.service.LeaveService.cancelLeave("EMP1"));
            leaveService.verify(com.revworkforce.service.LeaveService::viewHolidays);
        }
    }

    @Test
    void testShowMenu_PerformanceOptions() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<com.revworkforce.service.PerformanceService> perfService = mockStatic(
                        com.revworkforce.service.PerformanceService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                        com.revworkforce.service.NotificationService.class)) {

            com.revworkforce.model.Employee emp = new com.revworkforce.model.Employee();
            emp.setEmployeeId("EMP1");
            emp.setFirstName("Worker");

            session.when(SessionContext::get).thenReturn(emp);

            // Test performance options: 9, 10, 11, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(9, 10, 11, 17);

            EmployeeMenu.start();

            perfService.verify(() -> com.revworkforce.service.PerformanceService.submitSelfReview("EMP1"));
            perfService.verify(() -> com.revworkforce.service.PerformanceService.manageGoals("EMP1"));
            perfService.verify(() -> com.revworkforce.service.PerformanceService.viewManagerFeedback("EMP1"));
        }
    }

    @Test
    void testShowMenu_Case12_UpcomingData() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<EmployeeService> service = mockStatic(EmployeeService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                        com.revworkforce.service.NotificationService.class)) {

            com.revworkforce.model.Employee emp = new com.revworkforce.model.Employee();
            emp.setEmployeeId("EMP1");
            emp.setFirstName("Worker");

            session.when(SessionContext::get).thenReturn(emp);

            // Test case 12 (birthdays + anniversaries)
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(12, 17);

            EmployeeMenu.start();

            service.verify(EmployeeService::viewUpcomingBirthdays);
            service.verify(EmployeeService::viewWorkAnniversaries);
        }
    }

    @Test
    void testShowMenu_OtherOptions() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<EmployeeService> service = mockStatic(EmployeeService.class);
                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                        com.revworkforce.service.NotificationService.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class)) {

            com.revworkforce.model.Employee emp = new com.revworkforce.model.Employee();
            emp.setEmployeeId("EMP1");
            emp.setFirstName("Worker");

            session.when(SessionContext::get).thenReturn(emp);

            // Test options: 2, 3, 13, 14, 15, 16, then logout
            input.when(() -> InputUtil.readInt(anyString()))
                    .thenReturn(2, 3, 13, 14, 15, 16, 17);

            EmployeeMenu.start();

            service.verify(() -> EmployeeService.updateProfile("EMP1"));
            service.verify(() -> EmployeeService.viewManagerDetails("EMP1"));
            service.verify(EmployeeService::viewAnnouncements);
            service.verify(EmployeeService::employeeDirectory);
            notif.verify(() -> com.revworkforce.service.NotificationService.viewNotifications("EMP1"));
            service.verify(() -> EmployeeService.changePassword("EMP1"));
        }
    }

    @Test
    void testShowMenu_InvalidOption() {
        try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                        com.revworkforce.service.NotificationService.class)) {

            com.revworkforce.model.Employee emp = new com.revworkforce.model.Employee();
            emp.setEmployeeId("EMP1");
            emp.setFirstName("Worker");

            session.when(SessionContext::get).thenReturn(emp);

            // Test invalid option then logout
            input.when(() -> InputUtil.readInt(anyString())).thenReturn(999, 17);

            EmployeeMenu.start();
        }
    }
}
