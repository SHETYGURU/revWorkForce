package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.service.ManagerService;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class ManagerMenuTest {

        @Test
        void testShowMenu() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<ManagerService> service = mockStatic(ManagerService.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        // Mock SessionContext to return a dummy employee
                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Navigate: 1 (View Team) -> 14 (Logout)
                        input.when(() -> InputUtil.readInt(anyString()))
                                        .thenReturn(1)
                                        .thenReturn(14);

                        ManagerMenu.start();

                        service.verify(() -> ManagerService.viewTeam("MGR1"));
                }
        }

        @Test
        void testShowMenu_Case2_ValidTeamMember() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<ManagerService> service = mockStatic(ManagerService.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Case 2 -> Enter EMP001 (valid team member) -> Logout
                        input.when(() -> InputUtil.readInt(anyString())).thenReturn(2, 14);
                        input.when(() -> InputUtil.readString(anyString())).thenReturn("EMP001");

                        service.when(() -> ManagerService.isTeamMember("MGR1", "EMP001")).thenReturn(true);

                        ManagerMenu.start();

                        service.verify(() -> ManagerService.viewTeamBasic("MGR1"));
                        service.verify(() -> ManagerService.isTeamMember("MGR1", "EMP001"));
                        service.verify(() -> ManagerService.viewTeamMemberProfile("EMP001"));
                }
        }

        @Test
        void testShowMenu_Case3_ProcessLeave() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<ManagerService> service = mockStatic(ManagerService.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Case 3 -> Process leave: leaveId 100, approve, then exit loop
                        input.when(() -> InputUtil.readInt(anyString()))
                                        .thenReturn(3) // Choose option 3
                                        .thenReturn(100) // LeaveId
                                        .thenReturn(0) // Exit loop
                                        .thenReturn(14); // Logout

                        input.when(() -> InputUtil.readString(anyString()))
                                        .thenReturn("A") // Approve
                                        .thenReturn("Good to go"); // Comments

                        service.when(() -> ManagerService.isPendingLeave("MGR1", 100)).thenReturn(true);

                        ManagerMenu.start();

                        service.verify(() -> ManagerService.viewTeamLeaveRequests("MGR1"), times(2));
                        service.verify(() -> ManagerService.processLeave("MGR1", 100, "APPROVED", "Good to go"));
                }
        }

        @Test
        void testShowMenu_Case4_RevokeLeave() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<ManagerService> service = mockStatic(ManagerService.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Case 4 -> Revoke leave
                        input.when(() -> InputUtil.readInt(anyString()))
                                        .thenReturn(4, 200, 14); // Option 4, LeaveId 200, Logout

                        input.when(() -> InputUtil.readString(anyString()))
                                        .thenReturn("Emergency cancellation");

                        ManagerMenu.start();

                        service.verify(() -> ManagerService.viewTeamLeaveCalendar("MGR1"));
                        service.verify(() -> ManagerService.revokeApprovedLeave("MGR1", 200, "Emergency cancellation"));
                }
        }

        @Test
        void testShowMenu_Case6_LeaveCalendarFilter() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<ManagerService> service = mockStatic(ManagerService.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Case 6 -> Filter by employee, then exit
                        input.when(() -> InputUtil.readInt(anyString())).thenReturn(6, 14);
                        input.when(() -> InputUtil.readString(anyString())).thenReturn("EMP001", "0");

                        service.when(() -> ManagerService.isTeamMember("MGR1", "EMP001")).thenReturn(true);

                        ManagerMenu.start();

                        service.verify(() -> ManagerService.viewEmployeeLeaveCalendar("EMP001"));
                }
        }

        @Test
        void testShowMenu_Case10_PerformanceReview() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<ManagerService> service = mockStatic(ManagerService.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Case 10 -> Submit performance review
                        input.when(() -> InputUtil.readInt(anyString()))
                                        .thenReturn(10, 500, 4, 14); // Option 10, ReviewId 500, Rating 4, Logout

                        input.when(() -> InputUtil.readString(anyString()))
                                        .thenReturn("Great work!");

                        ManagerMenu.start();

                        service.verify(() -> ManagerService.viewTeamPerformance("MGR1"));
                        service.verify(() -> ManagerService.submitPerformanceReview("MGR1", 500, "Great work!", 4));
                }
        }

        @Test
        void testShowMenu_InvalidOption() {
                try (MockedStatic<InputUtil> input = mockStatic(InputUtil.class);
                                MockedStatic<SessionContext> session = mockStatic(SessionContext.class);
                                MockedStatic<com.revworkforce.service.NotificationService> notif = mockStatic(
                                                com.revworkforce.service.NotificationService.class)) {

                        com.revworkforce.model.Employee mgr = new com.revworkforce.model.Employee();
                        mgr.setEmployeeId("MGR1");
                        mgr.setFirstName("Boss");

                        session.when(SessionContext::get).thenReturn(mgr);

                        // Test invalid option then logout
                        input.when(() -> InputUtil.readInt(anyString())).thenReturn(999, 14);

                        ManagerMenu.start();
                }
        }
}
