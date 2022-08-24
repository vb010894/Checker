package ru.checker.tests.ssm.widgets;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWidget;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMNavigation {

    CheckerDesktopWidget widget;

    public SSMNavigation(CheckerDesktopWidget widget) {
        this.widget = widget;
    }


    /**
     * Root navigation item.
     */
    final String ROOT = "Тяжмаш";

    /**
     * Item 'Выпуск продукции'.
     */
    final String PRODUCT_RELEASE = "Выпуск продукции";

    /**
     * Item 'Заказы SAP'.
     */
    final String SAP_ORDERS = "Заказы SAP";

    /**
     * Item 'Заказы SAP' from rus recognizing.
     */
    final String SAP_ORDERS_RUS = "Заказы 5Ар";

    /**
     * Item 'Заказы Лоцман'.
     */
    final String LOTSMAN_ORDERS = "Заказы Лоцман";

    /**
     * Item 'Карта плавки'.
     */
    final String MELTING_MAP = "Карта плавки";

    /**
     * Item 'Справочник ОЗМ'.
     */
    final String OZM_DIRECTORY = "Справочник ОЗМ";

    /**
     * Item 'Справочник организации'.
     */
    final String ORGANIZATION_DIRECTORY = "Справочник организации";

    /**
     * Item 'Управление заданиями'.
     */
    final String TASK_MANAGEMENT = "Управление заданиями";


    /**
     * select item 'Выпуск продукции'.
     */
    public void selectProductRelease() {
        this.selectNode(PRODUCT_RELEASE);
    }

    /**
     * select item 'Заказы SAP'.
     */
    public void selectSapOrders() {
        Rectangle place = this.widget.getRectangle();
        String node = CheckerOCRUtils.getTextFromRectangle(place, CheckerOCRLanguage.ENG_RUS);
        if(!node.contains(SAP_ORDERS) && !node.contains(SAP_ORDERS_RUS))
            this.selectRoot();

        node = CheckerOCRUtils.getTextFromRectangle(place, CheckerOCRLanguage.ENG_RUS);
        if(node.contains(SAP_ORDERS))
            this.selectNode(SAP_ORDERS);
        else
            this.selectNode(SAP_ORDERS_RUS);
    }

    /**
     * select item 'Заказы Лоцман'.
     */
    public void selectOrderLotsman() {
        this.selectNode(LOTSMAN_ORDERS);
    }

    /**
     * select item 'Карта плавки'.
     */
    public void selectMapOfMelting() {
        this.selectNode(MELTING_MAP);
    }

    /**
     * select item 'Справочник O3M'.
     */
    public void selectOZMDirectory() {
        this.selectNode(OZM_DIRECTORY);
    }

    /**
     * select item 'Справочник организации'.
     */
    public void selectOrganizationDirectory() {
        this.selectNode(ORGANIZATION_DIRECTORY);
    }

    /**
     * select item 'Управление заданиями'.
     */
    public void selectTaskManagement() {
        this.selectNode(TASK_MANAGEMENT);
    }

    /**
     * Select navigation node.
     * @param name Node name
     */
    public void selectNode (String name) {
        Rectangle place = this.widget.getRectangle();
        String node = CheckerOCRUtils.getTextFromRectangle(place, CheckerOCRLanguage.ENG_RUS);
        System.out.println(node);
        if(!node.contains(name))
            this.selectRoot();

        assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выждать открытие корневого пункта навигации");
        Rectangle foundRectangle = CheckerOCRUtils.getTextAndMove(place, name, CheckerOCRLanguage.ENG_RUS);
        AutomationMouse.getInstance().setLocation((int) foundRectangle.getCenterX(), foundRectangle.y + 5 );
        AutomationMouse.getInstance().rightClick();
        assertDoesNotThrow(() -> {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }, "Не удалось выбрать элемент навигации");
    }

    /**
     * Select navigation node.
     * @param node Node
     */
    public void selectNode (SSMNavigationEnum node) {
        switch (node) {
            default:
                this.selectRoot();
                break;
            case SAP_ORDERS:
                this.selectSapOrders();
                break;
            case LOTSMAN_ORDERS:
                this.selectOrderLotsman();
                break;
            case PRODUCT_RELEASE:
                this.selectProductRelease();
                break;
            case MELTING_MAP:
                this.selectMapOfMelting();
                break;
            case ORGANIZATION_DIRECTORY:
                this.selectOrganizationDirectory();
                break;
            case OZM_DIRECTORY:
                this.selectOZMDirectory();
                break;
            case TASK_MANAGEMENT:
                this.selectTaskManagement();
                break;
        }
    }

    /**
     * Select navigation root node.
     */
    public void selectRoot() {
        Rectangle place = this.widget.getRectangle();
        Rectangle foundRectangle = CheckerOCRUtils.getTextAndMove(place, ROOT, CheckerOCRLanguage.ENG_RUS);
        AutomationMouse.getInstance().setLocation((int) foundRectangle.getCenterX(), foundRectangle.y + 5 );
        AutomationMouse.getInstance().rightClick();
        assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выждать открытие корневого пункта навигации");
        CheckerDesktopTest.getCurrentApp().waitApp();
    }

    public enum SSMNavigationEnum {

        /**
         * Root navigation item.
         */
        ROOT,

        /**
         * Item 'Выпуск продукции'.
         */
        PRODUCT_RELEASE,

        /**
         * Item 'Заказы SAP'.
         */
        SAP_ORDERS,

        /**
         * Item 'Заказы Лоцман'.
         */
        LOTSMAN_ORDERS,

        /**
         * Item 'Карта плавки'.
         */
        MELTING_MAP,

        /**
         * Item 'Справочник ОЗМ'.
         */
        OZM_DIRECTORY,

        /**
         * Item 'Справочник организации'.
         */
        ORGANIZATION_DIRECTORY,

        /**
         * Item 'Управление заданиями'.
         */
        TASK_MANAGEMENT
    }

}
