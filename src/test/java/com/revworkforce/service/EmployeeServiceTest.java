package com.revworkforce.service;

import com.revworkforce.dao.AnnouncementDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.ResultSet;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;

class EmployeeServiceTest {

    private EmployeeDAO mockEmpDao;
    private AnnouncementDAO mockAnnDao;
    private MockedStatic<InputUtil> mockInputUtil;
    private MockedStatic<AuthService> mockAuthService;

    @BeforeEach
    void setUp() throws Exception {
        mockEmpDao = mock(EmployeeDAO.class);
        mockAnnDao = mock(AnnouncementDAO.class);

        setPrivateStaticField(EmployeeService.class, "employeeDAO", mockEmpDao);
        setPrivateStaticField(EmployeeService.class, "announcementDAO", mockAnnDao);

        mockInputUtil = mockStatic(InputUtil.class);
        mockAuthService = mockStatic(AuthService.class);
    }

    @AfterEach
    void tearDown() {
        mockInputUtil.close();
        mockAuthService.close();
    }

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    void testViewProfile_Found() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(mockEmpDao.getProfile("EMP001")).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(anyString())).thenReturn("Test Value");

        EmployeeService.viewProfile("EMP001");

        verify(mockEmpDao).getProfile("EMP001");
    }

    @Test
    void testUpdateProfile() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(mockEmpDao.getProfile("EMP001")).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("phone")).thenReturn("123");
        when(rs.getString("address")).thenReturn("Addr");
        when(rs.getString("emergency_contact")).thenReturn("Emg");

        mockInputUtil.when(() -> InputUtil.readString(contains("Phone"))).thenReturn("999");
        mockInputUtil.when(() -> InputUtil.readString(contains("Address"))).thenReturn("");
        mockInputUtil.when(() -> InputUtil.readString(contains("Emergency"))).thenReturn("Mom");

        EmployeeService.updateProfile("EMP001");

        verify(mockEmpDao).updateProfile("EMP001", "999", "Addr", "Mom");
    }

    @Test
    void testChangePassword_Success() {
        mockInputUtil.when(() -> InputUtil.readString(contains("Current"))).thenReturn("oldPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("New"))).thenReturn("newPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("Confirm"))).thenReturn("newPass");

        mockAuthService.when(() -> AuthService.changePassword("EMP001", "oldPass", "newPass")).thenReturn(true);

        EmployeeService.changePassword("EMP001");

        mockAuthService.verify(() -> AuthService.changePassword("EMP001", "oldPass", "newPass"));
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        mockInputUtil.when(() -> InputUtil.readString(contains("Current"))).thenReturn("oldPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("New"))).thenReturn("newPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("Confirm"))).thenReturn("differentPass");

        EmployeeService.changePassword("EMP001");

        mockAuthService.verify(() -> AuthService.changePassword(anyString(), anyString(), anyString()), never());
    }

    @Test
    void testChangePassword_EmptyPassword() {
        mockInputUtil.when(() -> InputUtil.readString(contains("Current"))).thenReturn("oldPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("New"))).thenReturn("");
        mockInputUtil.when(() -> InputUtil.readString(contains("Confirm"))).thenReturn("");

        EmployeeService.changePassword("EMP001");

        mockAuthService.verify(() -> AuthService.changePassword(anyString(), anyString(), anyString()), never());
    }

    @Test
    void testChangePassword_Failure() {
        mockInputUtil.when(() -> InputUtil.readString(contains("Current"))).thenReturn("oldPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("New"))).thenReturn("newPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("Confirm"))).thenReturn("newPass");

        mockAuthService.when(() -> AuthService.changePassword("EMP001", "oldPass", "newPass")).thenReturn(false);

        EmployeeService.changePassword("EMP001");

        mockAuthService.verify(() -> AuthService.changePassword("EMP001", "oldPass", "newPass"));
    }

    @Test
    void testViewUpcomingBirthdays() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(mockEmpDao.getUpcomingBirthdays()).thenReturn(rs);

        EmployeeService.viewUpcomingBirthdays();

        verify(mockEmpDao).getUpcomingBirthdays();
    }

    @Test
    void testEmployeeDirectory() throws Exception {
        mockInputUtil.when(() -> InputUtil.readString(contains("search"))).thenReturn("John");
        java.util.List<java.util.Map<String, Object>> mockList = new java.util.ArrayList<>();
        when(mockEmpDao.searchEmployees("John")).thenReturn(mockList);

        EmployeeService.employeeDirectory();

        verify(mockEmpDao).searchEmployees("John");
    }

    @Test
    void testViewProfile_NotFound() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(mockEmpDao.getProfile("EMP001")).thenReturn(rs);
        when(rs.next()).thenReturn(false); // Not found

        EmployeeService.viewProfile("EMP001");

        verify(mockEmpDao).getProfile("EMP001");
    }

    @Test
    void testUpdateProfile_Failure() throws Exception {
        // First getProfile mock needed
        ResultSet rs = mock(ResultSet.class);
        when(mockEmpDao.getProfile("EMP001")).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("phone")).thenReturn("123");
        when(rs.getString("address")).thenReturn("Addr");
        when(rs.getString("emergency_contact")).thenReturn("Emg");

        mockInputUtil.when(() -> InputUtil.readString(contains("Phone"))).thenReturn("999");
        mockInputUtil.when(() -> InputUtil.readString(contains("Address"))).thenReturn("");
        mockInputUtil.when(() -> InputUtil.readString(contains("Emergency"))).thenReturn("Mom");

        doThrow(new RuntimeException("DB Error")).when(mockEmpDao).updateProfile(anyString(), anyString(), anyString(),
                anyString());

        EmployeeService.updateProfile("EMP001");

        verify(mockEmpDao).updateProfile("EMP001", "999", "Addr", "Mom");
    }

    @Test
    void testViewWorkAnniversaries() throws Exception {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockEmpDao.getWorkAnniversaries()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        EmployeeService.viewWorkAnniversaries();

        verify(mockEmpDao).getWorkAnniversaries();
    }

    @Test
    void testViewAnnouncements() throws Exception {
        com.revworkforce.dao.AnnouncementDAO mockAnnounceDao = mock(com.revworkforce.dao.AnnouncementDAO.class);
        setPrivateStaticField(EmployeeService.class, "announcementDAO", mockAnnounceDao);

        ResultSet mockRs = mock(ResultSet.class);
        when(mockAnnounceDao.getAllAnnouncements()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        EmployeeService.viewAnnouncements();

        verify(mockAnnounceDao).getAllAnnouncements();
    }

    @Test
    void testViewManagerDetails_Success() throws Exception {
        ResultSet empRs = mock(ResultSet.class);
        ResultSet mgrRs = mock(ResultSet.class);

        when(mockEmpDao.getProfile("EMP001")).thenReturn(empRs);
        when(empRs.next()).thenReturn(true);
        when(empRs.getString("manager_id")).thenReturn("MGR001");

        when(mockEmpDao.getProfile("MGR001")).thenReturn(mgrRs);
        when(mgrRs.next()).thenReturn(true);
        when(mgrRs.getString("first_name")).thenReturn("Boss");
        when(mgrRs.getString("last_name")).thenReturn("Manager");
        when(mgrRs.getString("email")).thenReturn("boss@example.com");
        when(mgrRs.getString("phone")).thenReturn("1234567890");

        EmployeeService.viewManagerDetails("EMP001");

        verify(mockEmpDao).getProfile("EMP001");
        verify(mockEmpDao).getProfile("MGR001");
    }

    @Test
    void testViewManagerDetails_NoManager() throws Exception {
        ResultSet empRs = mock(ResultSet.class);

        when(mockEmpDao.getProfile("EMP001")).thenReturn(empRs);
        when(empRs.next()).thenReturn(true);
        when(empRs.getString("manager_id")).thenReturn(null);

        EmployeeService.viewManagerDetails("EMP001");

        verify(mockEmpDao).getProfile("EMP001");
        verify(mockEmpDao, never()).getProfile(eq("MGR001"));
    }

    @Test
    void testViewManagerDetails_ManagerNotFound() throws Exception {
        ResultSet empRs = mock(ResultSet.class);
        ResultSet mgrRs = mock(ResultSet.class);

        when(mockEmpDao.getProfile("EMP001")).thenReturn(empRs);
        when(empRs.next()).thenReturn(true);
        when(empRs.getString("manager_id")).thenReturn("MGR001");

        when(mockEmpDao.getProfile("MGR001")).thenReturn(mgrRs);
        when(mgrRs.next()).thenReturn(false);

        EmployeeService.viewManagerDetails("EMP001");

        verify(mockEmpDao).getProfile("EMP001");
        verify(mockEmpDao).getProfile("MGR001");
    }
}
