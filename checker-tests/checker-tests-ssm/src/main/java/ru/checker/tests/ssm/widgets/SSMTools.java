package ru.checker.tests.ssm.widgets;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.List;
import mmarquee.automation.controls.ListItem;
import mmarquee.automation.controls.mouse.AutomationMouse;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Word;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWidget;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     *
     * @param widget widget
     */
    public SSMTools(CheckerDesktopWidget widget) {
        this.widget = widget;
    }

    public java.util.List<Map.Entry<String, Rectangle>> getTabs(int wordSpace) {
        Rectangle rectangle = this.widget.getRectangle();
        int x = rectangle.x;
        int y = rectangle.y - 30;
        int width = rectangle.width;
        int height = 30;
        Rectangle tabsRectangle = new Rectangle(x, y, width, height);
        java.util.List<Word> words = CheckerOCRUtils.getWords(tabsRectangle, CheckerOCRLanguage.RUS);
        return this.concatTabWords(tabsRectangle, words, wordSpace);
    }

    private java.util.List<Map.Entry<String, Rectangle>> concatTabWords(Rectangle parent, java.util.List<Word> words, int wordSpace) {
        if (Objects.nonNull(words) && words.size() == 0)
            return null;
        if (words.size() == 1)
            return Collections.singletonList(Map.entry(words.get(0).getText(), words.get(0).getBoundingBox()));

        Word[] wordsArray = words.toArray(new Word[words.size()]);
        Rectangle last = words.get(0).getBoundingBox();
        java.util.List<Map.Entry<String, Rectangle>> result = new LinkedList<>();
        StringBuilder tabString = new StringBuilder(words.get(0).getText());

        for (int i = 1; i < wordsArray.length; i++) {
            Rectangle target = wordsArray[i].getBoundingBox();
            if(Pattern.compile("[A-Za-zА-Яа-я0-9]").matcher(wordsArray[i].getText()).lookingAt()) {
                if (Math.abs(last.x - target.x) <= wordSpace) {
                    tabString.append(" ").append(wordsArray[i].getText());
                } else {
                    int x = parent.x + (last.x / 3);
                    int y = parent.y + (last.y / 3);
                    int width = last.width / 3;
                    int height = last.height / 3;
                    result.add(Map.entry(tabString.toString().replaceAll("[^A-Za-zА-Яа-я0-9., ]", "").trim(), new Rectangle(x, y, width, height)));
                    tabString = new StringBuilder(wordsArray[i].getText());
                    last = target;
                }
            }
        }
        result.add(Map.entry(tabString.toString().replaceAll("[^A-Za-zА-Яа-я., ]", "").trim(), wordsArray[wordsArray.length - 1].getBoundingBox()));
        return result;

    }


    /**
     * Select value from ssm combobox.
     *
     * @param ID    Combobox item ID
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
            List list = UIAutomation.getInstance().getDesktop().getList("");
            log.debug(
                    "Найдены элементы листа:\n{}",
                    list.getItems().parallelStream().map(i -> {
                        try {
                            return i.getName();
                        } catch (AutomationException e) {
                            return "*Без имени*";
                        }
                    }).collect(Collectors.joining("\n", "Элемент - '", "'")));


            ListItem item = list.getItem(value);
            AutomationMouse.getInstance().setLocation(item.getClickablePoint());
            Thread.sleep(50);
            AutomationMouse.getInstance().leftClick();
            CheckerDesktopTest.getCurrentApp().waitApp();
            Thread.sleep(1000);
        }, "Не удалось выбрать элемент списка комбобокса. ID - " + ID);
        log.info("Значение выбрано успешно");
    }

    /**
     * Click menu button.
     *
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
     *
     * @param ID   Toggle ID
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
     *
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
