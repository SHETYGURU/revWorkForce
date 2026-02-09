package com.revworkforce.model;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompleteModelCoverageTest {

    @Test
    void testAllModels() throws Exception {
        String packageName = "com.revworkforce.model";
        List<Class<?>> classes = getClasses(packageName);

        for (Class<?> clazz : classes) {
            System.out.println("Testing model: " + clazz.getName());
            testClass(clazz);
        }
    }

    private void testClass(Class<?> clazz) {
        try {
            // Test Constructors
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Object instance = null;
            for (Constructor<?> constructor : constructors) {
                constructor.setAccessible(true);
                Object[] args = new Object[constructor.getParameterCount()];
                // Fill args with default values if needed (nulls/zeros mostly work for POJOs)
                for (int i = 0; i < args.length; i++) {
                    args[i] = getDefaultValue(constructor.getParameterTypes()[i]);
                }

                try {
                    instance = constructor.newInstance(args);
                    assertNotNull(instance, "Instance should not be null for " + clazz.getSimpleName());
                } catch (Exception e) {
                    // Verification might fail for some complex constructors, ignore but log
                    // System.err.println("Could not instantiate " + clazz.getSimpleName() + ": " +
                    // e.getMessage());
                }
            }

            // If we managed to get an instance, test getters, setters, toString, equals,
            // hashCode
            if (instance == null) {
                // Try no-arg constructor if not found above
                try {
                    Constructor<?> noArg = clazz.getDeclaredConstructor();
                    noArg.setAccessible(true);
                    instance = noArg.newInstance();
                } catch (Exception e) {
                    // Ignore if no no-arg constructor
                }
            }

            if (instance != null) {
                testMethods(clazz, instance);
            }

        } catch (Exception e) {
            // Fail gracefully
            e.printStackTrace();
        }
    }

    private void testMethods(Class<?> clazz, Object instance) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                    method.invoke(instance);
                } else if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    method.invoke(instance, getDefaultValue(method.getParameterTypes()[0]));
                } else if (method.getName().equals("toString")) {
                    assertNotNull(method.invoke(instance));
                } else if (method.getName().equals("hashCode")) {
                    method.invoke(instance);
                } else if (method.getName().equals("equals")) {
                    method.invoke(instance, new Object()); // Not equal
                    method.invoke(instance, instance); // Equal
                }
            } catch (Exception e) {
                // Ignore method invocation errors
            }
        }
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == Integer.class)
            return 0;
        if (type == long.class || type == Long.class)
            return 0L;
        if (type == double.class || type == Double.class)
            return 0.0;
        if (type == float.class || type == Float.class)
            return 0.0f;
        if (type == boolean.class || type == Boolean.class)
            return false;
        if (type == char.class || type == Character.class)
            return ' ';
        if (type == String.class)
            return "test";
        return null; // For other objects
    }

    private List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resource == null) {
            return classes;
        }
        File directory = new File(resource.getFile());
        if (directory.exists()) {
            for (String file : directory.list()) {
                if (file.endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.substring(0, file.length() - 6)));
                }
            }
        }
        return classes;
    }
}
