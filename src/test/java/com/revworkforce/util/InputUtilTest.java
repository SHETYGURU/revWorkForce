package com.revworkforce.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class InputUtilTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        // Reset System.in and restore original scanner if needed (simulated)
        // Since we modified InputUtil to take a scanner, we don't strictly need to
        // restore System.in
        // but it's good practice if we were using System.setIn()
        // Here we just set a new scanner on InputUtil based on System.in to restore
        // state (or create a dummy one)
        InputUtil.setScanner(new Scanner(System.in));
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        InputUtil.setScanner(new Scanner(testIn));
    }

    @Test
    void testReadString() {
        provideInput("  Hello World  \n");
        String result = InputUtil.readString("Enter string: ");
        assertEquals("Hello World", result);
    }

    @Test
    void testReadInt_Valid() {
        provideInput("123\n");
        int result = InputUtil.readInt("Enter int: ");
        assertEquals(123, result);
    }

    @Test
    void testReadInt_InvalidThenValid() {
        provideInput("abc\n456\n");
        int result = InputUtil.readInt("Enter int: ");
        assertEquals(456, result);
    }

    @Test
    void testReadValidatedString_Predicate_Valid() {
        provideInput("valid\n");
        Predicate<String> validator = s -> s.equals("valid");
        String result = InputUtil.readValidatedString("Prompt: ", validator, "Error");
        assertEquals("valid", result);
    }

    @Test
    void testReadValidatedString_Predicate_InvalidThenValid() {
        provideInput("invalid\nvalid\n");
        Predicate<String> validator = s -> s.equals("valid");
        String result = InputUtil.readValidatedString("Prompt: ", validator, "Error");
        assertEquals("valid", result);
    }

    @Test
    void testReadValidatedString_Function_Valid() {
        provideInput("correct\n");
        Function<String, String> validator = s -> "correct".equals(s) ? null : "Error message";
        String result = InputUtil.readValidatedString("Prompt: ", validator);
        assertEquals("correct", result);
    }

    @Test
    void testReadValidatedString_Function_InvalidThenValid() {
        provideInput("wrong\ncorrect\n");
        Function<String, String> validator = s -> "correct".equals(s) ? null : "Error message";
        String result = InputUtil.readValidatedString("Prompt: ", validator);
        assertEquals("correct", result);
    }

    @Test
    void testClose() {
        // Use a dummy scanner to avoid closing System.in
        InputUtil.setScanner(new Scanner("dummy"));
        assertDoesNotThrow(InputUtil::close);

        // Restore for other tests (though tearDown handles this, good to be
        // explicit/safe)
        InputUtil.setScanner(new Scanner(System.in));
    }

    @Test
    void testPrivateConstructor() {
        // Reflection check for full coverage
        assertDoesNotThrow(() -> {
            java.lang.reflect.Constructor<InputUtil> constructor = InputUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertNotNull(constructor.newInstance());
        });
    }
}
