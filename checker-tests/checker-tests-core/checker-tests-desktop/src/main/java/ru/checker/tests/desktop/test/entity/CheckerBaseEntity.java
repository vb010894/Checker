package ru.checker.tests.desktop.test.entity;

import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.PropertyID;
import mmarquee.automation.controls.Button;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.*;
import ru.checker.tests.base.enums.CheckerOCRLanguage;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.desktop.utils.CheckerFieldsUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
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
@Log4j2
@SuppressWarnings("unused")
public abstract class CheckerBaseEntity<T extends AutomationBase, Y extends AutomationBase> {

    @Setter
    @Getter
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
     * Обновление элемента.
     * <p>
     * Принудительно ищет элемент управления.
     */
    public void refresh() {
        log.debug("Обновление элемента. ID - '{}', имя - '{}'", this.ID, this.getName());
        this.findMySelf();
        log.debug("Обновление прошло успешно");
    }

    /**
     * Get first Custom
     *
     * @param ID Custom ID
     * @return Custom
     */
    public <C> C custom(String ID, Class<C> wrapper) {
        Map<String, Object> definition = this.getElementDefinition(ID);
        if (definition.containsKey("index")) {
            return this.custom(ID, CheckerTools.castDefinition(definition.get("index")), wrapper);
        } else if (definition.containsKey("label")) {
            Panel result = this.panels(ID)
                    .parallelStream()
                    .filter(p -> CheckerFieldsUtils.getLabel(p.getElement()).startsWith(CheckerTools.castDefinition(definition.get("label"))))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Не найден элемент по надписи - '" + definition.get("label") + "'"));
            return assertDoesNotThrow(() -> wrapper.getConstructor(Panel.class, Map.class).newInstance(result, definition),
                    "Не удалось обернуть пользовательский элемент с ID - " + ID);
        } else {
            return this.custom(ID, 0, wrapper);
        }
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
            panels = this.convertControls(ID, Panel.class, this.usedPanels);


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
        return this.convertControl(ID, Panel.class, this.usedPanels);
    }

    /**
     * Get Edit by index.
     *
     * @param ID    Panel ID
     * @param index Panel index
     * @return Panel
     */
    public Panel panel(String ID, int index) {
        List<Panel> panels = this.panels(ID);
        assertTrue(
                panels.size() >= index,
                "Количество панелей '" + panels.size()
                        + "' меньше, чем index '" + index + "'");
        return panels.get(index);
    }

    /**
     * Get Panel by ID.
     *
     * @param ID Panel ID
     * @return Panel
     */
    public List<Panel> panels(String ID) {
        return this.convertControls(ID, Panel.class, this.usedPanels);
    }

    /**
     * Get first edit
     *
     * @param ID Edit ID
     * @return Edit
     */
    public EditBox edit(String ID) {
        return this.convertControl(ID, EditBox.class, this.usedEdits);
    }

    /**
     * Get Edit by index.
     *
     * @param ID    Edit ID
     * @param index Edit index
     * @return Edit
     */
    public EditBox edit(String ID, int index) {
        List<EditBox> edits = this.edits(ID);
        assertTrue(
                edits.size() >= index,
                "Количество полей '" + edits.size()
                        + "' меньше, чем index '" + index + "'");
        return edits.get(index);
    }

    /**
     * Get Edit by ID.
     *
     * @param ID Edit ID
     * @return Edit
     */
    public List<EditBox> edits(String ID) {
        return this.convertControls(ID, EditBox.class, this.usedEdits);
    }

    /**
     * Get button.
     *
     * @param ID Button ID
     * @return button
     */
    public Button button(String ID) {
        return this.convertControl(ID, Button.class, this.usedButtons);
    }

    /**
     * Get button by index.
     *
     * @param ID    Button ID
     * @param index Button index
     * @return Button
     */
    public Button button(String ID, int index) {
        List<Button> buttons = this.buttons(ID);
        assertTrue(
                buttons.size() >= index,
                "Количество кнопок '" + buttons.size()
                        + "' меньше, чем index '" + index + "'");
        return buttons.get(index);
    }

