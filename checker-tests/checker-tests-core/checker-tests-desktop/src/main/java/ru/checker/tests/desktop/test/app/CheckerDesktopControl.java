package ru.checker.tests.desktop.test.app;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.PropertyID;
import mmarquee.automation.controls.*;
import mmarquee.automation.controls.Button;
import mmarquee.automation.controls.Panel;
import ru.checker.tests.base.test.app.CheckerControl;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.base.robot.CheckerFieldsUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/***
 * Desktop control.
 * Desktop control
 * @author vd.zinovev
 *
 * @param <T> Control type.
 */
@Getter
@Setter
@Log4j2(topic = "TEST CASE")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public abstract class CheckerDesktopControl<T extends AutomationBase> extends CheckerControl<AutomationBase, T> {

    /**
     * Class name and name locators for calculation.
     */
    List<String> calculationLocators = List.of(
            "grid",
            "button",
            "combo",
            "list",
            "text",
            "input",
            "field",
            "radio",
            "check",
            "date",
            "calendar",
            "filter"
    );

    /**
     * control types locators for calculation.
     */
    List<ControlType> calculationTypes = List.of(
            ControlType.List,
            ControlType.Button,
            ControlType.CheckBox,
            ControlType.DataGrid,
            ControlType.ComboBox,
            ControlType.Table,
            ControlType.RadioButton,
            ControlType.Tab,
            ControlType.Tree,
            ControlType.Text,
            ControlType.Calendar,
            ControlType.Document,
            ControlType.Edit,
            ControlType.Menu
    );

    /**
     * Constructor by definition path.
     *
     * @param PATH Definition path
     */
    public CheckerDesktopControl(String PATH) {
        super(PATH);
    }

    /**
     * Constructor by definition.
     *
     * @param definition Control definition
     */
    public CheckerDesktopControl(Map<String, Object> definition) {
        super(definition);
    }

    /**
     * Find and create control.
     *
     * @param root Root element
     */
    @Override
    protected void createControl(AutomationBase root) {
        this.createControl(root, 0);
        this.calculate();
    }

    /**
     * Find and create control.
     * When control's searching return several.
     *
     * @param root Root element
     * @param index Control index
     */
    @Override
    @SuppressWarnings("unchecked")
    public void createControl(AutomationBase root, int index) {
        this.setControl((T) this.findChildrenByRoot(root, this.getSearch()).get(index));
        this.calculate();
    }

    /**
     * Children elements.
     * @param root Root
     * @param definition Child definition
     * @return Children
     */
    public List<AutomationBase> findChildrenByRoot(AutomationBase root, Map<String, Object> definition) {
        return assertDoesNotThrow(
                () -> {
                    List<AutomationBase> result = new LinkedList<>();
                    int timeout = 60000;
                    while (result.isEmpty() && timeout > 0) {
                        try {
                            result = root.getChildren(true)
                                    .parallelStream()
                                    .filter(control -> this.isSearchingControl(control, definition))
                                    .collect(Collectors.toList());
                            if(result.isEmpty()) {
                                Thread.sleep(1000);
                                timeout -= 1000;
                            }
                        } catch (Exception ex) {
                            Thread.sleep(1000);
                            timeout -= 1000;
                        }

                    }
                    return result;
                },
                "Не удалось найти элемент по условиям\n" + this.getSearch()
        );
    }

    /**
     * Searching control conditions.
     * @param control Raw controls
     * @param definition Control definition
     * @return Condition result
     */
    private boolean isSearchingControl(AutomationBase control, Map<String, Object> definition) {
        assertFalse(definition.isEmpty(), "Для поиска элемента должен быть заполнен ключ 'search'");
        return definition.entrySet().parallelStream().allMatch(entry -> {
                try {
                    Object test = control.getElement().getPropertyValue(PropertyID.valueOf(entry.getKey()).getValue());
                    if (test == null)
                        return false;
                    return Objects.equals(
                            control
                                    .getElement()
                                    .getPropertyValue(PropertyID.valueOf(entry.getKey()).getValue())
                                    .toString().trim(),
                            entry.getValue().toString());
                } catch (AutomationException e) {
                    return false;
                }
            });
    }

    /**
     * Find control children.
     *
     * @return List of control
     */
    @Override
    public List<AutomationBase> findChildren(Map<String, Object> definition) {
        return CheckerTools.castDefinition(this.findChildrenByRoot(this.getControl(), definition));
    }

    /**
     * Create defined elements.
     */
    protected void createElements() {
        if (this.getDefinition().containsKey("elements")) {
            List<Map<String, Object>> elements = CheckerTools.castDefinition(this.getDefinition().get("elements"));
            elements.parallelStream().forEach(e -> {
                Map<String, Object> temp = CheckerTools.castDefinition(e.get("element"));
                assertTrue(temp.containsKey("id"), "Не задан ID для элемента");
                this.getElements().put(CheckerTools.castDefinition(temp.get("id")), temp);
            });
        }
    }

    /**
     * Calculate useful controls.
     */
    @Override
    public void calculate() {
        log.info("Подсчет важных элементов на элементе {}", this.getID());
        this.setElementsCount(assertDoesNotThrow(() -> {
            List<AutomationBase> controls = this.getControl().getChildren(true);
            return controls.parallelStream().filter(f -> {
                try {
                    return this.calculationLocators.parallelStream().anyMatch(f.getClassName().toLowerCase()::contains)
                            ||
                            this.calculationLocators.parallelStream().anyMatch(f.getName().toLowerCase()::contains)
                            ||
                            this.calculationTypes.contains(ControlType.fromValue(f.getElement().getControlType()));
                } catch (Exception ex) {
                    return false;
                }

            }).count();
        }));
        log.info("На элемент '{}' найдено '{}' элементов", this.getID(), this.getElementsCount());
    }

    /**
     * Get first combobox.
     * @param ID Control ID
     * @return Control
     */
    public ComboBox firstCombobox(String ID) {
        return this.combobox(ID, 0);
    }


    /**
     * Get Combobox by index.
     * @param ID Control ID
     * @param index Control index
     * @return Control
     */
    public ComboBox combobox(String ID, int index) {
        return assertDoesNotThrow(() ->
                        new ComboBox(new ElementBuilder().element(this.element(ID, index).getElement())),
                "Не удалось конвертировать в кнопку. ID - " + ID);
    }

    /**
     * Find comboboxes.
     * @param ID Panel ID
     * @return Panels
     */
    public List<ComboBox> comboboxes (String ID) {
        return assertDoesNotThrow(
                () -> this.element(ID)
                        .parallelStream()
                        .map(element -> new ComboBox(new ElementBuilder().element(element.getElement())))
                        .collect(Collectors.toList()), "Не удалось конвертировать в панель. ID - " + ID);
    }

    /**
     * Get first edit.
     * @param ID Control ID
     * @return Control
     */
    public EditBox firstEdit(String ID) {
        return this.edit(ID, 0);
    }


    /**
     * Get edit by index.
     * @param ID Control ID
     * @param index Control index
     * @return Control
     */
    public EditBox edit(String ID, int index) {
        return assertDoesNotThrow(() ->
                        new EditBox(new ElementBuilder().element(this.element(ID, index).getElement())),
                "Не удалось конвертировать в кнопку. ID - " + ID);
    }

    public EditBox labelEdit(String ID) {
        Map<String, Object> definition = this.getElement(ID);
        assertTrue(definition.containsKey("label"), "Не заполнен ключ 'label'");
        String label = CheckerTools.castDefinition(definition.get("label"));
        return new EditBox(new ElementBuilder().element(CheckerFieldsUtils.filterFieldsByLabel(this.element(ID), label).getElement()));
    }

    /**
     * Find edits.
     * @param ID Panel ID
     * @return Panels
     */
    public List<EditBox> edits(String ID) {
        return assertDoesNotThrow(
                () -> this.element(ID)
                        .parallelStream()
                        .map(element -> new EditBox(new ElementBuilder().element(element.getElement())))
                        .collect(Collectors.toList()), "Не удалось конвертировать в панель. ID - " + ID);
    }

    /**
     * Get first button.
     * @param ID Control ID
     * @return Control
     */
    public Button firstButton(String ID) {
        return this.button(ID, 0);
    }


    /**
     * Get button by index.
     * @param ID Control ID
     * @param index Control index
     * @return Control
     */
    public Button button(String ID, int index) {
        return assertDoesNotThrow(() ->
                        new Button(new ElementBuilder().element(this.element(ID, index).getElement())),
                "Не удалось конвертировать в кнопку. ID - " + ID);
    }

    /**
     * Find buttons.
     * @param ID Panel ID
     * @return Panels
     */
    public List<Button> buttons(String ID) {
        return assertDoesNotThrow(
                () -> this.element(ID)
                        .parallelStream()
                        .map(element -> new Button(new ElementBuilder().element(element.getElement())))
                        .collect(Collectors.toList()), "Не удалось конвертировать в панель. ID - " + ID);
    }


    /**
     * Get first panel.
     * @param ID Control ID
     * @return Control
     */
    public Panel firstPanel(String ID) {
        return this.panel(ID, 0);
    }


    /**
     * Get panel by index.
     * @param ID Control ID
     * @param index Control index
     * @return Control
     */
    public Panel panel(String ID, int index) {
        return assertDoesNotThrow(() ->
                new Panel(new ElementBuilder().element(this.element(ID, index).getElement())),
                "Не удалось конвертировать в панель. ID - " + ID);
    }

    /**
     * Find panels.
     * @param ID Panel ID
     * @return Panels
     */
    public List<Panel> panels(String ID) {
        return assertDoesNotThrow(
                () -> this.element(ID)
                .parallelStream()
                .map(element -> new Panel(new ElementBuilder().element(element.getElement())))
                .collect(Collectors.toList()), "Не удалось конвертировать в панель. ID - " + ID);
    }

    /**
     * Get first control.
     * @param ID Control ID
     * @return Control
     */
    public AutomationBase firstElement(String ID) {
        return this.element(ID, 0);
    }

    /**
     * Get control by index.
     * @param ID Control ID
     * @param index Control index
     * @return Control
     */
    public AutomationBase element(String ID, int index) {
        if (this.getElement(ID).containsKey("index") && index == -1) {
            index = CheckerTools.castDefinition(this.getElement(ID).get("index"));
        }
        List<AutomationBase> controls = this.element(ID);
        assertTrue(
                (controls.size() - 1) >= index,
                String.format(
                        "Индекс выходит за пределы найденных элементов. ID - %s, index - %d, количеств элементов - %d",
                        ID, index, controls.size() - 1));
        return controls.get(index);
    }

    /**
     * Find controls by ID
     * @param ID Control ID
     * @return Controls
     */
    public List<AutomationBase> element(String ID) {
        return this.getControl(ID);
    }

    /**
     * Get list control by definition.
     * @param ID Control ID
     * @return List of controls
     */
    public List<AutomationBase> getControl(String ID) {
        assertTrue(this.getElements().containsKey(ID), "Не найдено описание элемента с ID - " + ID);
        assertTrue(this.getElements().get(ID).containsKey("search"), "Не найдены условия поиска('search') элемента с ID - " + ID);
        Map<String, Object> elementSearch = CheckerTools.castDefinition(this.getElements().get(ID).get("search"));
        List<AutomationBase> controls = this.findChildren(elementSearch);
        assertFalse(controls.isEmpty(), "Не найдено элементов. ID - " + ID);
        return controls;
    }

    public Rectangle getRectangle() {
        assertNotNull(this.getControl(), "Элемент не инициализирован. После создания вызовите метод 'createControl'");
        return assertDoesNotThrow(
                () -> this.getControl().getBoundingRectangle().toRectangle(),
                "Не удалось получить местоположения элемента. Элемент" + this.getOutName());
    }

}
