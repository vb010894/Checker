package ru.checker.tests.ssm.temp.widgets;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.List;
import mmarquee.automation.controls.ListItem;
import mmarquee.automation.controls.mouse.AutomationMouse;
import net.sourceforge.tess4j.ITessAPI;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWidget;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;

import java.awt.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class SSMTools {

    /**
     * Current widget.
     */
    final CheckerDesktopWidget widget;

    /**
     * Toggle state color locator.
     */
    final Color toggleEnabledColor = new Color(0, 93, 163);

    /**
     * Constructor.
     * @param widget widget
     */
    public SSMTools(CheckerDesktopWidget widget) {
        this.widget = widget;
    }


    /**
     * Select value from ssm combobox.
     * @param ID Combobox item ID
     * @param value Required value
     */
    public void selectCombobox(String ID, String value) {
        Rectangle rect = this.moveAngGetElementRectangle(ID);
        Map<String, Object> definition = this.widget.getElementDefinition(ID);
        log.info(
                "Выбор значения комбобокса {}. Значение - '{}'",
                CheckerTools.castDefinition(definition.get("buttonName")), value);
        AutomationMouse.getInstance().setLocation((int) rect.getMaxX() + 20, (int) rect.getCenterY());
        AutomationMouse.getInstance().doubleLeftClick();
        assertDoesNotThrow(() -> {
            List list = UIAutomation.getInstance().getDesktop().getList(0);
            ListItem item = list.getItem(value);
            AutomationMouse.getInstance().setLocation(item.getClickablePoint());
            Thread.sleep(1000);
            AutomationMouse.getInstance().leftClick();
            CheckerDesktopTest.getCurrentApp().waitApp();
            Thread.sleep(1000);
        }, "Не удалось выбрать элемент списка комбобокса. ID - " + ID);
        log.info("Значение выбрано успешно");
    }

    /**
     * Click menu button.
     * @param ID Button ID
     */
    public void clickButton(String ID) {
        this.moveAngGetElementRectangle(ID);
        Map<String, Object> definition = this.widget.getElementDefinition(ID);
        log.info(
                "Нажатие на кнопку {}",
                (String) CheckerTools.castDefinition(definition.get("buttonName")));
        AutomationMouse.getInstance().leftClick();
        log.info("Кнопка нажата");

    }

    /**
     * Change menu toggle's state
     * @param ID Toggle ID
     * @param isOn State
     */
    public void toggle(String ID, boolean isOn) {

        Rectangle rect = this.moveAngGetElementRectangle(ID);
        Map<String, Object> definition = this.widget.getElementDefinition(ID);
        log.info(
                "Переключение фильтра {} в статус - '{}'",
                CheckerTools.castDefinition(definition.get("buttonName")),
                (isOn) ? "Включен" : "Выключен");
        Color color = assertDoesNotThrow(() -> new Robot().getPixelColor((int) rect.getMaxX() + 20, (int) rect.getCenterY()));
        AutomationMouse.getInstance().setLocation((int) rect.getMaxX() + 15, (int) rect.getCenterY());
        if (color.equals(this.toggleEnabledColor) & !isOn) {
            log.info("Переключение на статус выключен");
            assertDoesNotThrow(() -> Thread.sleep(1000));
            AutomationMouse.getInstance().leftClick();
        }

        if (!color.equals(this.toggleEnabledColor) & isOn) {
            log.info("Переключение на статус включен");
            assertDoesNotThrow(() -> Thread.sleep(1000));
            AutomationMouse.getInstance().leftClick();
        }

        assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выполнить ожидание элемента");
        CheckerDesktopTest.getCurrentApp().waitApp();

        log.info("Переключение успешно");
    }

    /**
     * Move to menu element
     * @param ID Element ID
     * @return Element rectangle
     */
    private Rectangle moveAngGetElementRectangle(String ID) {
        Map<String, Object> definition = this.widget.getElementDefinition(ID);
        assertTrue(definition.containsKey("buttonName"), "Для нажатия кнопки должен быть заполнен ключ - 'buttonName'");
        String button = CheckerTools.castDefinition(definition.get("buttonName"));
        Rectangle place = assertDoesNotThrow(
                () -> this.widget.panel(ID).getBoundingRectangle().toRectangle(),
                "Не удалось получить положение родительской панели. ID - " + this.widget.getID());
        return CheckerOCRUtils.getTextAndMove(place, button, ITessAPI.TessPageIteratorLevel.RIL_WORD);
    }


}
