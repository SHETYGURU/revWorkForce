package com.revworkforce.service;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.DepartmentDAO;
import com.revworkforce.dao.DesignationDAO;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

        private EmployeeDAO mockEmpDao;
        private DepartmentDAO mockDeptDao;
        private DesignationDAO mockDesigDao;
        private MockedStatic<InputUtil> mockInputUtil;

        @BeforeEach
        public void setUp() throws Exception {
                mockEmpDao = Mockito.mock(EmployeeDAO.class);
                mockDeptDao = Mockito.mock(DepartmentDAO.class);
                mockDesigDao = Mockito.mock(DesignationDAO.class);

                setPrivateStaticField(AdminService.class, "employeeDAO", mockEmpDao);
                setPrivateStaticField(AdminService.class, "departmentDAO", mockDeptDao);
                setPrivateStaticField(AdminService.class, "designationDAO", mockDesigDao);

                mockInputUtil = Mockito.mockStatic(InputUtil.class);
        }

        @AfterEach
        public void tearDown() {
                if (mockInputUtil != null) {
                        mockInputUtil.close();
                }
        }

        private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(null, value);
        }

        @Test
        public void testAddEmployee_Success() throws Exception {
                // Mock Inputs for AdminService.addEmployee() flow

                // 1. Manager? (Y/N) -> "N" (Uses 3-arg readValidatedString)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Manager?"), any(Predicate.class),
                                anyString()))
                                .thenReturn("N");

                when(mockEmpDao.getNextId("EMP")).thenReturn("EMP001");

                // 2. First Name -> "John" (Uses 3-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("First Name"), any(Predicate.class),
                                anyString()))
                                .thenReturn("John");

                // 3. Last Name -> "Doe" (Uses 3-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Last Name"), any(Predicate.class),
                                anyString()))
                                .thenReturn("Doe");

                // 4. Email -> "john@example.com" (Uses 2-arg readValidatedString with Function)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Email"), any(Function.class)))
                                .thenReturn("john@example.com");

                // 5. Phone -> "1234567890" (Uses 2-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Phone"), any(Function.class)))
                                .thenReturn("1234567890");

                // 6. Address -> "123 Main St" (Uses 3-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Address"), any(Predicate.class),
                                anyString()))
                                .thenReturn("123 Main St");

                // 7. Emergency -> "Jane Doe" (Uses 3-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Emergency"), any(Predicate.class),
                                anyString()))
                                .thenReturn("Jane Doe");

                // 8. DOB -> "1990-01-01" (Uses 2-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("DOB"), any(Function.class)))
                                .thenReturn("1990-01-01");

                // 9. Joining Date Option -> 1 (Today)
                mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1);

                // 10. Dept ID -> "D001" (Uses 2-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Department ID"), any(Function.class)))
                                .thenReturn("D001");

                // 11. Desig ID -> "DES001" (Uses 2-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Designation ID"), any(Function.class)))
                                .thenReturn("DES001");

                // 12. Manager ID (Uses simple readString)
                mockInputUtil.when(() -> InputUtil.readString(contains("Manager ID"))).thenReturn("");

                // 13. Salary -> "50000" (Uses 3-arg)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Salary"), any(Predicate.class),
                                anyString()))
                                .thenReturn("50000");

                // Execute
                AdminService.addEmployee();

                // Verify
                verify(mockEmpDao).insertEmployee(
                                eq("EMP001"),
                                eq("John"),
                                eq("Doe"),
                                eq("john@example.com"),
                                eq("1234567890"),
                                eq("123 Main St"),
                                eq("Jane Doe"),
                                eq("1990-01-01"),
                                eq("D001"),
                                eq("DES001"),
                                eq(""),
                                eq(50000.0),
                                anyString(), // joining date
                                anyString() // password hash
                );
        }

        private static String contains(String substring) {
                return Mockito.argThat(arg -> arg != null && arg.contains(substring));
        }
}
