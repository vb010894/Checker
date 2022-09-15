package ru.checker.starter;

import com.sun.jna.platform.win32.User32;
import lombok.SneakyThrows;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.checker.tests.desktop.utils.CheckerDesktopMarker;

class CheckerStarterApplicationTests {

    @SneakyThrows
    @Test
    void contextLoads() {
        var handle = User32.INSTANCE.FindWindow("TcxFilterDialog", null);
        Element w = UIAutomation.getInstance().getElementFromHandle(handle);
        new CheckerDesktopMarker(w.getBoundingRectangle().toRectangle()).draw();
        System.out.println(ControlType.fromValue(w.getControlType()).toString());
    }

}
