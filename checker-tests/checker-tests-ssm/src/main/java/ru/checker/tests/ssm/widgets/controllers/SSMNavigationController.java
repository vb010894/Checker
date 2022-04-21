package ru.checker.tests.ssm.widgets.controllers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;
import ru.checker.tests.desktop.test.app.CheckerDesktopWidget;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@SuppressWarnings("unused")
public class SSMNavigationController {

    final CheckerDesktopWidget widget;

    final String ROOT = "Тяжмаш MES - PREPROD";
    final String PRODUCT_RELEASE = "Выпуск продукции";
    final String SAP_ORDERS = "Заказы SAP";
    final String LOTSMAN_ORDERS = "Заказы Лоцман";
    final String MELTING_MAP = "Заказы Лоцман";
    final String OZM_DIRECTORY = "Справочник ОЗМ";
    final String ORGANIZATION_DIRECTORY = "Справочник организации";
    final String TASK_MANAGEMENT = "Справочник организации";

    public SSMNavigationController(CheckerDesktopWidget widget) {
        this.widget = widget;
    }

    public void selectProductRelease() {
        this.selectNode(PRODUCT_RELEASE);
    }

    public void selectSapOrders() {
        this.selectNode(SAP_ORDERS);
    }

    public void selectOrderLotsman() {
        this.selectNode(LOTSMAN_ORDERS);
    }

    public void selectMapOfMelting() {
        this.selectNode(MELTING_MAP);
    }

    public void selectOZMDirectory() {
        this.selectNode(OZM_DIRECTORY);
    }

    public void selectOrganizationDirectory() {
        this.selectNode(ORGANIZATION_DIRECTORY);
    }

    public void selectTaskManagement() {
        this.selectNode(TASK_MANAGEMENT);
    }

    private void selectNode (String name) {
        Rectangle place = this.widget.getRectangle();
        String node = CheckerOCRUtils.getTextFromRectangle(place, CheckerOCRLanguage.ENG_RUS);
        if(!node.contains(name))
            this.selectRoot();
        Rectangle foundRectangle = CheckerOCRUtils.getTextAndMove(place, name, CheckerOCRLanguage.ENG_RUS);
        AutomationMouse.getInstance().setLocation((int) foundRectangle.getCenterX(), foundRectangle.y + 5 );
        AutomationMouse.getInstance().rightClick();
        AutomationMouse.getInstance().doubleLeftClick();
        assertDoesNotThrow(() -> {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }, "Не удалось выбрать элемент навигации");
    }

    public void selectRoot() {
        Rectangle place = this.widget.getRectangle();
        Rectangle foundRectangle = CheckerOCRUtils.getTextAndMove(place, ROOT, CheckerOCRLanguage.ENG_RUS);
        AutomationMouse.getInstance().setLocation((int) foundRectangle.getCenterX(), foundRectangle.y + 5 );
        AutomationMouse.getInstance().rightClick();
        CheckerDesktopTestCase.getSApplication().waitApp();
    }

}
