package ru.checker.tests.ssm.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;
import ru.checker.tests.desktop.test.app.CheckerDesktopForm;
import ru.checker.tests.desktop.test.app.CheckerDesktopWindow;
import ru.checker.tests.ssm.widgets.controllers.SSMNavigationController;
import ru.checker.tests.ssm.widgets.controllers.SSMToolsController;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Log4j2(topic = "TEST CASE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMTestCase extends CheckerDesktopTestCase {

    @Getter
    CheckerDesktopWindow rootWindow;

    @Getter
    CheckerDesktopForm form;

    @Getter
    Robot robot;

    @BeforeEach
    @Override
    public void beforeEach() {
        assertDoesNotThrow(() -> {
            this.robot = new Robot();
            this.rootWindow = getSApplication().window("ssm_main");
            this.rootWindow.maximize();

            SSMNavigationController navigation = this.rootWindow.widget("ssm_navigation", SSMNavigationController.class);
            navigation.selectSapOrders();

            this.form = this.rootWindow.form("sap_orders_form");
        }, "Не удалось выбрать пункт в навигации");
    }


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
