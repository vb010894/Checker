package ru.checker.tests.ssm.controls.grid;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.Window;
import mmarquee.automation.controls.mouse.AutomationMouse;
import org.junit.jupiter.api.function.ThrowingSupplier;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.CheckerDesktopTestCase;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2(topic = "TEST CASE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("ConstantConditions")
public class SSMGrid {

    final Color headerColor = new Color(244, 244, 244);
    final Color acceptedRowColor = new Color(255, 85, 85);

    final Panel control;
    final Robot robot;

    SSMGridData data;
    Rectangle headerRectangle;


    public SSMGrid(Panel control) {
        this.control = control;
        this.robot = assertDoesNotThrow((ThrowingSupplier<Robot>) Robot::new, "Не удалось получить доступ к клавиатуре");
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

        WinDef.HWND handle = User32.INSTANCE.FindWindow(null, "Ssm");
        if (handle != null) {
            assertDoesNotThrow(() -> {
               Element windowRaw = UIAutomation.getInstance().getElementFromHandle(handle);
               Window rejectWindow = new Window(new ElementBuilder().element(windowRaw));
               rejectWindow.close();
            }, "Не удалось закрыть окно отказа в доступе");
        }

        log.debug("Конвертация данных");
        this.data = new SSMGridData(stringData);
        this.data.convert();
        log.debug("Данные успешно конвертированы.\nКоличество колонок - {}\nКоличество строк - {}", this.data.getHeaderSize(), this.data.getRowSize());

        return this.data;
    }

    /**
     * Check all values equals required in column
     *
     * @param columnName Column name
     * @param value      Searching value
     * @return Check result
     */
    public boolean columnExistValue(String columnName, String value) {
        log.info("Проверка колонки '{}' на наличие значения '{}'", columnName, value);
        boolean result = this.data.getColumnData(columnName).parallelStream().allMatch(value::equalsIgnoreCase);
        log.info("Колонка '{}' содержит {} все значения равные '{}'", columnName, (result) ? "" : "не", value);
        return result;
    }

    /**
     * Check grid has data.
     * @return Check result
     */
    public boolean hasData() {
        log.info("Проверка наличия данных в таблице");
        SSMGridData data = this.getAllData();
        log.debug("Обнаружено\nКолонок - '{}'\nСтрок - {}", data.getHeaderSize(), data.getRowSize());
        return data.getRowSize() > 0L;
    }

    /**
     * Check grid hasn't data.
     * @return Check result
     */
    public boolean hasNotData() {
        log.info("Проверка отсутствия данных в таблице");
        SSMGridData data = this.getAllData();
        return data.getRowSize() == 0L;
    }

    /**
     * Select row by index
     *
     * @param index Current index
     */
    public void selectRow(int index) {
        assertDoesNotThrow(() -> {
            this.focus();
            log.info("Выбор {} строки в таблице", index + 1);
            this.robot.keyPress(KeyEvent.VK_PAGE_UP);
            this.robot.keyRelease(KeyEvent.VK_PAGE_UP);

            this.robot.keyPress(KeyEvent.VK_HOME);
            this.robot.keyRelease(KeyEvent.VK_HOME);

            for (int i = 0; i < index; i++) {
                this.robot.keyPress(KeyEvent.VK_DOWN);
                this.robot.keyRelease(KeyEvent.VK_DOWN);
                Thread.sleep(2);
            }
            log.info("Строка выбрана");
        }, "Не удалось выбрать строку");

    }

    /**
     * Accept cell by index
     *
     * @param index Current index
     */
    public SSMGridData selectAndAcceptCell(int index) {
        this.selectRow(index);
        return assertDoesNotThrow(() -> {
            log.info("Выделение {} строки", index + 1);
            this.robot.keyPress(KeyEvent.VK_ENTER);
            this.robot.keyRelease(KeyEvent.VK_ENTER);

            Thread.sleep(2000);
            Element cell = UIAutomation.getInstance().getFocusedElement();
            Rectangle cellRectangle = cell.getBoundingRectangle().toRectangle();
            AutomationMouse.getInstance().setLocation(cellRectangle.x, cellRectangle.y);

            this.robot.keyPress(KeyEvent.VK_ESCAPE);
            this.robot.keyRelease(KeyEvent.VK_ESCAPE);

            this.robot.keyPress(KeyEvent.VK_CONTROL);
            AutomationMouse.getInstance().leftClick();
            this.robot.keyRelease(KeyEvent.VK_CONTROL);

            log.info("Проверка выделения");
            boolean found = false;
            for (int i = cellRectangle.x; i < (int) this.control.getBoundingRectangle().toRectangle().getMaxX(); i++) {
                if (this.robot.getPixelColor(i, (int) cellRectangle.getCenterY()).equals(this.acceptedRowColor)) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "Строка не была выделена");
            log.info("Строка успешно выделена");

            log.info("Считывание выделенной строки");
            CheckerTools.clearClipboard();
            AutomationMouse.getInstance().setLocation(this.control.getBoundingRectangle().toRectangle().x + 5, (int) cellRectangle.getCenterY());
            AutomationMouse.getInstance().leftClick();

            this.robot.keyPress(KeyEvent.VK_ESCAPE);
            this.robot.keyRelease(KeyEvent.VK_ESCAPE);

            this.robot.keyPress(KeyEvent.VK_SHIFT);
            this.robot.keyPress(KeyEvent.VK_END);
            this.robot.keyRelease(KeyEvent.VK_SHIFT);
            this.robot.keyRelease(KeyEvent.VK_END);

            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_C);
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.keyRelease(KeyEvent.VK_C);

            String stringData = "";
            int limit = 10000;
            while (stringData.equals("") & limit >= 0) {
                stringData = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
                if (!stringData.equals("")) {
                    break;
                }
                Thread.sleep(1000);
                limit -= 1000;
            }
            assertNotEquals(stringData, "", "Не удалось считать данные из таблицы");
            this.data = new SSMGridData(stringData);
            this.data.convert();
            log.info("Данные считаны");
            return this.data;
        }, "Не удалось выделить ячейку");
    }


}
