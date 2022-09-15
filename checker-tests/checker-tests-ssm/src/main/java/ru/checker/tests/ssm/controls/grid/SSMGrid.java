package ru.checker.tests.ssm.controls.grid;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import junit.framework.AssertionFailedError;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.ControlType;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.Window;
import mmarquee.automation.controls.mouse.AutomationMouse;
import net.sourceforge.tess4j.ITessAPI;
import org.junit.jupiter.api.function.ThrowingSupplier;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWidget;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;
import ru.checker.tests.ssm.annotations.CheckerDefinitionValue;
import ru.checker.tests.ssm.widgets.SSMTools;
import ru.checker.tests.ssm.windows.core.service.GridFilterWindow;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2(topic = "TEST CASE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings({"ConstantConditions", "unused"})
public class SSMGrid {

    /**
     * Элемент управления.
     */
    final Panel control;

    /**
     * Робот.
     */
    final Robot robot;

    /**
     * Описание элемента.
     */
    final Map<String, Object> DEFINITION;

    /**
     * ID таблицы.
     */
    String ID;

    /**
     * Имя таблицы.
     */
    String name;

    /**
     * Данные таблицы.
     */
    SSMGridData data;

    /**
     * Местоположение заголовков таблицы.
     */
    Rectangle headerRectangle;

    /**
     * Конфигурация таблицы.
     */
    @Getter
    @Setter
    Config config;

    public SSMGrid(Panel control, Map<String, Object> definition) {
        this.control = control;
        this.DEFINITION = definition;
        this.config = definition.containsKey("config") ? new Config(CheckerTools.castDefinition(definition.get("config"))) : new Config();
        this.robot = assertDoesNotThrow((ThrowingSupplier<Robot>) Robot::new, "Не удалось получить доступ к клавиатуре");
    }

    /**
     * ID таблицы.
     *
     * @return ID
     */
    public String getID() {
        if (this.ID == null) {
            assertTrue(this.DEFINITION.containsKey("id"), "Для таблицы не задан ID");
            this.ID = CheckerTools.castDefinition(this.DEFINITION.get("id"));
        }
        return this.ID;
    }

    /**
     * Имя таблицы.
     *
     * @return Имя
     */
    public String getName() {
        if (this.name == null) {
            this.name = this.DEFINITION.containsKey("name")
                    ? CheckerTools.castDefinition(this.DEFINITION.get("name"))
                    : "*Нет Имени*";
        }
        return this.name;
    }

    /**
     * Get table rectangle.
     * <p>
     * Получение местоположения таблицы.
     *
     * @return Rectangle
     * Местоположение
     */
    public Rectangle getRectangle() {
        return assertDoesNotThrow(
                () -> this.control.getBoundingRectangle().toRectangle(),
                "Не удалось получить расположение таблицы");
    }

    /**
     * Фокус над таблицей.
     */
    private void focus() {
        AtomicBoolean focused = new AtomicBoolean(false);
        assertDoesNotThrow(() -> {
            int limit = 60000;
            while (limit >= 0) {
                try {
                    log.debug("Фокус над таблицей");
                    this.control.getElement().setFocus();

                    log.debug("Переход на 1 строку 1 ячейку");
                    CheckerDesktopManipulator.Keyboard.sendKeys("PAGE_UP");
                    CheckerDesktopManipulator.Keyboard.sendKeys("HOME");
                    focused.set(true);
                    break;
                } catch (Exception ex) {
                    Thread.sleep(1000);
                    limit -= 1000;
                }
            }
        }, "Не удалось сфокусироваться над таблицей '" + this.getID() + ". " + this.getName() + "'");
        assertTrue(focused.get(), "Таблица '" + this.getID() + ". " + this.getName() + "' не была сфокусирована");
        log.debug("Таблица '{}. {}' сфокусирована", this.getID(), this.getName());
    }

    /**
     * Получает данные видимой страницы таблицы
     * с отключенной проверкой данных.
     *
     * @return Дынные страницы таблицы
     */
    public SSMGridData getFirstPageData() {
        return this.getFirstPageData(false);
    }

    /**
     * Получает данные видимой страницы таблицы.
     * <p>
     * Взаимодействие происходит при помощи комбинации SHIFT + PAGE_DOWN.
     *
     * @param needDataCheck Нужна ли проверка наличия данных
     * @return Данные страницы из таблицы.
     */
    public SSMGridData getFirstPageData(boolean needDataCheck) {
        log.debug("Чтение данных таблицы '{}. {}' из первой страницы", this.getID(), this.getName());

        log.debug("Очистка данных таблицы '{}. {}'", this.getID(), this.getName());
        this.data = new SSMGridData("");
        this.focus();

        Runnable run = () -> {
            CheckerDesktopManipulator.Keyboard.pressKeys("SHIFT");
            CheckerDesktopManipulator.Keyboard.sendKeys("END | PAGE_DOWN");
            CheckerDesktopManipulator.Keyboard.releaseKeys("SHIFT");
        };

        return this.data;
    }

    /**
     * Получает все записи таблицы
     * с выключенной проверкой на наличие данных.
     *
     * @return Данные
     */
    public SSMGridData getAllData() {
        return this.getAllData(false);
    }

    /**
     * Получает все записи таблицы.
     * Используется комбинация CTRL + A.
     * При большом объеме данных
     * возможно появление окна с отказом доступа к буферу обмена,
     * В данном случае данные вернуться с пустым значением,
     * !!!но не исключает зависание программы!!!
     *
     * @param needCheckData Нужна ли проверка на наличие данных
     * @return Данные
     */
    public SSMGridData getAllData(boolean needCheckData) {
        log.info("Чтение всех данных таблицы '{}. {}'", this.getID(), this.getData());
        CheckerTools.clearClipboard();
        this.data = new SSMGridData("");
        String stringData = "";
        this.focus();
        Runnable run = () -> CheckerDesktopManipulator.Keyboard.sendKeys("CONTROL | A");
        this.readData(run, needCheckData);

        return this.data;
    }

    /**
     * Check grid has data.
     * Проверяет есть ли данные в таблице.
     * <p>
     * Проверка осуществляется с помощью метода:
     *
     * @see SSMGrid#hasDataResult()
     * <p>
     * При ошибке бросает исключение:
     * @see AssertionFailedError
     */
    public void hasData() {
        assertTrue(
                this.hasDataResult(),
                String.format("Не найдено записей в таблице '%s.%s'", this.getID(), this.getName()));
    }

    /**
     * Check grid has data.
     * Проверяет есть ли данные в таблице.
     * <p>
     * !!! Не обходимо считать данные в буфер таблицы перед проверкой!!!
     *
     * @return Результат проверки
     */
    public boolean hasDataResult() {
        log.info("Проверка наличия данных в таблице '{}.{}'", this.getID(), this.getName());
        log.debug("Обнаружено\nКолонок - '{}'\nСтрок - {}", this.data.getHeaderSize(), this.data.getRowSize());
        boolean result = this.data.getRowSize() > 0;
        log.info("В таблице '{}.{}' {} записи", this.getID(), this.getName(), (result ? "имеет" : "не имеет"));
        return result;
    }

    /**
     * Check grid hasn't data.
     * Проверяет отсутствие данных.
     * <p>
     * Проверка проходит с помощью метода:
     *
     * @see SSMGrid#hasDataResult()
     * <p>
     * При ошибке бросает исключение:
     * @see AssertionFailedError
     */
    public void hasNotData() {
        log.info("Проверка отсутствия данных в таблице '{}.{}'", this.getID(), this.getName());
        assertFalse(this.hasDataResult(), String.format("Найдены записи в таблице '%s.%s'", this.getID(), this.getName()));
        log.info("В таблице '{}.{}' отсутствуют данные", this.getID(), this.getName());
    }

    /**
     * Проверка содержит ли значение колонка таблицы.
     *
     * @param column Колонка таблицы
     * @param value  Искомое значение
     *               <p>
     *               При ошибке бросает исключение:
     * @see AssertionFailedError
     */
    public void containsData(String column, String value) {
        assertTrue(this.containsDataResult(column, value), String.format(
                "Найдены записи в таблице '%s.%s', не удовлетворяющие условию: 'Колонка '%s' содержит '%s''",
                this.getID(),
                this.getName(),
                column,
                value));
    }

    /**
     * Check grid contains data.
     * Метод проверяет содержит ли колонка таблицы данные.
     *
     * @param column Grid column
     *               Колонка таблицы
     * @param value  Searching value
     *               Искомое значение
     * @return Результат проверки
     */
    public boolean containsDataResult(String column, String value) {
        log.info(
                "Проверка данных в таблице '{}.{}' по условию: 'Колонка '{}' содержит '{}''",
                this.getID(),
                this.getName(),
                column,
                value);
        boolean result = this.data.getColumnData(column).parallelStream().anyMatch(row -> row.contains(value));
        log.info(
                "В таблице '{}.{}' {} данные, удовлетворяющие условию: 'Колонка '{}' содержит '{}''",
                this.getID(),
                this.getName(),
                (result ? "присутствуют" : "отсутствуют"),
                column,
                value);
        return result;
    }

    /**
     * Check grid doesn't contains data.
     *
     * @param column Grid column
     * @param value  searching value
     *               <p>
     *               При ошибке бросает исключение:
     * @see AssertionFailedError
     */
    public void doesntContainsData(String column, String value) {
        assertFalse(this.containsDataResult(column, value), String.format(
                "Найдены записи в таблице '%s.%s', не удовлетворяющие условию: 'Колонка '%s' не содержит '%s''",
                this.getID(),
                this.getName(),
                column,
                value));
    }

    /**
     * Проверяет равенства колонок в таблице.
     * @param columns Словарь "Колонка-Значение"
     */
    public void columnsDataEqual(Map<String, String> columns) {
        AtomicReference<Map.Entry<String, String>> error = new AtomicReference<>();
        assertTrue(
                columns.entrySet().parallelStream().allMatch(entry -> {
                    boolean result;
                    if(!(result = this.containsDataResult(entry.getKey(), entry.getValue()))) {
                        error.set(entry);
                    }

                    return result;
                }),
                "Найдены записи не удовлетворяющие условию равенства");
    }

    /**
     * Check grid column data equals value.
     * Проверка всех значений колонки таблицы равных значению.
     *
     * @param column Grid column
     *               Имя колонки таблицы
     * @param value  searching value
     *               Искомое значение
     *               <p>
     *               При ошибке бросает исключение:
     * @see AssertionFailedError
     */
    public void columnDataEquals(String column, String value) {
        assertTrue(this.columnDataEqualsResult(column, value), String.format(
                "Найдены записи в таблице '%s.%s', не удовлетворяющие условию: 'Данные колонки  '%s' равны '%s''",
                this.getID(),
                this.getName(),
                column,
                value));
    }

    /**
     * Проверяет значения колонки равны значению.
     * !!! Перед проверкой следует считать данные в буфер таблицы !!!
     *
     * @param column Имя колонки таблицы
     * @param value  Искомое значение
     * @return Результат проверки
     */
    public boolean columnDataEqualsResult(String column, String value) {
        log.info(
                "Проверка данных в таблице '{}.{}' по условию: 'Данные колонки '{}' равны '{}''",
                this.getID(),
                this.getName(),
                column,
                value);
        boolean result = this.data.getColumnData(column).parallelStream().allMatch(row -> row.equals(value));
        log.info(
                "В таблице '{}.{}' {} данные, удовлетворяющие условию: 'Данные колонки '{}' равны '{}''",
                this.getID(),
                this.getName(),
                (result ? "присутствуют" : "отсутствуют"),
                column,
                value);

        return result;
    }

    /**
     * Check grid column data equals value.
     *
     * @param column Grid column
     * @param value  searching value
     *
     * <p>При ошибке бросает исключение:
     * @see AssertionFailedError
     */
    public void columnDataNotEquals(String column, String value) {
        assertFalse(this.columnDataEqualsResult(column, value), String.format(
                "Найдены записи в таблице '%s.%s', не удовлетворяющие условию: 'Данные колонки  '%s' не равны '%s''",
                this.getID(),
                this.getName(),
                column,
                value));
    }

    /**
     * Select row by index.
     *
     * Выбор строки по индексу.
     *
     * @param index Current index
     *              Индекс строки
     */
    public void selectRow(int index) {
        assertDoesNotThrow(() -> {
            this.focus();
            log.info("Выбор {} строки в таблице", index + 1);

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
        log.info("Выделение строки таблицы красным цветом");
        return assertDoesNotThrow(() -> {

            log.debug("Вычисление координат строки {} таблицы", index);
            AtomicReference<Rectangle> cellRectangle = new AtomicReference<>();
            SSMGridData data = this.getDataFromRow(0, cellRectangle);
            assertNotNull(cellRectangle.get(), "Не удалось получить координаты строки");
            log.debug("Координаты строки получены");

            log.debug("Выделение {} строки", index + 1);
            AutomationMouse.getInstance().setLocation(cellRectangle.get().x + 20, cellRectangle.get().y);
            this.robot.keyPress(KeyEvent.VK_CONTROL);
            AutomationMouse.getInstance().leftClick();
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            log.debug("Строка выделена");

            log.info("Проверка выделения");
            assertTrue(this.checkRowColor(cellRectangle.get(), this.config.acceptedRowColor), "Строка не была выделена");
            log.info("Строка успешно выделена. Считывание данных...");
            this.data = data;
            log.info("Данные считаны");
            return this.data;
        }, "Не удалось выделить ячейку с индексом " + index);
    }

    /**
     * Выделение ячейки и проверка,
     * что она выделилась синим цветом.
     * <p>
     * Цвет задан в настройках.
     *
     * @param index Индекс строки с 0
     * @return Данные из строки
     * @see Config#selectedRowColor
     */
    public SSMGridData selectRowAndCheckSelection(int index) {
        log.info("Выделение сроки и чтение данных с номером {}", index + 1);
        AtomicReference<Rectangle> cellRectangle = new AtomicReference<>();
        this.getDataFromRow(index, cellRectangle);
        log.info("Данные получены");
        log.info("Проверка выделения строки с номером {}", index + 1);
        assertTrue(
                this.checkRowColor(cellRectangle.get(), this.config.selectedRowColor),
                "Строка не выделена цветом - " + this.config.selectedRowColor);
        log.info("Строка выделена цветом '{}'", this.config.selectedRowColor);
        return this.data;
    }

    /**
     * Выделение ячейки и проверка,
     * что она выделилась синим цветом.
     * <p>
     * Цвет задан в настройках.
     *
     * @param index Индекс строки с 0
     * @return Данные из строки
     * @see Config#selectedRowColor
     */
    public SSMGridData selectRowAndCheckAssigned(int index) {
        log.info("Выделение сроки и чтение данных с номером {}", index + 1);
        AtomicReference<Rectangle> cellRectangle = new AtomicReference<>();
        this.getDataFromRow(index, cellRectangle);
        log.info("Данные получены");
        log.info("Проверка выделения строки с номером {}", index + 1);
        assertTrue(
                this.checkRowColor(cellRectangle.get(), this.config.assignedRowColor),
                "Строка не выделена цветом - " + this.config.assignedRowColor);
        log.info("Строка выделена цветом '{}'", this.config.assignedRowColor);
        return this.data;
    }

    /**
     * Проверка цвета строки таблицы.
     *
     * @param cellRectangle Положение локатора таблицы
     * @param color         Искомый цвет
     * @return Результат проверки
     */
    public boolean checkRowColor(Rectangle cellRectangle, Color color) {
        boolean found = false;
        log.debug("Поиск цвета - '{}'", color.toString());
        for (int i = cellRectangle.x; i < (int) this.getRectangle().getMaxX(); i++) {
            if (this.robot.getPixelColor(i, (int) cellRectangle.getCenterY()).equals(color)) {
                found = true;
                break;
            }
        }
        log.debug("Цвет {}", (found ? "Найден" : "Не найден"));
        return found;
    }

    /***
     * Get row index where condition was match.
     * @param name Column name
     * @param pattern Value pattern
     * @return Column index
     */
    public int getRowIndexRowByCondition(String name, Pattern pattern) {
        SSMGridData data = this.getAllData();
        List<String> rows = data.getColumnData(name);
        AtomicInteger index = new AtomicInteger();
        for (String row : rows) {
            if (pattern.matcher(row.trim()).find())
                break;
            index.getAndIncrement();
        }
        assertTrue(index.get() < rows.size(), "Не найдено строки по условию - " + pattern.pattern());
        return index.get();
    }

    /**
     * Read data with delay.
     * Delay max = 60 sec.
     * If u need to change limit,
     * correct variable limit.
     * !!! Method already send combination 'CTRL + C' !!!
     * U need to select data only.
     *
     * @param preActions        Pre action before reading
     * @param checkContainsData if you need to check empty data type 'true'
     * @return Mapped grid data
     */
    private SSMGridData readData(Runnable preActions, boolean checkContainsData) {
        log.debug(
                "Проверка на наличие данных таблицы '{}. {}' {}",
                this.getID(),
                this.getName(),
                (checkContainsData ? "Включена" : "Выключена"));
        CheckerTools.clearClipboard();
        String stringData = "";
        int limit = 60000;

        // Continue while clipboard data is empty or limit is not reached
        while (stringData.equals("") & limit >= 0) {
            // run pre actions
            preActions.run();

            //copy selected data to clipboard
            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_C);

            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.keyRelease(KeyEvent.VK_C);

            assertDoesNotThrow(() -> Thread.sleep(2000));

            // read data from clipboard
            try {
                stringData = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
            } catch (UnsupportedFlavorException e) {
                fail("В буфере обнаружено не строчное значение");
            } catch (IllegalStateException | IOException e) {
                assertDoesNotThrow(
                        () -> Thread.sleep(1000),
                        "Не удалось выполнить ожидание при считывании данных в таблице");
                limit -= 1000;
                continue;
            }

            if (!stringData.equals("")) {
                break;
            }
            assertDoesNotThrow(
                    () -> Thread.sleep(1000),
                    "Не удалось выполнить ожидание при считывании данных в таблице");
            limit -= 1000;
        }

        WinDef.HWND handle = User32.INSTANCE.FindWindow(null, "Ssm");
        if (handle != null) {
            assertDoesNotThrow(() -> {
                Element windowRaw = UIAutomation.getInstance().getElementFromHandle(handle);
                Window rejectWindow = new Window(new ElementBuilder().element(windowRaw));
                rejectWindow.close();
            }, "Не удалось закрыть окно отказа в доступе");
        }

        if (checkContainsData)
            assertNotEquals(stringData, "", "Не удалось считать данные из таблицы");

        log.debug("Таблица '{}. {}' успешно прочитана", this.getID(), this.getName());
        SSMGridData data = new SSMGridData(stringData);
        data.convert(this.config.headerCount);
        log.debug("Конвертирование данных таблицы '{}. {}'", this.getID(), this.getName());
        log.debug("Данные успешно конвертированы.\nКоличество колонок - {}\nКоличество строк - {}", data.getHeaderSize(), data.getRowSize());
        log.debug("Сохранение данных в буфере таблицы '{}. {}'", this.getID(), this.getName());
        this.data = data;
        log.debug("Данные сохранены");
        this.focus();
        return data;
    }

    public SSMGridData getDataFromRow(int index) {
        AtomicReference<Rectangle> rectangleAtomicReference = new AtomicReference<>();
        return this.getDataFromRow(index, rectangleAtomicReference);
    }

    /**
     * Get data from row by index
     *
     * @param index Row index
     * @return Row data
     */
    public SSMGridData getDataFromRow(int index, AtomicReference<Rectangle> cellRectangle) {
        this.selectRow(index);

        this.robot.keyPress(KeyEvent.VK_ESCAPE);
        this.robot.keyPress(KeyEvent.VK_ESCAPE);

        this.robot.keyPress(KeyEvent.VK_HOME);
        this.robot.keyPress(KeyEvent.VK_HOME);

        this.robot.keyPress(KeyEvent.VK_ENTER);
        this.robot.keyRelease(KeyEvent.VK_ENTER);

        Element cell = assertDoesNotThrow(() -> {
            Thread.sleep(8000);
            return UIAutomation.getInstance().getFocusedElement();
        }, "Не удалось найти ячейку-локатор строки с индексом -" + index);

        assertNotNull(cell, "Не удалось найти ячейку-локатор строки");
        cellRectangle.set(assertDoesNotThrow(() -> cell.getBoundingRectangle().toRectangle(), "Не удалось получить расположение элемента"));
        int y = assertDoesNotThrow(() -> cell.getClickablePoint().y, "Не удалось получить высоту строки таблицы");
        int x;
        log.debug("Выделение строки. Y - {}. X - {}", (x = this.getRectangle().x), y);
        Runnable run = () -> {
            WinDef.POINT point = new WinDef.POINT(x + 5, y);
            AutomationMouse.getInstance().setLocation(point);
            AutomationMouse.getInstance().leftClick();

            // Кода в таблице отсутствует область для выделения
            if (!this.robot.getPixelColor(x + 3, y).equals(new Color(240, 240, 240))) {
                this.robot.keyPress(KeyEvent.VK_ESCAPE);
                this.robot.keyRelease(KeyEvent.VK_ESCAPE);

                this.robot.keyPress(KeyEvent.VK_END);
                this.robot.keyRelease(KeyEvent.VK_END);

                this.robot.keyPress(KeyEvent.VK_SHIFT);
                this.robot.keyPress(KeyEvent.VK_HOME);
                this.robot.keyRelease(KeyEvent.VK_SHIFT);
                this.robot.keyRelease(KeyEvent.VK_HOME);
            }
        };
        log.debug("Выделено");

        this.data = this.readData(run, true);
        return this.data;
    }

    /**
     * Получает данные по индексу строки.
     * Проверка данных по умолчанию выключена.
     * @param index Индекс строки
     */
    public void getDataByRow(int index) {
        this.getDataByRow(index, false);
    }

    /**
     * Получает данные по индексу строки.
     * @param index Индекс строки
     * @param needCheck Нужна ли проверка данных
     */
    public void getDataByRow(int index, boolean needCheck) {
        this.focus();
        this.selectRow(0);
        Runnable run = () -> {
            CheckerDesktopManipulator.Keyboard.sendKeys("END", KeyEvent.VK_SHIFT);
        };

        this.data = this.readData(run, needCheck);
        CheckerDesktopManipulator.Keyboard.sendKeys("HOME");
    }

    public void filter(String filterID) {
        ConditionConfigurer config = ConditionConfigurer.getConfig(this.DEFINITION, filterID);
        this.filter(config);

    }

    /**
     *
     * @param config
     */
    public void filter(ConditionConfigurer config) {
        this.getDataByRow(0, true);
        String cellName = (this.config.headerCount == 0) ? config.columnIndexReference : config.column;
        this.moveToCell(cellName);
        if(this.config.hasHeader && this.config.headerCount == 0) {
            this.filterByUnreadColumns(config);
        } else if(this.config.unFocused.contains(config.column)) {
            this.filterByUnreadColumns(config);
        } else {
            this.filterByEnterCell(config);
        }
    }

    private void filterByEnterCell(ConditionConfigurer configurer) {
        try {
            EditBox cell = this.enterToCell();
            Rectangle cellRectangle = cell.getBoundingRectangle().toRectangle();
            Rectangle headerRectangle = new Rectangle(cellRectangle.x, cellRectangle.y - 20, cellRectangle.width, 20);
            this.findAndClickFilterButton(headerRectangle);
            this.callFilterWindow();
            GridFilterWindow filter = CheckerDesktopTest.getCurrentApp().window("ssm_core_filter", GridFilterWindow.class);
            filter.setFilterByConfigurer(configurer);
            filter.clickOK();
        } catch (Exception ex) {
            this.filterByUnreadColumns(configurer);
        }
    }

    public EditBox enterToCell() {
        CheckerDesktopManipulator.Keyboard.sendKeys("SPACE");
        assertDoesNotThrow(() -> Thread.sleep(1000), String.format("Не удалось дождаться входа в ячейку таблицы '%s. %s'", this.getID(), this.getName()));
        Element raw = assertDoesNotThrow(() -> UIAutomation.getInstance().getFocusedElement(), "Не удалось получить элемент управления под фокусом");
        assertDoesNotThrow(() -> {
            if(raw.getControlType() != ControlType.Edit.getValue() && !raw.getClassName().equalsIgnoreCase("TcxCustomInnerTextEdit"))
                throw new Exception(String.format("Элемент управления под фокусом не является ячейкой таблицы '%s. %s'", this.getID(), this.getName()));
        });
        return new EditBox(new ElementBuilder().element(raw));
    }

    private void filterByUnreadColumns(ConditionConfigurer config) {
        Map.Entry<String, Rectangle> header = this.getHeadersByOCR(config);
        this.findFilterButtonFromHeaderRectangle(header.getValue(), header.getKey());
        this.callFilterWindow();
        GridFilterWindow filter = CheckerDesktopTest.getCurrentApp().window("ssm_core_filter", GridFilterWindow.class);
        filter.setFilterByConfigurer(config);
        filter.clickOK();
    }

    /**
     * Проверяет являться ли колонка искомой.
     * @param header Заголовок
     * @param config Конфигуратор условий.
     * @return Заголовок
     */
    private boolean getSearchingColumn(Map.Entry<String, Rectangle> header, ConditionConfigurer config) {
        Pattern pattern;
        if(Objects.nonNull(config.columnCondition)) {
            pattern = Pattern.compile(config.columnCondition);
        } else {
            pattern = Pattern.compile("^" + config.column);
        }

        return pattern.matcher(header.getKey()).lookingAt();
    }

    /**
     * Ищет кнопку фильтра на колонке.
     * @param headerRectangle Положение колонки
     * @param columnName Имя колонки
     */
    private void findFilterButtonFromHeaderRectangle(Rectangle headerRectangle, String columnName) {
        int y = (int) headerRectangle.getCenterY();
        boolean found = false;
        for (int i = (int) headerRectangle.getMaxX(); i > headerRectangle.x; i--) {
            AutomationMouse.getInstance().setLocation(i, y);
            if(this.robot.getPixelColor(i + 3, y).equals(this.config.filterColor)) {
                AutomationMouse.getInstance().leftClick();
                found = true;
                break;
            }
        }
        assertTrue(found, String.format("Не удалось найти кнопку фильтра колонки '%s' таблицы '$%s. %s'", columnName, this.getID(), this.getName()));
    }

    /**
     * Считывание колонок методом OCR.
     *
     * Срабатывает в случае, когда в настройках указано:
     * @see Config#headerCount - количество заголовков при считывании в буфер = '0'.
     * @see Config#hasHeader - имеются ли заголовки = 'true'
     *
     * Механизм:
     * 1) От начала таблицы + 2px ищет цвет равный цвету,
     * указанного в настройках.
     * @see Config#headerColor - Цвет колонок
     * 2) Считывает пиксели пока не найдет цвет указанный в настройках.
     * @see Config#headerSplitColor - Цвет разделения заголовков.
     * При нахождении цвета формирует местоположение заголовка и считывает.
     *
     * @return Заголовки и их положение
     */
    private Map.Entry<String, Rectangle> getHeadersByOCR(ConditionConfigurer config) {
        log.debug("Вычисление положения строки заголовков таблицы '{}. {}'", this.getID(), this.getName());
        int rectangleY = -1;
        Rectangle tableRect = this.getRectangle();
        for (int i = tableRect.y; i < tableRect.height; i++) {
            if(this.robot.getPixelColor(tableRect.x + 2, i).equals(this.config.tableHeaderColor)) {
                rectangleY = i;
                break;
            }
        }
        assertNotEquals(rectangleY, -1, String.format(
                "Не найден цвет-локатор строки с заголовками талицы '%s. %s'",
                this.getID(),
                this.getName()));
        log.debug("Положение строки заголовков вычислено. Y - '{}'", rectangleY);
        log.debug("Считывание колонок таблицы '{}. {}'", this.getID(), this.getName());
        Map.Entry<String, Rectangle> header = null;
        int rectX = tableRect.x + 2;
        for (int i = rectX; i < tableRect.width; i++) {
            if(this.robot.getPixelColor(i, rectangleY).equals(this.config.headerSplitColor)) {
                int width = i - rectX;
                Rectangle headerRectangle = new Rectangle(rectX, rectangleY, width - 1, 20);
                String headerName = CheckerOCRUtils.getTextFromRectangle(headerRectangle).replaceAll("[^A-Za-zА-Яа-я0-9, ]", "").trim();
                if(this.getSearchingColumn(Map.entry(headerName, headerRectangle), config)) {
                    header = Map.entry(headerName, headerRectangle);
                    break;
                } else {
                    i += 2;
                    rectX = i;
                }
            }
        }
        assertNotNull(header, String.format("Не удалось найти заголовок '%s' таблицы '%s. %s'", config.column, this.getID(), this.getName()));
        log.debug("Заголовок найден - '{}'", header.getKey());

        return header;
    }
    /**
     * Clear GUI filter.
     */
    public void clearFilter() {
        log.info("Очистка фильтра");
        int top = (int) this.getRectangle().getMinY();
        int bottom = (int) this.getRectangle().getMaxY() - ((this.config.hasBottomScroll) ? 20 : 2);
        int x = this.getRectangle().x + 5;
        boolean startFound = false;
        boolean endFound = false;

        int start = 0;
        int end = 0;

        for (int i = bottom; i > top; i--) {
            AutomationMouse.getInstance().setLocation(x, i);
            Color color = this.robot.getPixelColor(x, i);
            if (color.equals(this.config.enabledFilterColor)) {
                AutomationMouse.getInstance().setLocation(x, i - 5);
                AutomationMouse.getInstance().leftClick();
                break;
            }
        }
        log.info("Фильтр очищен");
    }

    /**
     * Filter table by GUI.
     */
    public void filterByGUI(String configID) {
        String[] columns = new String[this.config.unFocused.size()];
        AtomicInteger index = new AtomicInteger(0);
        this.config.unFocused.forEach(member -> columns[index.getAndIncrement()] = member.toString());
        this.filterByGUI(ConditionConfigurer.getConfig(this.DEFINITION, configID), CheckerOCRLanguage.RUS, (columns));
    }

    /**
     * Filter table by GUI.
     *
     * @param config Condition configurer
     */
    public void filterByGUI(ConditionConfigurer config) {
        String[] columns = new String[this.config.unFocused.size()];
        AtomicInteger index = new AtomicInteger(0);
        this.config.unFocused.forEach(member -> columns[index.getAndIncrement()] = member.toString());
        this.filterByGUI(config, CheckerOCRLanguage.RUS, (columns));
    }

    /**
     * Filter table by GUI.
     *
     * @param config    Condition configurer
     * @param unFocused Unfocused columns
     */
    public void filterByGUI(ConditionConfigurer config, String... unFocused) {
        this.filterByGUI(config, CheckerOCRLanguage.RUS, unFocused);
    }

    /**
     * Filter table by GUI.
     *
     * @param config    Condition configurer
     * @param unFocused Unfocused columns
     */
    public void filterByGUI(ConditionConfigurer config, CheckerOCRLanguage language, String... unFocused) {
        for (int i = 0; i <= this.config.filterCallingTryCont; i++) {
            try {
                log.info("Производится фильтрация через интерфейс");
                if (unFocused == null)
                    unFocused = new String[0];
                AtomicReference<Rectangle> atomicCellRectangle = new AtomicReference<>();
                SSMGridData data = this.getDataFromRow(0, atomicCellRectangle);
                assertNotNull(atomicCellRectangle.get(), "Не удалось получить расположение ячейки локатора");

                Rectangle cellRectangle = this.moveToCell(
                        new Point(atomicCellRectangle.get().x, (int) atomicCellRectangle.get().getMinY()),
                        data,
                        config.column,
                        config.columnCondition,
                        language,
                        unFocused
                );

                this.findAndClickFilterButton(cellRectangle);
                this.callFilterWindow();

                GridFilterWindow filter = CheckerDesktopTest.getCurrentApp().window("ssm_core_filter", GridFilterWindow.class);
                filter.setFilterByConfigurer(config);
                filter.clickOK();
                log.info("Фильтрация выполнена");
                break;
            } catch (Exception | Error ex) {
                log.warn("Повторная попытка фильтрации. Так как была прервана по ошибке");
                if (i != this.config.filterCallingTryCont) {
                    assertDoesNotThrow(() -> Thread.sleep(1000));
                } else {
                    fail(ex);
                }
            }

        }
    }

    /**
     * Перемещение к ячейке по имени колонки и первой строки по умолчанию.
     *
     * @param columnName Имя колонки
     */
    public void moveToCell(String columnName) {
        this.moveToCell(columnName, 0);
    }

    /**
     * Перемещение к ячейке по имени колонки и номеру строки.
     *
     * @param columnName Имя колонки
     * @param rowIndex   Номер строки
     */
    public void moveToCell(String columnName, int rowIndex) {
        log.debug("Фокус над таблицей");
        this.control.getElement().setFocus();
        log.debug("Таблица успешно сфокусирована");
        assertTrue(
                this.data.getRowSize() >= rowIndex + 1,
                String.format("В таблице меньше записей '%d' чем индекс - %d",
                        this.data.getRowSize(),
                        rowIndex));
        log.debug("Заданный индекс строки {} находится в переделах записей", rowIndex);

        assertTrue(
                this.data.getHeaders().contains(columnName),
                String.format("В таблице отсутствует колонка '%s'",
                        columnName));
        log.debug("Колонка {} в таблице присутствует", columnName);

        log.debug("Переход к первой строчке");
        CheckerDesktopManipulator.Keyboard.sendKeys("PAGE_UP");
        log.debug("Перемещение к первой ячейке");
        CheckerDesktopManipulator.Keyboard.sendKeys("HOME");

        log.debug("Перемещение к нужной строке");
        for (int i = 0; i < rowIndex; i++) {
            CheckerDesktopManipulator.Keyboard.sendKeys("DOWN");
        }

        log.debug("Перемещение к нужной ячейке");
        for (String header : this.data.getHeaders()) {
            if (header.equals(columnName))
                break;
            if (!this.config.getUnFocused().contains(header)) {
                CheckerDesktopManipulator.Keyboard.sendKeys("RIGHT");
            }
        }

        log.debug("Перемещение к ячейке успешно завершено");
    }

    /**
     * Перемещается к ячейке.
     * @param rowPoint Точка-локатор строки
     * @param data Данные
     * @param targetCell Нужная ячейка
     * @param columnCondition Паттерн колони
     * @param language Язык распознавания
     * @param unFocused Не фокусируемые ячейки
     * @return Положение ячейки
     */
    private Rectangle moveToCell(Point rowPoint, SSMGridData data, String targetCell, String columnCondition, CheckerOCRLanguage language, String... unFocused) {
        List<String> unFocusedList = Arrays.asList(unFocused);
        AtomicReference<Rectangle> out = new AtomicReference<>();

        this.robot.setAutoDelay(50);
        this.robot.keyPress(KeyEvent.VK_HOME);
        this.robot.keyRelease(KeyEvent.VK_HOME);

        this.robot.keyPress(KeyEvent.VK_ESCAPE);
        this.robot.keyRelease(KeyEvent.VK_ESCAPE);

        for (String header : data.getHeaders()) {
            if (!unFocusedList.contains(header)) {
                if (header.equals(targetCell)) {
                    this.robot.keyPress(KeyEvent.VK_SPACE);
                    this.robot.keyRelease(KeyEvent.VK_SPACE);

                    Rectangle cell = assertDoesNotThrow(() -> {
                        Thread.sleep(5000);
                        return UIAutomation.getInstance().getFocusedElement().getBoundingRectangle().toRectangle();
                    }, "Не удалось найти ячейку-локатор");
                    out.set(cell);
                    break;
                }
                this.robot.keyPress(KeyEvent.VK_ESCAPE);
                this.robot.keyRelease(KeyEvent.VK_ESCAPE);
                this.robot.keyPress(KeyEvent.VK_RIGHT);
                this.robot.keyRelease(KeyEvent.VK_RIGHT);
            } else {
                if (header.equals(targetCell)) {
                    int x = this.getRectangle().x - 2;
                    int y = rowPoint.y - 20;
                    int width = this.getRectangle().width;
                    int height = 20;
                    Rectangle headersRectangle = new Rectangle(x, y, width, height);
                    Rectangle r = CheckerOCRUtils.getTextAndMove(headersRectangle, Pattern.compile("^" + ((columnCondition != null) ? columnCondition : targetCell) + "$"), language, ITessAPI.TessPageIteratorLevel.RIL_WORD);
                    int cellMaxX = 0;
                    for (int i = (int) r.getMaxX() + 2; i < this.getRectangle().getMaxX(); i++) {
                        if (this.robot.getPixelColor(i, (int) r.getMaxY()).equals(Color.BLACK)) {
                            cellMaxX = i;
                            break;
                        }
                    }
                    assertNotEquals(cellMaxX, 0, "Не удалось вычислить заголовок таблицы");

                    x = r.x;
                    y = (int) r.getMaxY();
                    width = (int) (cellMaxX - r.getMaxX());
                    height = 3;

                    Rectangle cell = new Rectangle(x, y + 5, width, height);
                    out.set(cell);
                    break;
                }
            }
        }
        assertNotNull(out.get(), "Не удалось найти локатор для открытия фильтра");
        return out.get();
    }


    /**
     * Find and click filter button on column header.
     * The search will be done
     * when the cell locator is found
     * and the mouse selection button is color-coded
     * in the variable 'filterColor'.
     *
     * Находит и нажимает кнопку фильтра в названии колонки.
     * Механизм:
     * 1) Метод получает положение ячейки - локатора.
     * 2) Двигает мышкой вверх, считывает цвет пикселя и сравнивает с индикатором пока, не появится фильтр
     * 3) Цвет - индикатор задан в конфигурациях таблицы.
     * @see Config#filterColor
     *
     * 3) Если находит цвет схожий с индикатором отступает 3px вверх и нажимает левой кнпкой мыши.
     *
     * @param cellRectangle Cell-locator rectangle
     *                      Ячейка - локатор таблицы
     */
    private void findAndClickFilterButton(Rectangle cellRectangle) {
        log.info("Поиск фильтра в таблице");
        AutomationMouse.getInstance().setLocation(this.getRectangle().x, this.getRectangle().y);
        assertDoesNotThrow(() -> Thread.sleep(500), "Не удалось дождаться перемещения мыши");
        boolean found = false;
        for (int i = cellRectangle.y; i > this.getRectangle().y; i--) {
            int x = (int) cellRectangle.getMaxX() - 4;
            AutomationMouse.getInstance().setLocation(x, i);
            assertDoesNotThrow(() -> Thread.sleep(15), "Не удалось подождать мышь");
            if (this.robot.getPixelColor(x, i + 3).equals(this.config.filterColor)) {
                AutomationMouse.getInstance().setLocation(x, i - 5);
                assertDoesNotThrow(() -> Thread.sleep(500));
                AutomationMouse.getInstance().leftClick();
                found = true;
                assertDoesNotThrow(() -> Thread.sleep(500), "Не удалось подождать список фильтрации");
                break;
            }
        }
        assertTrue(found, "Фильтр колонки не найден");
    }

    /**
     * Calling filter window.
     * Вызывает око фильтрации.
     * После нажатия на кнопку фильтра в колонке таблицы
     * выводится лист выбора значения.
     * В этом листе метод выбирает запись "Выбор...".
     */
    private void callFilterWindow() {
        log.info("Фильтр найден. Поиск окна фильтрации");
        Element searchList = assertDoesNotThrow(() ->
                        UIAutomation.getInstance().getFocusedElement(),
                "Не удалось найти список фильтрации таблицы");
        Rectangle searchRectangle = assertDoesNotThrow(() -> searchList.getBoundingRectangle().toRectangle(), "Не удалось найти положения листа фильтрации");
        CheckerOCRUtils.getTextAndMove(
                new Rectangle(searchRectangle.x - 5, searchRectangle.y, searchRectangle.width + 5, searchRectangle.height),
                "Выб", ITessAPI.TessPageIteratorLevel.RIL_WORD);
        AutomationMouse.getInstance().leftClick();
        log.info("Окно фильтрации вызвано");
    }

    /**
     * Выбирает вкладку над таблицей.
     * @param name Имя вкладки
     */
    public void selectTab(String name) {
        log.debug("Переключение кладки таблицы. Локатор - '{}'", name);
        Rectangle place = this.getRectangle();
        CheckerDesktopWidget w = new CheckerDesktopWidget(this.control, this.control, this.DEFINITION);
        SSMTools pages = new SSMTools(w);

        log.debug("Поиск вкладки '{}' таблицы '{}. {}'",
                name,
                this.getID(),
                this.getName());
        List<Map.Entry<String, Rectangle>> tabs = pages.getTabs(this.config.pageWordSpace);
        Map.Entry<String, Rectangle> tab = tabs
                .parallelStream()
                .filter(tabEntry -> tabEntry.getKey().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(
                        () -> {
                            throw new AssertionFailedError(String.format(
                                    "Не удалось найти вкладку '%s' таблицы '%s. %s'",
                                    name,
                                    this.getID(),
                                    this.getName()));
                        });
        log.debug("Нажатие на вкладку {}' таблицы '{}. {}'",
                name,
                this.getID(),
                this.getName());
        CheckerDesktopManipulator.Mouse.click((int) tab.getValue().getCenterX(), (int) tab.getValue().getCenterY());
        log.debug("Вкладка {} таблицы '{}. {}' успешно переключена",
                name,
                this.getID(),
                this.getName());
    }

    /**
     * GUI filter condition configurer.
     *
     * @author vd.zinovev
     */
    @Builder
    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ConditionConfigurer {
        String column;
        String columnCondition;
        String columnIndexReference;
        String value1;
        String value2;
        Condition condition1;
        Condition condition2;
        Separator separator;

        /**
         * Получает описание конфигуратора фильтра из конфигурации таблицы.
         *
         * @param DEFINITION Описание таблицы
         * @param FILTER_ID  ID фильтра
         * @return Конфигуратор фильтра
         */
        public static ConditionConfigurer getConfig(Map<String, Object> DEFINITION, String FILTER_ID) {
            Map<String, Object> getFilterDefinition = getFilterByID(DEFINITION, FILTER_ID);
            return assertDoesNotThrow(() -> {
                ConditionConfigurer config = new ConditionConfigurer.ConditionConfigurerBuilder().build();
                Arrays.stream(config.getClass().getDeclaredFields())
                        .parallel()
                        .forEach(field -> fillField(CheckerTools.castDefinition(getFilterDefinition.get("filter")), field, config));
                return config;
            }, "Не удалось конвертировать описание в конфигурацию фильтра");
        }

        /**
         * Заполняет поле из описания фильтра.
         *
         * @param DEFINITION Описание фильтра
         * @param field      Поле
         * @param config     Конфигуратор
         */
        private static void fillField(Map<String, Object> DEFINITION, Field field, ConditionConfigurer config) {
            String name = field.isAnnotationPresent(CheckerDefinitionValue.class)
                    ? field.getAnnotation(CheckerDefinitionValue.class).value()
                    : field.getName();

            if (DEFINITION.containsKey(name)) {
                field.setAccessible(true);
                Object value;
                if(field.getType().isEnum()) {
                    if(field.getType().equals(SSMGrid.Condition.class))
                        value = assertDoesNotThrow(() -> SSMGrid.Condition.valueOf(DEFINITION.get(name).toString()),"В перечисленных значениях условий '" + DEFINITION.get(name) + "' не найдено");
                    else
                        value = assertDoesNotThrow(() -> SSMGrid.Separator.valueOf(DEFINITION.get(name).toString()),"В перечисленных значениях условий '" + DEFINITION.get(name) + "' не найдено");
                } else {
                    value = String.valueOf(DEFINITION.get(name));
                }
                assertDoesNotThrow(() -> field.set(config, value), "Не удалось получить доступ к полю конфигуратора фильтра");
                field.setAccessible(false);
            }
        }

        /**
         * Получает конфигурацию фильтров из описания таблицы.
         *
         * @param DEFINITION Описание таблицы.
         * @param FILTER_ID  ID фильтра
         * @return Описание конфигурации
         */
        private static Map<String, Object> getFilterByID(Map<String, Object> DEFINITION, String FILTER_ID) {
            assertTrue(DEFINITION.containsKey("filters"), "Описание таблицы не содержит описание фильтров (ключ 'filters')");
            List<Map<String, Object>> filters = CheckerTools.castDefinition(DEFINITION.get("filters"));
            assertNotEquals(filters.size(), 0, "Не удалось найти фильтр с ID - '" + FILTER_ID + "'. Список фильтров пуст");
            return filters
                    .parallelStream()
                    .filter(def -> {
                        assertTrue(def.containsKey("filter"),"Не найден ключ 'filter' описаниях фильтра");
                        Map<String, Object> temp = CheckerTools.castDefinition(def.get("filter"));
                        return temp.getOrDefault("id", "").toString().equals(FILTER_ID);
                    })
                    .findFirst()
                    .orElseThrow(() -> {
                        throw new AssertionFailedError("Не найден фильтр с ID - " + FILTER_ID);
                    });
        }
    }

    /**
     * GUI condition separator.
     * Перечисление связки фильтров.
     *
     * @author vd.zinovev
     */
    public enum Separator {
        AND("И"),
        OR("ИЛИ"),
        NONE(null);

        @Getter
        final String value;

        Separator(String value) {
            this.value = value;
        }
    }

    /**
     * GUI condition.
     * Условия фильтрации.
     *
     * @author vd.zinovev
     */
    public enum Condition {
        EQUAL("равно"),
        NOT_EQUAL("не равно"),
        EMPTY("пустое"),
        NOT_EMPTY("не пустое"),
        MORE_THEN("больше чем"),
        LESS_THEN("меньше чем"),
        LESS_OR_EQUAL("меньше чем или равно"),
        MORE_OR_EQUAL("больше чем или равно"),
        LIKE("похоже"),
        NOT_LIKE("не похоже"),
        CONTAINS("содержит"),
        NOT_CONTAINS("не содержит"),
        START_WITH("начинается с"),
        END_WITH("заканчивается с");

        @Getter
        final String value;

        Condition(String value) {
            this.value = value;
        }
    }

    /**
     * Конфигурация таблицы.
     *
     * Для изменения в описании таблицы необходимо задать ключ 'config'
     * и задать нужные поля обозначенные в аннотации.
     * @see CheckerDefinitionValue
     *
     * @author vd.zinovev
     */
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    @NoArgsConstructor
    public static class Config {

        /**
         * Цвет заголовков.
         */
        Color headerColor = new Color(244, 244, 244);

        /**
         * Цвет назначенной строки.
         */
        Color acceptedRowColor = new Color(255, 85, 85);

        /**
         *
         */
        Color filterColor = new Color(181, 181, 181);

        /**
         * Цвет отчищенного фильтра.
         */
        Color clearFilterColor = new Color(244, 244, 244);

        /**
         * Цвет фильтра при наведенной мыши.
         */
        Color enabledFilterColor = new Color(179, 215, 244);

        /**
         * Цвет назначенной строки.
         */
        Color assignedRowColor = new Color(95, 214, 106);

        /**
         * Цвет выбранной строки.
         */
        Color selectedRowColor = new Color(129, 175, 233);

        /**
         * Цвет строки с заголовками.
         */
        Color tableHeaderColor = new Color(224, 224, 224);

        /**
         * Цвет разделения заголовков.
         */
        Color headerSplitColor = new Color(0, 0, 0);

        /**
         * Не фокусируемые колонки.
         * В конфигурации указывается в качестве массива.
         */
        @CheckerDefinitionValue("unfocused")
        ArrayList<Object> unFocused = new ArrayList<>();

        /**
         * Есть ли скролл снизу.
         * Нужно для правильной отчистки фильтра.
         */
        @CheckerDefinitionValue("has_scroll_bar")
        boolean hasBottomScroll = true;

        /**
         * Служебная колонка.
         */
        @CheckerDefinitionValue("column_count")
        int columnCount = 1;

        /**
         * Есть ли служебная колонка для выделения строк.
         */
        @CheckerDefinitionValue("has_selection_bar")
        boolean hasSelectionBar = true;

        /**
         * Высота вкладок таблицы.
         */
        @CheckerDefinitionValue("page_height")
        int pageHeight = 30;

        /**
         * Количество строк заголовков таблицы.
         */
        @CheckerDefinitionValue("header_count")
        int headerCount = 1;

        /**
         * Ширина пробела между словами вкладок.
         */
        @CheckerDefinitionValue("header_word_space")
        int pageWordSpace = 140;

        /**
         * Максимальная задержка чтения данных.
         */
        @CheckerDefinitionValue("max_wait_delay")
        int maxWaitDelay = 60;

        /**
         * Максимальная задержка чтения данных.
         */
        @CheckerDefinitionValue("filter_calling_try_cont")
        int filterCallingTryCont = 3;

        @CheckerDefinitionValue("has_header")
        boolean hasHeader = true;

        /**
         * Конструктор конфигурации таблицы.
         * @param definition Описание таблицы
         */
        public Config(Map<String, Object> definition) {
            definition.entrySet().parallelStream().forEach(entry -> Arrays
                    .stream(this.getClass().getDeclaredFields()).parallel()
                    .filter(field ->
                            field.isAnnotationPresent(CheckerDefinitionValue.class)
                                    && field.getAnnotation(CheckerDefinitionValue.class).value().equalsIgnoreCase(entry.getKey()))
                    .findFirst().ifPresent(found -> assertDoesNotThrow(() -> found.set(this, entry.getValue()), "Не удалось настроить таблицу")));
        }
    }
}
