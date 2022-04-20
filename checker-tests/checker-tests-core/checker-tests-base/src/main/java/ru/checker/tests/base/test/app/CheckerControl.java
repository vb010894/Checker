package ru.checker.tests.base.test.app;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.checker.tests.base.utils.CheckerTools;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public abstract class CheckerControl<R, T> {

    /**
     * Control ID.
     */
    String ID;

    /**
     * Control name.
     */
    String name;

    /**
     * Current control.
     */
    @Setter
    T control;

    /**
     * Root control.
     */
    @Setter
    R root;

    /**
     * Control definition.
     */
    Map<String, Object> definition;

    /**
     * Control search.
     */
    Map<String, Object> search;

    /**
     * Control child element.
     */
    Map<String, Map<String, Object>> elements = new HashMap<>();

    @Setter
    long elementsCount = 0;

    /**
     * Constructor by definition path.
     * @param PATH Definition path
     */
    public CheckerControl(String PATH) {
        Map<String, Object> definition = CheckerTools.convertYAMLToMap(getDefinitionPath() + PATH);
        this.init(definition);
    }

    /**
     * Constructor by definition.
     * @param definition Control definition
     */
    public CheckerControl(Map<String, Object> definition) {
        this.init(definition);
    }

    /**
     * Find and create control.
     */
    protected abstract void createControl(R root);

    /**
     * Find and create control.
     * When control's searching return several.
     * @param index Control index
     */
    public abstract void createControl(R root, int index);

    /**
     * Find control children.
     * @param definition Control definition.
     * @return List of control
     */
    public abstract List<?> findChildren(Map<String, Object> definition);

    /**
     * Get definition path.
     * @return Definition path
     */
    protected abstract String getDefinitionPath();

    /**
     * Class init.
     * @param definition Control definition
     */
    private void init(Map<String, Object> definition) {
        this.definition = definition;

        assertTrue(this.definition.containsKey("id"), "Не задан ID окна. Ключ 'id'");
        this.ID = CheckerTools.castDefinition(this.definition.get("id"));

        this.name = CheckerTools.castDefinition(this.definition.getOrDefault("name", ""));
        this.search = CheckerTools.castDefinition(this.definition.getOrDefault("search", new HashMap<>()));
        this.createElementsDefinition();

    }

    /**
     * Create controls.
     */
    private void createElementsDefinition() {
        if(this.getDefinition().containsKey("elements")) {
            List<Map<String, Object>> widgets = CheckerTools.castDefinition(this.getDefinition().get("elements"));
            widgets.parallelStream().forEach(element -> {
                assertTrue(element.containsKey("element"));
                Map<String, Object> elem = CheckerTools.castDefinition(element.get("element"));
                assertTrue(elem.containsKey("id"), "Элемент должен содержать ключ 'id'");
                this.elements.put(CheckerTools.castDefinition(elem.get("id")), elem);
            });
        }
    }

    /**
     * Create child elements.
     */
    protected abstract void createElements();

    /**
     * Calculate child elements.
     */
    public abstract void calculate();

    /**
     * Name from control definition.
     * Key in definition - 'name'
     * @return Definition name
     */
    public String getDefinitionName() {
        return this.name;
    }

    /**
     * Get original control name from driver.
     * @return Original name
     */
    public abstract String getOriginalName();

    /**
     * Get out control name.
     * @return Control out name
     */
    public String getOutName() {
        return (!this.getDefinitionName().equals("")) ? this.getDefinitionName() : this.getOriginalName();
    }

    /**
     * Get element definition by ID.
     * @param ID Element ID
     * @return Element definition
     */
    public Map<String, Object> getElement(String ID) {
        assertTrue(this.elements.containsKey(ID), "Не найдено с ID - " + ID);
        return this.elements.get(ID);
    }

}
