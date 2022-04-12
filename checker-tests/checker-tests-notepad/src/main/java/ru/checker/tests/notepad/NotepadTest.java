package ru.checker.tests.notepad;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Button;
import org.junit.jupiter.api.*;
import org.springframework.stereotype.Component;
import ru.checker.tests.desktop.base.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Notepad ++ tests.
 * @author vd.zinovev
 */
@Component("Notepad")
@Log4j2(topic = "TEST CASE")
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
        System.out.println("aaa");
        CheckerDesktopWindow root = getSApplication().getWindow("notepad_root");
        Button buttonNew = root.getButton("notepad_root_new");
        System.out.println(assertDoesNotThrow(buttonNew::getName, "Не удалось получить имя кнопки"));

    }

}
