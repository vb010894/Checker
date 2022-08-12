package ru.checker.tests.ssm.controls.grid;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
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
import ru.checker.tests.desktop.base.robot.CheckerDesktopMarker;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.annotations.CheckerDefinitionValue;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
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

    final Panel control;
    final Robot robot;
    final Map<String, Object> DEFINITION;

    SSMGridData data;
    Rectangle headerRectangle;

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
     * Get table rectangle.
     *
     * @return Rectangle
     */
    public Rectangle getRectangle() {
        return assertDoesNotThrow(
                () -> this.control.getBoundingRectangle().toRectangle(),
                "Не удалось получить расположение таблицы");
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

            CheckerDesktopTest.getCurrentApp().waitApp();

            stringData = assertDoesNotThrow(() -> {
                Thread.sleep(1000);
                return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
            }, "Не удалось получить данные из буфера");
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
     *
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
     *
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

            AtomicReference<Rectangle> cellRectangle = new AtomicReference<>();
            SSMGridData data = this.getDataFromRow(0, cellRectangle);
            assertNotNull(cellRectangle.get(), "Не удалось получить координаты строки");

            AutomationMouse.getInstance().setLocation(cellRectangle.get().x + 20, cellRectangle.get().y);

            this.robot.keyPress(KeyEvent.VK_CONTROL);
            AutomationMouse.getInstance().leftClick();
            this.robot.keyRelease(KeyEvent.VK_CONTROL);

            log.info("Проверка выделения");
            boolean found = false;
            for (int i = cellRectangle.get().x; i < (int) this.control.getBoundingRectangle().toRectangle().getMaxX(); i++) {
                if (this.robot.getPixelColor(i, (int) cellRectangle.get().getCenterY()).equals(this.config.acceptedRowColor)) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "Строка не была выделена");
            log.info("Строка успешно выделена");
            this.data = data;
            log.info("Данные считаны");
            return this.data;
        }, "Не удалось выделить ячейку");
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
        if (checkContainsData)
            assertNotEquals(stringData, "", "Не удалось считать данные из таблицы");

        SSMGridData data = new SSMGridData(stringData);
        data.convert();
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

        return this.readData(run, true);

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
     *
     * @param config    Condition configurer
     */
    public void filterByGUI(ConditionConfigurer config) {
        String[] columns = new String[this.config.unFocused.size()];
        AtomicInteger index = new AtomicInteger(0);
        this.config.unFocused.forEach(membder -> {
            columns[index.getAndIncrement()] = membder.toString();
        });
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
        for (int i = 0; i < 4; i++) {
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

                Element focused = assertDoesNotThrow(() -> {
                    Thread.sleep(8000);
                    return UIAutomation.getInstance().getFocusedElement();
                }, "Не удалось найти ячейку-локатор строки");
                this.fillFilterDialog(focused, config);
                log.info("Фильтрация выполнена");
                break;
            } catch (Exception | Error ex) {
                log.warn("Повторная попытка фильтрации. Так как была прервана по ошибке");
                if(i != 3) {
                    assertDoesNotThrow(() -> Thread.sleep(1000));
                } else {
                    fail(ex);
                }
            }

        }

    }

    private Rectangle moveToCell(Point rowPoint, SSMGridData data, String targetCell, String columnCondition, CheckerOCRLanguage language, String... unFocused) {
        List<String> unFocusedList = Arrays.asList(unFocused);
        AtomicReference<Rectangle> out = new AtomicReference<>();

        this.robot.setAutoDelay(50);
        this.robot.keyPress(KeyEvent.VK_HOME);
        this.robot.keyRelease(KeyEvent.VK_HOME);

        this.robot.keyPress(KeyEvent.VK_ESCAPE);
        this.robot.keyRelease(KeyEvent.VK_ESCAPE);

        for (String header: data.getHeaders()) {
            if (!unFocusedList.contains(header)) {
                if (header.equals(targetCell)) {
                    this.robot.keyPress(KeyEvent.VK_ENTER);
                    this.robot.keyRelease(KeyEvent.VK_ENTER);

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
     * Fill filter dialog window.
     *
     * @param focused Focused element after filter button was pressed
     */
    private void fillFilterDialog(Element focused, ConditionConfigurer config) {
        // поиск родительского окна фильтра
        Element focusedEl = focused;
        AtomicReference<Element> temp = new AtomicReference<>(focusedEl);
        int controlType = 0;
        while (controlType != ControlType.Window.getValue()) {
            temp.set(assertDoesNotThrow(() -> UIAutomation.getInstance().getControlViewWalker().getParentElement(temp.get())));
            int ct = assertDoesNotThrow(() -> temp.get().getControlType());
            if (ct == ControlType.Window.getValue()) {
                focusedEl = temp.get();
                break;
            }
        }

        Window dialog = new Window(new ElementBuilder().element(focusedEl));
        assertDoesNotThrow(() -> assertEquals("Настройка фильтра", dialog.getName(), "Под фокусом находится окно не являющейся окном фильтрации"));
        List<EditBox> edits = assertDoesNotThrow(
                () -> dialog.getChildren(true).parallelStream().filter(control -> {
                            try {
                                return control.getElement().getControlType() == ControlType.Edit.getValue();
                            } catch (AutomationException e) {
                                return false;
                            }
                        })
                        .map(element -> new EditBox(new ElementBuilder().element(element.getElement())))
                        .collect(Collectors.toList()),
                "Не удалось заполнить окно фильтрации");
        this.fillFilterConditionFiled(dialog, edits, config);
        this.fillFilterValueFiled(edits, config);
        assertDoesNotThrow(() -> dialog.getButton("OK").click(), "Не удалось нажать кнопку 'ОК' в окне фильтра");
    }

    /**
     * Fill filter dialog value fields.
     *
     * @param edits      Dialog edits
     * @param configurer Condition configurer
     */
    private void fillFilterValueFiled(List<EditBox> edits, ConditionConfigurer configurer) {
        List<EditBox> conditionFields = edits.parallelStream().filter(control -> {
            try {
                return control.getClassName().equals("TcxCustomInnerTextEdit");
            } catch (AutomationException e) {
                return false;
            }
        }).collect(Collectors.toList());

        assertFalse(conditionFields.isEmpty(), "Не удалось найти поля-значения фильтра");

        AtomicInteger minY = new AtomicInteger((int) this.getRectangle().getMaxY());
        AtomicReference<EditBox> higherEdit = new AtomicReference<>();
        conditionFields.forEach(field -> {
            int y = assertDoesNotThrow(() -> field.getBoundingRectangle().top, "Не удалось получить высоту поля-значения фильтра");
            if (y < minY.get()) {
                higherEdit.set(field);
                minY.set(y);
            }
        });

        EditBox firstConditionField = higherEdit.get();
        assertNotNull(configurer.value1, "В конфигурации фильтрации обязательно должна быть заполнена переменная 'value1'");
        assertDoesNotThrow(() -> {
            Thread.sleep(1000);
            firstConditionField.setValue(configurer.value1);
        }, "Не удалось вставить значение в первое условие фильтрации");

        if (configurer.separator != null && configurer.separator != Separator.NONE) {
            AtomicInteger maxY = new AtomicInteger(0);
            AtomicReference<EditBox> lowerEdit = new AtomicReference<>();
            conditionFields.forEach(field -> {
                int y = assertDoesNotThrow(() -> field.getBoundingRectangle().top, "Не удалось получить высоту поля-значения фильтра");
                if (y > maxY.get()) {
                    lowerEdit.set(field);
                    maxY.set(y);
                }
            });

            EditBox secondField = lowerEdit.get();
            assertNotNull(configurer.value2, "В конфигурации фильтрации должна быть заполнена переменная 'value2'");
            assertDoesNotThrow(() -> {
                Thread.sleep(1000);
                secondField.setValue(configurer.value2);
            }, "Не удалось вставить значение в первое условие фильтрации");
        }
    }

    /**
     * Fill condition fields.
     *
     * @param dialog     Filter dialog
     * @param edits      Filter edits
     * @param configurer Conditions configurer
     */
    private void fillFilterConditionFiled(Window dialog, List<EditBox> edits, ConditionConfigurer configurer) {
        List<EditBox> conditionFields = edits.parallelStream().filter(control -> {
            try {
                return control.getClassName().equals("TcxCustomComboBoxInnerEdit");
            } catch (AutomationException e) {
                return false;
            }
        }).collect(Collectors.toList());

        assertFalse(conditionFields.isEmpty(), "Не удалось найти поля-условия фильтра");

        AtomicInteger minY = new AtomicInteger((int) this.getRectangle().getMaxY());
        AtomicReference<EditBox> higherEdit = new AtomicReference<>();
        conditionFields.forEach(field -> {
            int y = assertDoesNotThrow(() -> field.getBoundingRectangle().top, "Не удалось получить высоту поля-условия фильтра");
            if (y < minY.get()) {
                higherEdit.set(field);
                minY.set(y);
            }
        });

        EditBox firstConditionField = higherEdit.get();
        assertNotNull(configurer.condition1.value, "В конфигурации фильтрации обязательно должна быть заполнена переменная 'condition1'");
        assertDoesNotThrow(() -> {
            Thread.sleep(1000);
            firstConditionField.setValue(configurer.condition1.value);
            Thread.sleep(1000);
        }, "Не удалось вставить значение в первое условие фильтрации");

        if (configurer.separator != null && configurer.separator != Separator.NONE) {
            AtomicInteger maxY = new AtomicInteger(0);
            AtomicReference<EditBox> lowerEdit = new AtomicReference<>();
            conditionFields.forEach(field -> {
                int y = assertDoesNotThrow(() -> field.getBoundingRectangle().top, "Не удалось получить высоту поля-условия фильтра");
                if (y > maxY.get()) {
                    lowerEdit.set(field);
                    maxY.set(y);
                }
            });

            EditBox secondField = lowerEdit.get();
            assertDoesNotThrow(() -> dialog.getButton(configurer.separator.value).click(), "Не удалось переключить соединение условий - " + configurer.separator.getValue());
            assertNotNull(configurer.condition2.value, "В конфигурации фильтрации должна быть заполнена переменная 'condition2'");
            assertDoesNotThrow(() -> {
                Thread.sleep(1000);
                secondField.setValue(configurer.condition2.value);
            }, "Не удалось вставить значение в первое условие фильтрации");
        }
    }

    /**
     * Find and click filter button on column header.
     * The search will be done
     * when the cell locator is found
     * and the mouse selection button is color-coded
     * in the variable 'filterColor'.
     *
     * @param cellRectangle Cell-locator rectangle
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
        String value1;
        String value2;
        Condition condition1;
        Condition condition2;
        Separator separator;
    }

    /**
     * GUI condition separator.
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

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    @NoArgsConstructor
    public static class Config {
        Color headerColor = new Color(244, 244, 244);
        Color acceptedRowColor = new Color(255, 85, 85);
        Color filterColor = new Color(181, 181, 181);
        Color clearFilterColor = new Color(244, 244, 244);
        Color enabledFilterColor = new Color(179, 215, 244);

        @CheckerDefinitionValue("unfocused")
        ArrayList<Object> unFocused = new ArrayList<>();

        @CheckerDefinitionValue("has_scroll_bar")
        boolean hasBottomScroll = true;
        @CheckerDefinitionValue("column_count")
        int columnCount = 1;
        @CheckerDefinitionValue("has_selection_bar")
        boolean hasSelectionBar = true;

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
