package ru.checker.tests.ssm.widgets.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.List;
import mmarquee.automation.controls.ListItem;
import mmarquee.automation.controls.mouse.AutomationMouse;
import net.sourceforge.tess4j.ITessAPI;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;
import ru.checker.tests.desktop.test.app.CheckerDesktopWidget;

import java.awt.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2(topic = "TEST CASE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMToolsController {

    final CheckerDesktopWidget widget;
    final Color toggleEnabledColor = new Color(0, 93,163);

    public SSMToolsController(CheckerDesktopWidget widget) {
        this.widget = widget;
    }

    public void  selectCombobox(String ID, String value) {

        Rectangle rect = this.moveAngGetElementRectangle(ID);
        Map<String, Object> definition = this.widget.getElement(ID);
       /*log.info(
                "Переключение фильтра {} в статус - '{}'",
                CheckerTools.castDefinition(definition.get("buttonName")),
                (isOn)? "Включен" : "Выключен");*/
        AutomationMouse.getInstance().setLocation((int) rect.getMaxX() + 20, (int) rect.getCenterY());
        AutomationMouse.getInstance().doubleLeftClick();
        assertDoesNotThrow(() -> {
            List list = UIAutomation.getInstance().getDesktop().getList(0);
            ListItem item = list.getItem(value);
            item.click();
        }, "Не удалось выбрать элемент списка комбобокса. ID - " + ID);

    }

    public void clickButton(String ID) {
        this.moveAngGetElementRectangle(ID);
        Map<String, Object> definition = this.widget.getElement(ID);
        log.info(
                "Нажатие на кнопку {}",
                (String) CheckerTools.castDefinition(definition.get("buttonName")));
        AutomationMouse.getInstance().leftClick();
        log.info("Кнопка нажата");

    }

    public void toggle(String ID, boolean isOn) {

        Rectangle rect = this.moveAngGetElementRectangle(ID);
        Map<String, Object> definition = this.widget.getElement(ID);
        log.info(
                "Переключение фильтра {} в статус - '{}'",
                CheckerTools.castDefinition(definition.get("buttonName")),
                        (isOn)? "Включен" : "Выключен");
        Color color = assertDoesNotThrow(() -> new Robot().getPixelColor((int) rect.getMaxX() + 20, (int) rect.getCenterY()));
        AutomationMouse.getInstance().setLocation((int) rect.getMaxX() + 20, (int) rect.getCenterY());
        if(color.equals(this.toggleEnabledColor) & !isOn) {
            log.info("Переключение на статус выключен");
            AutomationMouse.getInstance().leftClick();
        }

        if(!color.equals(this.toggleEnabledColor) & isOn) {
            log.info("Переключение на статус включен");
            AutomationMouse.getInstance().leftClick();
        }

        assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выполнить ожидание элемента");
        CheckerDesktopTestCase.getSApplication().waitApp();

        log.info("Переключение успешно");
    }

    private Rectangle moveAngGetElementRectangle(String ID) {
        Map<String, Object> definition = this.widget.getElement(ID);
        assertTrue(definition.containsKey("buttonName"), "Для нажатия кнопки должен быть заполнен ключ - 'buttonName'");
        String button = CheckerTools.castDefinition(definition.get("buttonName"));
        Rectangle place = assertDoesNotThrow(
                () -> this.widget.firstElement(ID).getBoundingRectangle().toRectangle(),
                "Не удалось получить положение родительской панели. ID - " + this.widget.getOutName());
        return CheckerOCRUtils.getTextAndMove(place, button, ITessAPI.TessPageIteratorLevel.RIL_WORD);
    }
}