    /**
     * Get buttons by ID.
     *
     * @param ID Button ID
     * @return Buttons
     */
    public List<Button> buttons(String ID) {
        return this.convertControls(ID, Button.class, this.usedButtons);
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
     *
     * @param ID Element ID
     * @return Element definition
     */
    public Map<String, Object> getElementDefinition(String ID) {
        assertTrue(this.elements.containsKey(ID), String.format("Элемент с ID '%s' не описан\n %s", ID, String.join(",", this.elements.keySet())));
        return this.elements.get(ID);
    }

    /**
     * Конвертирование элемента управления в нужный тип.
     * @param ID ID элемента
     * @param target Нужный тип элемента управления
     * @param cache Кеш элемента управления по типу
     * @param <C> Тип элемента
     * @return Сконвертированный элемент
     */
    private <C extends AutomationBase> C convertControl(String ID, Class<C> target, Map<String, List<C>> cache) {
        C result = this.getControlFromCache(ID, cache);
        if (result != null)
            return result;

        log.debug("Получение элемента с ID - '{}'...", ID);
        List<AutomationBase> found = this.getFound(ID);
        if(found.isEmpty()) {
            log.debug("Элементов по условиям поиска не найдено");
            return null;
        }

        log.debug("Элементы получены в количестве - '{}'", found.size());
        Map<String, Object> definition = this.getElementDefinition(ID);
        AutomationBase resultBase;

        if (definition.containsKey("label")) {
            resultBase = this.filterControlByLabel(found, ID);
        } else if (definition.containsKey("index")) {
            resultBase = this.filterControlByIndex(found, CheckerTools.castDefinition(definition.get("index")));
        } else {
            log.debug("Не найдено ключей для выборки. Выбор первого элемента");
            resultBase = found.get(0);
        }
        log.debug("Конвертирование элемента в '{}'", target.getSimpleName());
        result = this.convertElement(resultBase, target);
        log.debug("Элемент успешно конвертирован");
        log.debug("Запись в кеш");
        cache.put(ID, Collections.singletonList(result));
        log.debug("Элемент с ID '{}' успешно записан в кеш", ID);
        try {
            result.getElement().setFocus();
        } catch (Exception ex) {
            log.warn("Не удалось установить фокус для элемента управления c ID - {}", ID);
        }

        return result;
    }

    /**
     * Выборка элемента по надписи.
     *
     * @param found Найденные элементы управления
     * @param ID    ID элемента управления
     * @return Элемент управления
     */
    private AutomationBase filterControlByLabel(List<AutomationBase> found, String ID) {
        Map<String, Object> definition = this.getElementDefinition(ID);
        String label = CheckerTools.castDefinition(definition.get("label"));
        CheckerOCRLanguage language = assertDoesNotThrow(() -> (definition.containsKey("labelLag")
                        ? CheckerOCRLanguage.valueOf(CheckerTools.castDefinition(definition.get("labelLag")))
                        : CheckerOCRLanguage.RUS),
                "Не найден язык " + definition.get("labelLag") + " для распознавания надписи элемента");

        log.debug("Выборка элементов по маске надписи '{}'. Язык надписи '{}'", label, language.getValue());
        AutomationBase required;
        List<AutomationBase> selected = found
                .parallelStream()
                .filter(element -> {
                    Pattern pattern = Pattern.compile((label.startsWith("^") ? label.trim() : "^" + label.trim()));
                    return pattern.matcher(CheckerFieldsUtils.getLabel(element.getElement(), language).trim()).lookingAt();
                }).collect(Collectors.toList());
        if (selected.isEmpty()) {
            required = null;
            fail("Не удалось найти элемент с ID - '" + ID + "' по маске надписи '" + label + "'");
        } else if (selected.size() == 1) {
            log.debug("Найден единственный элемент с ID '{}' по маске надписи '{}'", ID, label);
            required = selected.get(0);
        } else if (definition.containsKey("index")) {
            required = this.filterControlByIndex(selected, CheckerTools.castDefinition(definition.get("index")));
        } else {
            log.debug("Найдено несколько элементов с ID '{}' по маске надписи '{}'. В описании не задан 'index'. Выбран первый элемент", ID, label);
            required = selected.get(0);
        }
        return required;
    }

    /**
     * Конвертирует элемент управления в нужный тип.
     *
     * @param base   Базовый элемент управления
     * @param target Нужный тип элемента
     * @param <C>    Тип элемента
     * @return Сконвертированный элемент
     */
    private <C extends AutomationBase> C convertElement(AutomationBase base, Class<C> target) {
        log.debug("Конвертирование в элемент управления('{}')", target.getSimpleName());
        C converted = assertDoesNotThrow(
                () -> target.getConstructor(ElementBuilder.class).newInstance(new ElementBuilder().element(base.getElement())),
                "Не удалось конвертировать элемент в '"
                        + target.getSimpleName() + "'");
        log.debug("Элемент управления успешно сконвертированно");
        return converted;
    }

    /**
     * Выборка элемента по индексу.
     *
     * @param found Коллекция элементов
     * @param index Индекс элемента
     * @return Элемент
     */
    private AutomationBase filterControlByIndex(List<AutomationBase> found, int index) {
        log.debug("Выборка элемента по его индексу");
        assertTrue(found.size() >= index, "Индекс '" + index + "' превышает количество найденных '" + found.size() + "'");
        AutomationBase selected = found.get(index);
        log.debug("Элемент с индексом '{}' найден", index);
        return selected;
    }

    /**
     * Получает элемент из кеша.
     *
     * @param ID    ID элемента
     * @param cache Кеш
     * @param <C>   Элемент управления
     * @return Элемент управления
     */
    private <C extends AutomationBase> C getControlFromCache(String ID, Map<String, List<C>> cache) {
        try {
            if (cache.containsKey(ID) && !cache.get(ID).isEmpty() && cache.get(ID).get(0).isEnabled()) {
                log.debug("Элемент c ID - {} активен. Извлечение из кеша", ID);
                return cache.get(ID).get(0);
            }
        } catch (Exception ex) {
            log.debug("Элемент c ID - {} не активен. Повторная попытка поиска", ID);
        }

        return null;
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
    private <C extends AutomationBase> List<C> convertControls(String ID, Class<C> target, Map<String, List<C>> output) {
        if(output.containsKey(ID)) {
            if(this.checkControlsFromCache(ID, output)) {
                log.debug("Элементы управления с ID '{}' активны. Извлечение из кеша...", ID);
                return output.get(ID);
            } else {
                log.debug("Один или несколько элементов управления в кеше не активны. Повторный поиск...");
            }
        }

        log.debug("Поиск элементов с ID '{}'...", ID);
        List<AutomationBase> found = this.getFound(ID);
        log.debug("Элементы с ID '{}' найдены в количестве '{}'", ID, found.size());

        log.debug("Конвертирование элемента управления в '{}'", target.getSimpleName());
        List<C> result = found
                .parallelStream()
                .map(f -> this.convertElement(f, target))
                .collect(Collectors.toList());
        log.debug("Элементы успешно сконвертированны");
        log.debug("Запись в кеш");
        output.put(ID, result);
        log.debug("Элемент с ID {} успешно записаны в кеш", ID);
        return result;
    }

    /**
     * Получает активность элементов в кеше.
     * @param ID ID Элемента управления
     * @param cache Кеш
     * @param <C> Тип элемента управления
     * @return Результат проверки
     */
    private <C extends AutomationBase> boolean checkControlsFromCache(String ID, Map<String, List<C>> cache) {
        return cache.get(ID).parallelStream().allMatch(element -> {
            try {
                return element.isEnabled();
            } catch (AutomationException e) {
                return false;
            }
        });
    }

    /**
     * Get found elements.
     *
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
            nodes.forEach(n -> {
                Map<String, Object> childDefinition;
                if (n.containsKey("path")) {
                    String app = CheckerDesktopTest.getApplication().getName();
                    String path = String.format("/Tests/%s/%s/%s", app, this.references.get(node), n.get("path"));
                    childDefinition = CheckerTools.convertYAMLToMap(path);
                } else {
                    childDefinition = CheckerTools.castDefinition(n.get(node.substring(0, node.length() - 1)));
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
        log.debug("Поиск компонента");
        AtomicReference<String> ID = new AtomicReference<>();
        AtomicReference<String> name = new AtomicReference<>();
        List<AutomationBase> found = this.findControl(this.root, definition, ID, name);
        if (!throwIfNotFound && found.isEmpty()) {
            log.debug("Компоненты не найдены. Исключения при проверке выключены");
            return false;
        }

        assertFalse(found.isEmpty(), String.format("Не найден ни один элемент управления с ID - '%s', именем - '%s' по условиям поиска", ID.get(), name.get()));
        log.debug("Компоненты '{}. {}' найдены в количестве: {}", ID.get(), name.get(), found.size());

        this.ID = ID.get();
        this.name = ID.get();

        if (found.size() > 0) {
            if (this.definition.containsKey("index")) {
                int index = CheckerTools.castDefinition(this.definition.get("index"));
                log.debug("В описании элемента найден индекс('index') '{}'", index);
                assertTrue(
                        index <= found.size() - 1,
                        String.format(
                                "Индекс компонента управления  с ID - '%s', именем - '%s' больше количества найденных",
                                ID.get(),
                                name.get()));
                this.control = CheckerTools.castDefinition(found.get(index));
            }

            if (found.size() > 1)
                log.debug("Найдено несколько элементов управления с ID - '{}', именем - '{}'. Выбран первый.", ID.get(), name.get());
            this.control = CheckerTools.castDefinition(found.get(0));
        } else {
            fail(String.format("Не найдено элементов управления для '%s. %s'", ID.get(), name.get()));
            return false;
        }

        log.debug("Элемент управления найден и присвоен текущей сущности");
        return true;
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
        log.debug("Чтение условий поиска элемента '{}. {}'", currentID, currentName);
        assertTrue(
                definition.containsKey("search"),
                String.format(
                        "Не задан ключ поиска компонента управления c ID - '%s', name - '%s'",
                        currentID,
                        currentName));
        Map<String, Object> search = CheckerTools.castDefinition(definition.get("search"));
        log.debug("Условия прочитаны");

        List<AutomationBase> result = new LinkedList();
        int limit = this.waitTimeout;

        log.debug("Поиск элемента '{}. {}'", currentID, currentName);
        while (result.isEmpty() && limit >= 0) {
            try {
                List<AutomationBase> temp = root.getChildren(true);
                log.debug("Найдено неотсортированных элементов - '{}'", temp.size());
                result = temp
                        .stream()
                        .filter(control -> this.isSearchingControl(control, search))
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                log.debug("Повторная попытка найти элемент '{}. {}'. Осталось - {}", currentID, currentName, limit);
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
                Object property = control.getElement().getPropertyValue(PropertyID.valueOf(entry.getKey()).getValue());
                if (property == null)
                    return false;

                return Objects.equals(
                        property.toString().trim(),
                        entry.getValue().toString().trim());
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
