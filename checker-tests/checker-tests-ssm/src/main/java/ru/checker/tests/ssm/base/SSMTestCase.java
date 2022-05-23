package ru.checker.tests.ssm.base;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;
import ru.checker.tests.desktop.test.app.CheckerDesktopForm;
import ru.checker.tests.desktop.test.app.CheckerDesktopWindow;
import ru.checker.tests.ssm.widgets.controllers.SSMNavigationController;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import javax.imageio.ImageIO;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
public class SSMTestCase extends CheckerDesktopTestCase {

    List<String> popupWindowTitles = List.of(
            "Ssm"
    );

    /**
     * Root window.
     */
    @Getter
    CheckerDesktopWindow rootWindow;

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
    @BeforeEach
    @Override
    public void beforeEach() {
        assertDoesNotThrow(() -> {
            this.robot = new Robot();
            this.rootWindow = getSApplication().window("ssm_main");
            this.rootWindow.maximize();

            assertTrue(getConstants().containsKey("environment"), "Не задан путь к главной форме");
            String env = CheckerTools.castDefinition(getConstants().get("environment"));
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
    @AfterEach
    @Override
    public void afterEach() {
        AtomicInteger index = new AtomicInteger(0);
        WinDef.HWND handle = User32.INSTANCE.FindWindow(null, "Ssm");
        Window test = new Window(new ElementBuilder().handle(handle));
        try {
            test.getChildren(true).forEach(c -> {
                try {
                    System.out.println(c.getClassName());
                } catch (AutomationException e) {
                    e.printStackTrace();
                }
            });
            if(test.getButton("Закрыть") != null)
                test.getButton("Закрыть").click();
            else
                test.close();
        } catch (AutomationException e) {
            e.printStackTrace();
        }
        this.popupWindowTitles.parallelStream().forEach(popup -> User32.INSTANCE.DestroyWindow(User32.INSTANCE.FindWindow(null, popup)));

        assertDoesNotThrow(() -> {
            this.form.getControl()
                    .getChildren(true)
                    .parallelStream()
                    .filter(element -> {
                        try {
                            ControlType type = ControlType.fromValue(element.getElement().getControlType());
                            System.out.println(type.toString());
                            return type.equals(ControlType.Window);
                        } catch (AutomationException e) {
                            return false;
                        }
                    })
                    .forEach(window -> {
                        index.getAndIncrement();
                        Window w = new Window(new ElementBuilder().element(window.getElement()));
                        try {
                            log.info("Закрытие всплывающего окна");
                            System.out.println("Осталось диалоговое окно");
                            w.focus();
                            BufferedImage image = this.robot.createScreenCapture(w.getBoundingRectangle().toRectangle());
                            File out = new File(CheckerTools.getRootPath() + "/Reports/Images/dialog-window" + index.get() + ".bmp");
                            if(!out.getParentFile().exists())
                                if(!out.getParentFile().mkdirs())
                                    log.warn("Не удалось создать папку со скриншотами");
                            ImageIO.write(image, "bmp", out);
                            if(!User32.INSTANCE.CloseWindow(window.getNativeWindowHandle()))
                                log.warn("Не удалось закрыть всплывающее окно");
                            log.info("Всплывающее окно закрыто");
                        } catch (AutomationException e) {
                            log.warn("Не удалось закрыть диалоговое окно");
                        } catch (IOException e) {
                            log.warn("Не удалось сохранить скриншот всплывающей формы");
                        }
                    });
            log.info("Закрытие формы. Форма - " + this.form.getOutName());
            SSMToolsController menu = this.rootWindow.widget("ssm_menu", SSMToolsController.class);
            menu.clickButton("info_exit");
        }, "Не удалось закрыть форму" + this.form.getOutName());
        log.info("Форма закрыта");
    }
}
