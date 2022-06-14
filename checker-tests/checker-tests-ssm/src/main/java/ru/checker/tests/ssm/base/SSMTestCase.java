package ru.checker.tests.ssm.base;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;
import ru.checker.tests.desktop.test.app.CheckerDesktopForm;
import ru.checker.tests.desktop.test.app.CheckerDesktopWindow;
import ru.checker.tests.ssm.widgets.controllers.SSMNavigationController;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SSM Test case base.
 *
 * 1) start form.
 * 2) close form.
 * 3) storage root window
 * 4) storage root form
 *
 * @author vd.zinovev
 */
@Log4j2(topic = "TEST CASE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SSMTestCase extends CheckerDesktopTestCase {

    List<String> popupWindowTitles = List.of(
            "Ssm"
    );

    /**
     * Root window.
     */
    @Getter
    CheckerDesktopWindow rootWindow;

    /**
     * Root window handle.
     */
    WinDef.HWND rootWindowHandle;

    /**
     * Root form.
     */
    @Getter
    CheckerDesktopForm form;

    /**
     * AWT Robot.
     */
    @Getter
    Robot robot;

    /**
     * Before each test.
     * 1) start main form.
     * 2) storage form
     * 3) storage window
     */
    @Parameters({"constants.environment"})
    @BeforeMethod
    public void beforeEach(String environment) {
        assertDoesNotThrow(() -> {
            this.robot = new Robot();
            this.rootWindow = getSApplication().window("ssm_main");
            this.rootWindowHandle = rootWindow.getControl().getNativeWindowHandle();
            this.rootWindow.maximize();

            String env = CheckerTools.castDefinition(environment);
            SSMNavigationController.SSMNavigation nav = SSMNavigationController.SSMNavigation.valueOf(env);
            SSMNavigationController navigation = this.rootWindow.widget("ssm_navigation", SSMNavigationController.class);
            navigation.selectNode(nav);
            // TODO: 06.05.2022 Убрать хард код!!!
            this.form = this.rootWindow.form("mf");
        }, "Не удалось выбрать пункт в навигации");
    }

    /**
     * After each test.
     *
     * 1) close app
     */
    @AfterMethod
    @Override
    public void afterEach() {
        this.closePopupWindows();
        log.info("Закрытие формы. Форма - " + this.form.getOutName());
        SSMToolsController menu = this.rootWindow.widget("ssm_menu", SSMToolsController.class);
        menu.clickButton("info_exit");
        log.info("Форма закрыта");
    }

    private void closePopupWindows() {
        log.info("Закрытие всплывающих окон");
        assertDoesNotThrow(() -> Thread.sleep(5000));
        WinDef.HWND handle = User32.INSTANCE.GetForegroundWindow();
        if(handle != null && !handle.equals(this.rootWindowHandle)) {
            BufferedImage image = this.robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            File out = new File(CheckerTools.getRootPath() + "/Reports/Images/dialog-windows.bmp");
            if(!out.getParentFile().exists())
                if(!out.getParentFile().mkdirs())
                    log.warn("Не удалось создать папку со скриншотами");
            try {
                ImageIO.write(image, "bmp", out);
            } catch (IOException e) {
                log.error("Не удалось сохранить скриншот всплывающих окон");
            }
        }

        while (handle != null && !handle.equals(this.rootWindowHandle)) {
            WinDef.HWND temp = handle;
            Element el = assertDoesNotThrow(() -> UIAutomation.getInstance().getElementFromHandle(temp), "Не удалось получить активное окно");
            try {
                if (el.getControlType() == ControlType.Window.getValue()) {
                    assertDoesNotThrow(() -> new Window(new ElementBuilder().element(el)).close());
                    assertDoesNotThrow(() -> Thread.sleep(1000));
                    handle = User32.INSTANCE.GetForegroundWindow();
                }else {handle = null;}
            } catch (AutomationException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Окна закрыты");
    }
}
