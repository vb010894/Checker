package ru.checker.tests.desktop.test.entity;

import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.AutomationException;
import mmarquee.automation.PropertyID;
import mmarquee.automation.controls.Button;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.*;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checker control base entity.
 *
 * @param <Y> Root control type
 * @param <T> Current control type
 * @author vd.zinovev
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public abstract class CheckerBaseEntity<T extends AutomationBase, Y extends AutomationBase> {

    @Setter
    private int waitTimeout = 60;

    /**
     * Elements definition reference.
     */
    final Map<String, String> references = Map.of(
            "forms", "Forms",
            "widgets", "Widgets",
            "windows", "Windows",
            "elements", "Elements"
    );
    /**
     * Control ID.
     * <p>
     * Definition key - 'id'.
     */
    @Setter(AccessLevel.PROTECTED)
    String ID;

    /**
     * Control name.
     * <p>
     * Definition key - 'name'.
     */
    @Setter(AccessLevel.PROTECTED)
    String name;

    /**
     * Current control.
     */
    @Setter(AccessLevel.PROTECTED)
    T control;

    /**
     * Root control.
     */
    final Y root;

    /**
     * Control definition.
     */
    final Map<String, Object> definition;

    /**
     * Used children buttons.
     */
    @Getter
    final Map<String, List<Button>> usedButtons = new HashMap<>();

    /**
     * Used children Edits.
     */
    @Getter
    final Map<String, List<EditBox>> usedEdits = new HashMap<>();

    /**
     * Used children Edits.
     */
    @Getter
    final Map<String, List<Panel>> usedPanels = new HashMap<>();

    /**
     * Used children Edits.
     */
    @Getter
    final Map<String, List<Object>> usedCustoms = new HashMap<>();

    /**
     * Used element definition.
     */
    @Getter
    private final Map<String, Map<String, Object>> elements = new HashMap<>();


    /**
     * Constructor.
     *
     * @param root       Root control
     * @param definition Control definition
     */
    public CheckerBaseEntity(Y root, Map<String, Object> definition) {
        this.definition = definition;
        AtomicReference<String> ID = new AtomicReference<>();
        AtomicReference<String> name = new AtomicReference<>();
        this.getIndicators(ID, name);

        this.ID = ID.get();
        this.name = name.get();


        this.root = root;

        if (definition.containsKey("elements"))
            this.addChildrenDefinition("elements", this.elements);
    }


    /**
     * Get first Custom
     *
     * @param ID Custom ID
     * @return Custom
     */
    public <C> C custom(String ID, Class<C> wrapper) {
        return this.custom(ID, 0, wrapper);
    }

    /**
     * Get Custom by index.
     *
     * @param ID    Custom ID
     * @param index Custom index
     * @return Custom
     */
    public <C> C custom(String ID, int index, Class<C> wrapper) {
        Panel panel;
        if (this.getUsedPanels().containsKey(ID))
            panel = this.getUsedPanels().get(ID).get(0);
        else
            panel = this.convertControlByIndex(ID, Panel.class, index, this.usedPanels);

        return assertDoesNotThrow(() -> wrapper.getConstructor(Panel.class, Map.class).newInstance(panel, this.elements.get(ID)),
                "Не удалось обернуть пользовательский элемент с ID - " + ID);

    }

    /**
     * Get Custom by ID.
     *
     * @param ID Custom ID
     * @return Custom
     */
    public <C> List<C> customs(String ID, Class<C> wrapper) {
        List<Panel> panels;
        if (this.getUsedPanels().containsKey(ID))
            panels = this.getUsedPanels().get(ID);
        else
            panels = this.convertControl(ID, Panel.class, this.usedPanels);


       return panels
                .parallelStream()
                .map(
                        panel -> assertDoesNotThrow(() -> wrapper.getConstructor(Panel.class, Map.class).newInstance(panel, this.elements.get(ID)),
                                "Не удалось обернуть пользовательский элемент с ID - " + ID))
                .collect(Collectors.toList());
    }

    /**
     * Get first Panel
     *
     * @param ID Panel ID
     * @return Panel
     */
    public Panel panel(String ID) {
        return this.panel(ID, 0);
    }

    /**
     * Get Edit by index.
     *
     * @param ID    Panel ID
     * @param index Panel index
     * @return Panel
     */
    public Panel panel(String ID, int index) {
        if (this.getUsedPanels().containsKey(ID))
            return this.getUsedPanels().get(ID).get(0);
        else
            return this.convertControlByIndex(ID, Panel.class, index, this.usedPanels);
    }

    /**
     * Get Panel by ID.
     *
     * @param ID Panel ID
     * @return Panel
     */
    public List<Panel> panels(String ID) {
        if (this.getUsedPanels().containsKey(ID))
            return this.getUsedPanels().get(ID);
        else
            return this.convertControl(ID, Panel.class, this.usedPanels);
    }

    /**
     * Get first edit
     *
     * @param ID Edit ID
     * @return Edit
     */
    public EditBox edit(String ID) {
        return this.edit(ID, 0);
    }

    /**
     * Get Edit by index.
     *
     * @param ID    Edit ID
     * @param index Edit index
     * @return Edit
     */
    public EditBox edit(String ID, int index) {
        if (this.getUsedEdits().containsKey(ID))
            return this.getUsedEdits().get(ID).get(0);
        else
            return this.convertControlByIndex(ID, EditBox.class, index, this.usedEdits);
    }

    /**
     * Get Edit by ID.
     *
     * @param ID Edit ID
     * @return Edit
     */
    public List<EditBox> edits(String ID) {
        if (this.getUsedEdits().containsKey(ID))
            return this.getUsedEdits().get(ID);
        else
            return this.convertControl(ID, EditBox.class, this.usedEdits);
    }

    /**
     * Get first button
     *
     * @param ID Button ID
     * @return button
     */
    public Button button(String ID) {
        return this.button(ID, 0);
    }

    /**
     * Get button by index.
     *
     * @param ID    Button ID
     * @param index Button index
     * @return Button
     */
    public Button button(String ID, int index) {
        if (this.getUsedButtons().containsKey(ID))
            return this.getUsedButtons().get(ID).get(0);
        else
            return this.convertControlByIndex(ID, Button.class, index, this.usedButtons);
    }

    /**
     * Get buttons by ID.
     *
     * @param ID Button ID
     * @return Buttons
     */
    public List<Button> buttons(String ID) {
        if (this.getUsedButtons().containsKey(ID))
            return this.getUsedButtons().get(ID);
        else
            return this.convertControl(ID, Button.class, this.usedButtons);
    }

    /**
     * Convert single control by index
     *
     * @param target Target class
     * @param output Cache output
     * @param <C>    Output control
     * @return Controls
     */
    private <C extends AutomationBase> C convertControlByIndex(String ID, Class<C> target, int index, Map<String, List<C>> output) {
        if (index < 0) {
            assertTrue(this.elements.get(ID).containsKey("index"), "Ключ 'index' не найден в описании");
            index = CheckerTools.castDefinition(this.elements.get(ID).get("index"));
        }

        List<AutomationBase> found = this.getFound(ID);
        assertTrue(found.size() >= (index - 1), String.format("Размер найденных элементов '%d' меньше индекса '%d'", found.size(), index));
        found = List.of(found.get(index));
        List<C> result = found.parallelStream().map(f -> assertDoesNotThrow(() ->
                        target.getConstructor(ElementBuilder.class).newInstance(new ElementBuilder().element(f.getElement()))))
                .collect(Collectors.toList());
        output.put(ID, result);
        return result.get(0);
    }

    /**
     * Get element definition.
     * @param ID Element ID
     * @return Element definition
     */
    public Map<String, Object> getElementDefinition(String ID) {
        assertTrue(this.elements.containsKey(ID), String.format("Элемент с ID '%s' не описан", ID));
        return this.elements.get(ID);
    }

    /**
     * Convert controls
     *
     * @param ID     Control ID
     * @param target Target class
     * @param output Cache output
     * @param <C>    Output control
     * @return Controls
     */
    private <C extends AutomationBase> List<C> convertControl(String ID, Class<C> target, Map<String, List<C>> output) {
        List<AutomationBase> found = this.getFound(ID);
        List<C> result = found.parallelStream().map(f -> assertDoesNotThrow(() ->
                        target.getConstructor(ElementBuilder.class).newInstance(new ElementBuilder().element(f.getElement()))))
                .collect(Collectors.toList());
        output.put(ID, result);
        return result;
    }

    /**
     * Get found elements.
     * @param ID Element ID.
     * @return Found automation controls
     */
    private List<AutomationBase> getFound(String ID) {
        assertTrue(this.elements.containsKey(ID), String.format("Элемент с ID - '%s' не описан в файлах конфигураций", ID));
        Map<String, Object> definition = this.getElements().get(ID);
        AtomicReference<String> currentID = new AtomicReference<>();
        AtomicReference<String> currentName = new AtomicReference<>();
        return this.findRawChildren(definition, currentID, currentName);
    }

    /**
     * Get control original name.
     *
     * @return Name
     */
    public String getOriginalName() {
        assertNotNull(this.control, String.format(
                "Элемент управления с ID - '%s', именем - '%s' не инициализирован",
                this.ID,
                this.name));
        return assertDoesNotThrow(
                () -> this.control.getName(),
                String.format("Не удалось получить имя элемента управления с ID - '%s', именем - '%s'", this.ID, this.name));
    }

    /**
     * Get control classname.
     *
     * @return Classname
     */
    public String getClassName() {
        assertNotNull(this.control, String.format(
                "Элемент управления с ID - '%s', именем - '%s' не инициализирован",
                this.ID,
                this.name));
        return assertDoesNotThrow(
                () -> this.control.getClassName(),
                String.format("Не удалось получить класс элемента управления с ID - '%s', именем - '%s'", this.ID, this.name));
    }

    /**
     * Get control rectangle.
     *
     * @return Rectangle
     */
    public WinDef.POINT getClickablePoint() {
        assertNotNull(this.control, String.format(
                "Элемент управления с ID - '%s', именем - '%s' не инициализирован",
                this.ID,
                this.name));
        return assertDoesNotThrow(
                () -> this.control.getClickablePoint(),
                String.format("Не удалось получить точку нажатия элемента управления с ID - '%s', именем - '%s'", this.ID, this.name));
    }


    /**
     * Get control rectangle.
     *
     * @return Rectangle
     */
    public Rectangle getRectangle() {
        assertNotNull(this.control, String.format(
                "Элемент управления с ID - '%s', именем - '%s' не инициализирован",
                this.ID,
                this.name));
        return assertDoesNotThrow(
                () -> this.control.getBoundingRectangle().toRectangle(),
                String.format("Не удалось получить положение элемента управления с ID - '%s', именем - '%s'", this.ID, this.name));
    }

    /**
     * Add children definition.
     *
     * @param node   Children node
     * @param output Definition output
     */
    protected void addChildrenDefinition(String node, Map<String, Map<String, Object>> output) {
        assertTrue(this.references.containsKey(node), "Нет задано отношение нода - папка. Нода - " + node);
        if (this.definition.containsKey(node)) {
            List<Map<String, Object>> nodes = CheckerTools.castDefinition(this.definition.get(node));
            nodes.parallelStream().forEach(n -> {
                Map<String, Object> childDefinition;
                if (n.containsKey("path")) {
                    String app = CheckerDesktopTest.getApplication().getName();
                    String path = String.format("/Tests/%s/%s/%s", app, this.references.get(node), n.get("path"));
                    childDefinition = CheckerTools.convertYAMLToMap(String.format(path));
                } else {
                    childDefinition = (Map<String, Object>) n.get(node.substring(0, node.length() - 1));
                }
                assertTrue(
                        childDefinition.containsKey("id"),
                        "Не задан ID ля элемента. \n"
                                + childDefinition
                                .entrySet()
                                .stream()
                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                .collect(Collectors.joining(",", "{", "}")));
                String id = CheckerTools.castDefinition(childDefinition.get("id"));
                output.put(id, childDefinition);
            });
        }
    }

    public boolean findMySelf() {
        return this.findMySelf(true);
    }

    /**
     * Find and save self-control.
     */
    public boolean findMySelf(boolean throwIfNotFound) {
        AtomicReference<String> ID = new AtomicReference<>();
        AtomicReference<String> name = new AtomicReference<>();
        List<AutomationBase> found = this.findControl(this.root, definition, ID, name);
        if(!throwIfNotFound && found.isEmpty())
            return false;

        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        this.ID = ID.get();
        this.name = ID.get();

        if (found.size() > 0) {
            if (this.definition.containsKey("index")) {
                int index = CheckerTools.castDefinition(this.definition.get("index"));
                assertTrue(
                        index <= found.size() - 1,
                        String.format(
                                "Индекс компонента управления  с ID - '%s', именем - '%s' больше количества найденных",
                                ID.get(),
                                name.get()));
                this.control = CheckerTools.castDefinition(found.get(index));
            }
            System.out.printf("##[warning] Найдено несколько элементов управления с ID - '%s', именем - '%s'. Выбран первый - ''\n", ID.get(), name.get());
            this.control = CheckerTools.castDefinition(found.get(0));
        }

        return true;
    }

    /**
     * Find first control child.
     *
     * @param definition Control definition
     * @param ID         Control ID atomic
     * @param name       Control name atomic
     * @return First child
     */
    protected AutomationBase findFirstRawChild(Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        List<AutomationBase> found = this.findRawChildren(definition, ID, name);
        return found.get(0);
    }

    /**
     * Get RAW children control by index.
     * <p>
     * Definition key - 'index'.
     *
     * @param definition Control definition
     * @param ID         Control id atomic
     * @param name       Control name atomic
     * @return Control by index
     */
    protected AutomationBase findRawChildByIndex(Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        List<AutomationBase> found = this.findControl(this.control, definition, ID, name);
        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        if (this.definition.containsKey("index")) {
            int index = CheckerTools.castDefinition(this.definition.get("index"));
            assertTrue(
                    index <= found.size() - 1,
                    String.format(
                            "Индекс компонента управления  с ID - '%s', именем - '%s' больше количества найденных",
                            ID.get(),
                            name.get()));
            return CheckerTools.castDefinition(found.get(index));
        }
        System.out.printf("##[warning] Найдено несколько элементов управления с ID - '%s', именем - '%s'. Выбран первый - ''\n", ID.get(), name.get());
        return CheckerTools.castDefinition(found.get(0));
    }

    /**
     * Get Raw children controls list.
     *
     * @param definition Control definition
     * @param ID         Control ID atomic
     * @param name       Control name atomic
     * @return List of children controls
     */
    protected List<AutomationBase> findRawChildren(Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        List<AutomationBase> found = this.findControl(this.control, definition, ID, name);
        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        return found;
    }

    /**
     * !!! Searching control main method.
     *
     * @param root       Root control
     * @param definition Control definition
     * @param ID         Control ID atomic
     * @param name       Control name atomic
     * @return List of children control
     */
    protected List<AutomationBase> findControl(AutomationBase root,
                                               Map<String, Object> definition,
                                               AtomicReference<String> ID,
                                               AtomicReference<String> name) {
        this.getIndicators(ID, name);
        String currentID = ID.get();
        String currentName = name.get();
        assertTrue(definition.containsKey("search"), String.format("Не задан ключ поиска компонента управления c ID - '%s', name - '%s'", currentID, currentName));
        Map<String, Object> search = CheckerTools.castDefinition(definition.get("search"));

        List<AutomationBase> result = new LinkedList();
        int limit = this.waitTimeout;

        while (result.isEmpty() && limit >= 0) {
            try {
                result = root.getChildren(true)
                        .parallelStream()
                        .filter(control -> this.isSearchingControl(control, search))
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                System.out.println("Повторная попытка найти элемент. Осталось - " + limit);
            } finally {
                limit--;
                assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось выполнить ожидание элемента");
            }
        }

        return result;
    }

    /**
     * Searching control conditions.
     *
     * @param control    Raw controls
     * @param definition Control definition
     * @return Condition result
     */
    private boolean isSearchingControl(AutomationBase control, Map<String, Object> definition) {
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
     * Get control ID and name.
     *
     * @param ID   Control ID atomic
     * @param name Control name atomic
     */
    protected void getIndicators(AtomicReference<String> ID, AtomicReference<String> name) {
        assertTrue(definition.containsKey("id"), "В описании не найден ключ 'id', который задает ID элемента");
        String currentID = CheckerTools.castDefinition(definition.get("id"));
        ID.set(currentID);
        String currentName;
        if (!definition.containsKey("name")) {
            System.out.printf("##[warning] Не задано имя компонента управления с ID - '%s'. Задание имени по умолчанию - ''\n", currentID);
            currentName = "";
        } else {
            currentName = CheckerTools.castDefinition(definition.get("name"));
        }

        name.set(currentName);
    }

}
