package ru.checker.tests.notepad;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.stereotype.Component;

import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;

/**
 * Notepad ++ tests.
 * @author vd.zinovev
 */
@Component("Notepad")
@Log4j2(topic = "[Test case]")
@DisplayName("Тесты блокнота")
public class NotepadTest extends CheckerDesktopTestCase {

    // Test case definition.
    static {
        prepare("Notepad");
    }

    /**
     * Test notepad.
     * for example.
     */
    @Test
    @DisplayName("Простой тест")
    public void simpleTest() {
        log.info("notepad first");
    }

    @Disabled
    @Test
    @DisplayName("Пропущеный тест")
    void disabledTest() {
        log.warn("disabled");
    }

    @Test
    @DisplayName("Пропущеный тест")
    void assumedTest() {
        Assumptions.assumeTrue(false, "Тест пропущен");
    }

    @Test
    @DisplayName("Тест с ошибкой")
    void errorTest() {
        Assertions.fail("Ошибка");
    }

}
