package ru.checker.tests.desktop.test.entity;

import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.AutomationException;
import mmarquee.automation.PropertyID;
import mmarquee.automation.controls.AutomationBase;
import ru.checker.tests.base.test.CheckerTestCase;
import ru.checker.tests.base.utils.CheckerTools;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checker control base entity.
 * @author vd.zinovev
 *
 * @param <T> Root control type
 * @param <Y> Current control type
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public abstract class CheckerBaseEntity<T extends AutomationBase, Y extends AutomationBase> {

    /**
     * Control ID.
     *
     * Definition key - 'id'.
     */
    String ID;

    /**
     * Control name.
     *
     * Definition key - 'name'.
     */
    String name;

    /**
     * Current control.
     */
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
     * Used element definition.
     */
    @Getter
    private final Map<String, Map<String, Object>> elements = new HashMap<>();

    /**
     * Constructor.
     * @param root Root control
     * @param definition Control definition
     */
    public CheckerBaseEntity(Y root, Map<String, Object> definition) {
        AtomicReference<String> ID = new AtomicReference<>();
        AtomicReference<String> name = new AtomicReference<>();
        this.getIndicators(ID, name);

        this.ID = ID.get();
        this.name = name.get();

        this.definition = definition;
        this.root = root;

        if(definition.containsKey("elements"))
            this.addChildrenDefinition("elements","Elements", this.elements);
    }

    //public List<Button> buttons =

    /**
     * Get control original name.
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
     * @param node Children node
     * @param dir Reference directory
     * @param output Definition output
     */
    private void addChildrenDefinition(String node, String dir, Map<String, Map<String, Object>> output) {
        if (this.definition.containsKey(node)) {
            List<Map<String, Object>> nodes = CheckerTools.castDefinition(this.definition.get(node));
            nodes.parallelStream().forEach(n -> {
                Map<String, Object> childDefinition;
                if (n.containsKey("path")) {
                    String app = CheckerTestCase.getApplication().getName();
                    String path = String.format("/Test/%s/%s/%s", app, dir, n.get("path"));
                    Map<String, Object> definition = CheckerTools.convertYAMLToMap(String.format(path));
                    childDefinition = definition;
                } else {
                    childDefinition = n;
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

    /**
     * Find and save self-control.
     */
    public void findMySelf() {
        AtomicReference<String> ID = new AtomicReference<>();
        AtomicReference<String> name = new AtomicReference<>();
        List<AutomationBase> found = this.findControl(this.root, definition, ID, name);
        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        this.ID = ID.get();
        this.name = ID.get();

        if(found.size() > 0) {
            if(this.definition.containsKey("index")) {
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
    }

    /**
     * Find first control child.
     *
     * @param definition Control definition
     * @param ID Control ID atomic
     * @param name Control name atomic
     *
     * @return First child
     */
    protected AutomationBase findFirstRawChild(Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        List<AutomationBase> found = this.findRawChildren(definition, ID, name);
        return found.get(0);
    }

    /**
     * Get RAW children control by index.
     *
     * Definition key - 'index'.
     *
     * @param definition Control definition
     * @param ID Control id atomic
     * @param name Control name atomic
     *
     * @return Control by index
     */
    protected AutomationBase findRawChildByIndex(Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        List<AutomationBase> found =  this.findControl(this.control, definition, ID, name);
        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        if(this.definition.containsKey("index")) {
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
     * @param ID Control ID atomic
     * @param name Control name atomic
     *
     * @return List of children controls
     */
    protected List<AutomationBase> findRawChildren(Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        List<AutomationBase> found =  this.findControl(this.control, definition, ID, name);
        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        return found;
    }

    /**
     * !!! Searching control main method.
     *
     * @param root Root control
     * @param definition Control definition
     * @param ID Control ID atomic
     * @param name Control name atomic
     *
     * @return List of children control
     */
    protected List<AutomationBase> findControl(AutomationBase root, Map<String, Object> definition, AtomicReference<String> ID, AtomicReference<String> name) {
        this.getIndicators(ID, name);
        String currentID = ID.get();
        String currentName = name.get();
        assertTrue(definition.containsKey("search"), String.format("Не задан ключ поиска компонента управления c ID - '%s', name - '%s'", currentID, currentName));
        Map<String, Object> search = CheckerTools.castDefinition(definition.get("search"));

        return assertDoesNotThrow(() ->
            root.getChildren(true)
                    .parallelStream()
                    .filter(control -> this.isSearchingControl(control, search))
                    .collect(Collectors.toList()), String.format("Не удалось найти и создать элемент c ID - '%s', name - '%s'", currentID, currentName));
    }

    /**
     * Searching control conditions.
     * @param control Raw controls
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
     * @param ID Control ID atomic
     * @param name Control name atomic
     */
    private void getIndicators(AtomicReference<String> ID, AtomicReference<String> name) {
        assertTrue(definition.containsKey("id"),"В описании не найден ключ 'id', который задает ID элемента");
        String currentID = CheckerTools.castDefinition(definition.get("id"));
        ID.set(currentID);
        String currentName = "";
        if(!definition.containsKey("name"))
            System.out.printf("##[warning] Не задано имя компонента управления с ID - '%s'. Задание имени по умолчанию - ''\n", currentID);
        else
            currentName = CheckerTools.castDefinition(definition.get("name"));

        name.set(currentName);
    }

}
