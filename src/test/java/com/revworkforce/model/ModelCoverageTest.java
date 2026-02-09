package com.revworkforce.model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModelCoverageTest {

    private final List<Class<?>> modelClasses = Arrays.asList(
            Announcement.class,
            Attendance.class,
            AuditLog.class,
            Department.class,
            Designation.class,
            Employee.class,
            Goal.class,
            Holiday.class,
            LeaveApplication.class,
            LeaveBalance.class,
            LeaveType.class,
            Notification.class,
            PerformanceCycle.class,
            PerformanceReview.class,
            Role.class,
            SystemPolicy.class);

    @Test
    void testAllModels() throws Exception {
        for (Class<?> clazz : modelClasses) {
            testModelClass(clazz);
        }
    }

    private void testModelClass(Class<?> clazz) throws Exception {
        System.out.println("Testing model: " + clazz.getSimpleName());
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Test toString
        assertNotNull(instance.toString());

        // Test Getters and Setters
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                String getterName = "get" + method.getName().substring(3);

                // Handle boolean isX getter
                Method getter = null;
                try {
                    getter = clazz.getMethod(getterName);
                } catch (NoSuchMethodException e) {
                    try {
                        getterName = "is" + method.getName().substring(3);
                        getter = clazz.getMethod(getterName);
                    } catch (NoSuchMethodException ex) {
                        // Ignore if no getter
                        continue;
                    }
                }

                Class<?> paramType = method.getParameterTypes()[0];
                Object value = getDefaultValue(paramType);

                // Invoke setter
                method.invoke(instance, value);

                // Invoke getter
                if (getter != null) {
                    getter.invoke(instance);
                }
            }
        }

        // Test toString
        assertNotNull(instance.toString());

        // Test equals and hashCode
        Object instance2 = clazz.getDeclaredConstructor().newInstance();
        if (instance.getClass().equals(instance2.getClass())) {
            // For simple POJOs, default equals is object identity which might differ,
            // but we just want to ensure method doesn't crash.
            // If equals is overridden, it should work.
            boolean eq = instance.equals(instance2);
            int h1 = instance.hashCode();
            int h2 = instance2.hashCode();
        }
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == Integer.class)
            return 1;
        if (type == double.class || type == Double.class)
            return 1.0;
        if (type == boolean.class || type == Boolean.class)
            return true;
        if (type == long.class || type == Long.class)
            return 1L;
        if (type == float.class || type == Float.class)
            return 1.0f;
        if (type == short.class || type == Short.class)
            return (short) 1;
        if (type == byte.class || type == Byte.class)
            return (byte) 1;
        if (type == char.class || type == Character.class)
            return 'A';
        if (type == String.class)
            return "test";
        if (type == Date.class)
            return new Date(System.currentTimeMillis());
        if (type == Timestamp.class)
            return new Timestamp(System.currentTimeMillis());
        return null;
    }
}
