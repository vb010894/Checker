package ru.checker.tests.desktop.test.entity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.Window;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckerDesktopForm extends CheckerBaseEntity<Panel, Window> {

    Map<String, Map<String, Object>> widgetsDefinitions = new HashMap<>();
    /**
     * Constructor.
     *
     * @param root       Root control
     * @param definition Control definition
     */
    public CheckerDesktopForm(Window root, Map<String, Object> definition) {
        super(root, definition);
        this.addChildrenDefinition("widgets", widgetsDefinitions);
    }

    /**
     * Get window widget.
     * @param ID Widget ID
     * @return Widget
     */
    public <W> W widget(String ID, Class<W> wrapper) {
        assertTrue(widgetsDefinitions.containsKey(ID), String.format("Виджет с ID - %s не описана", ID));
        CheckerDesktopWidget widget = new CheckerDesktopWidget(this.getControl(), this.widgetsDefinitions.get(ID));
        widget.findMySelf();
        return assertDoesNotThrow(() -> wrapper.getConstructor(CheckerDesktopWidget.class).newInstance(widget), "Не удалось обернуть виджет с ID - " + ID);
    }


}
