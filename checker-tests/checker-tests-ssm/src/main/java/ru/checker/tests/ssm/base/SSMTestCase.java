package ru.checker.tests.ssm.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;
import ru.checker.tests.desktop.test.app.CheckerDesktopForm;
import ru.checker.tests.desktop.test.app.CheckerDesktopWindow;
import ru.checker.tests.ssm.widgets.controllers.SSMNavigationController;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import java.awt.*;

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

            this.form = this.rootWindow.form("sap_orders_form");
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
        log.info("Закрытие формы. Форма - " + this.form.getOutName());
        assertDoesNotThrow(() -> {
            SSMToolsController menu = this.rootWindow.widget("ssm_menu", SSMToolsController.class);
            menu.clickButton("info_exit");
        }, "Не удалось закрыть форму" + this.form.getOutName());
        log.info("Форма закрыта");
    }
}
