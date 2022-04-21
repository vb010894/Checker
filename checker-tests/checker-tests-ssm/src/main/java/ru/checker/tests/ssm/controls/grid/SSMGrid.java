package ru.checker.tests.ssm.controls.grid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Log4j2(topic = "TEST CASE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMGrid {

    final Panel control;
    SSMGridData data;
    Rectangle headerRectangle;
    final Color headerColor = new Color(244, 244, 244);
    final Robot robot;

    public SSMGrid(Panel control) {
        this.control = control;
        this.robot = assertDoesNotThrow(() -> new Robot(), "Не удалось получить доступ к клавиатуре");
        this.getAllData();
    }

    private void focus() {
        log.debug("Фокус над таблицей");
        assertDoesNotThrow(() -> {
            int limit = 60000;
            while (limit >= 0) {
                try {
                    Rectangle controlRect = this.control.getBoundingRectangle().toRectangle();
                    AutomationMouse.getInstance().setLocation(controlRect.x + 5, (int) controlRect.getCenterY());
                    AutomationMouse.getInstance().leftClick();

                    this.robot.keyPress(KeyEvent.VK_PAGE_UP);
                    this.robot.keyRelease(KeyEvent.VK_PAGE_UP);
                    break;
                } catch (Exception ex) {
                    Thread.sleep(1000);
                    limit -= 1000;
                }
            }

        });
        log.debug("Таблица сфокусирована");
    }

    public SSMGridData getFirstPageData() {
        log.info("Чтение данных таблицы из первой страницы");
        CheckerTools.clearClipboard();
        this.data = new SSMGridData("");
        String stringData = "";
        this.focus();
        int limit = 10000;
        while (stringData.equals("") & limit >= 0) {

            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_A);
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.keyRelease(KeyEvent.VK_A);

            this.robot.keyPress(KeyEvent.VK_SHIFT);
            this.robot.keyPress(KeyEvent.VK_PAGE_DOWN);
            this.robot.keyRelease(KeyEvent.VK_SHIFT);
            this.robot.keyRelease(KeyEvent.VK_PAGE_DOWN);

            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_C);
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.keyRelease(KeyEvent.VK_C);

            stringData = assertDoesNotThrow(() -> Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString(), "Не удалось получить данные из буфера");
            if (stringData.equals("")) {
                assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выдержать паузу");
                limit -= 1000;
            } else {
                break;
            }
        }
        log.info("Данные таблицы успешно прочитаны");

        log.debug("Конвертация данных");
        this.data = new SSMGridData(stringData);
        this.data.convert();
        log.debug("Данные успешно конвертированы.\nКоличество колонок - {}\nКоличество строк - {}", this.data.getHeaderSize(), this.data.getRowSize());

        return this.data;
    }

    public SSMGridData getAllData() {
        log.info("Чтение данных таблицы");
        CheckerTools.clearClipboard();
        this.data = new SSMGridData("");
        String stringData = "";
        this.focus();
        int limit = 10000;
        while (stringData.equals("") & limit >= 0) {
            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_A);
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.keyRelease(KeyEvent.VK_A);

            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_C);
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.keyRelease(KeyEvent.VK_C);

            CheckerDesktopTestCase.getSApplication().waitApp();

            stringData = assertDoesNotThrow(() -> Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString(), "Не удалось получить данные из буфера");
            if (stringData.equals("")) {
                assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выдержать паузу");
                limit -= 1000;
            } else {
                break;
            }
        }
        log.info("Данные таблицы успешно прочитаны");

        log.debug("Конвертация данных");
        this.data = new SSMGridData(stringData);
        this.data.convert();
        log.debug("Данные успешно конвертированы.\nКоличество колонок - {}\nКоличество строк - {}", this.data.getHeaderSize(), this.data.getRowSize());

        return this.data;
    }

    public boolean columnExistValue(String columnName, String value) {
        log.info("Проверка колонки '{}' на наличие значения '{}'", columnName, value);
        boolean result = this.data.getColumnData(columnName).parallelStream().allMatch(value::equalsIgnoreCase);
        log.info("Колонка '{}' содержит {} все значения равные '{}'", columnName, (result) ? "" : "не", value);
        return result;
    }


}
