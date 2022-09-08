package ru.checker.tests.desktop.definitions;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import org.junit.jupiter.api.Test;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
class CheckerDesktopTestCaseDefinitionTest {

    @Test
    @SneakyThrows
    void testGetInstance() throws InterruptedException {

    }
}