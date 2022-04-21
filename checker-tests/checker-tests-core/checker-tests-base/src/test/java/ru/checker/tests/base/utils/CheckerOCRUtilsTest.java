package ru.checker.tests.base.utils;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CheckerOCRUtils methods.
 * @author vd.zinovev
 * @see CheckerOCRUtils class under test
 */
class CheckerOCRUtilsTest {

    /**
     * Positive geting recognized text from input stream.
     */
    @Test
    void getTextFromRectangle() {
        assertAll(
                    () -> assertEquals(
                            CheckerOCRUtils.getTextFromInputStream(getClass().getResourceAsStream("/forOCR/add.png")),
                            new String("Добавить".getBytes(), StandardCharsets.UTF_8),
                            "Найдено несовпадение при распознование текста"),
                    () -> assertEquals(
                            CheckerOCRUtils.getTextFromInputStream(getClass().getResourceAsStream("/forOCR/agp.png")),
                            "АГП",
                            "Найдено несовпадение при распознование текста"),
                    () -> assertEquals(
                            CheckerOCRUtils.getTextFromInputStream(getClass().getResourceAsStream("/forOCR/Date.png")),
                            "04.04.2022",
                            "Найдено несовпадение при распознование текста"),
                    () -> assertEquals(
                            CheckerOCRUtils.getTextFromInputStream(getClass().getResourceAsStream("/forOCR/environment.png")),
                            "Среда",
                            "Найдено несовпадение при распознование текста")
                );
    }

    /**
     * Positive recognized test from file.
     */
    @Test
    void testGetTextFromFile() {
        File f = assertDoesNotThrow(() -> new File(Objects.requireNonNull(getClass().getResource("/forOCR/environment.png")).toURI()));
        assertEquals(
                CheckerOCRUtils.getTextFromFile(f),
                "Среда",
                "Найдено несовпадение при распознование текста");
    }

    /**
     * Positive recognized test from rectangle
     */
    @Test
    void testGetTextFromRectangle() {
        assertDoesNotThrow(() -> CheckerOCRUtils.getTextFromRectangle(new Rectangle(0, 0, 500, 500)));
    }

    /**
     * Negative test with null values.
     */
    @Test
    void negativeNull() {
        assertAll("Ошибочное исключение при 'NULL' параметрах распознования",
                () -> assertThrows(AssertionFailedError.class, () -> CheckerOCRUtils.getTextFromInputStream(null)),
                () -> assertThrows(AssertionFailedError.class, () -> CheckerOCRUtils.getTextFromRectangle(null)),
                () -> assertThrows(AssertionFailedError.class, () -> CheckerOCRUtils.getTextFromFile(null)));

    }
}