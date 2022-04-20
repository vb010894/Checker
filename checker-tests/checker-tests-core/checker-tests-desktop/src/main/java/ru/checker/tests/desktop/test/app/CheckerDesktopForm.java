package ru.checker.tests.desktop.test.app;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.Panel;
import ru.checker.tests.base.test.app.CheckerForm;
import ru.checker.tests.base.test.app.CheckerWidget;
import ru.checker.tests.base.utils.CheckerTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2(topic = "TEST CASE")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class CheckerDesktopForm extends CheckerDesktopControl<Panel> implements CheckerForm<AutomationBase> {

    /**
     * Form widgets.
     */
    final Map<String, CheckerDesktopWidget> widgets = new HashMap<>();

    /**
     * Constructor by definition path.
     *
     * @param PATH Definition path
     */
    public CheckerDesktopForm(String PATH) {
        super(PATH);
        this.init();
    }

    /**
     * Constructor by definition.
     *
     * @param definition Control definition
     */
    public CheckerDesktopForm(Map<String, Object> definition) {
        super(definition);
        this.init();
    }

    /**
     * Init method.
     */
    private void init() {
        if(this.getDefinition().containsKey("widgets")) {
            List<Map<String, Object>> widgets = CheckerTools.castDefinition(this.getDefinition().get("widgets"));
            widgets.parallelStream().forEach(widget -> {
                if(widget.containsKey("path")) {
                    String path = CheckerTools.castDefinition(widget.get("path"));
                    CheckerDesktopWidget wid = new CheckerDesktopWidget(path);
                    this.widgets.put(wid.getID(), wid);
                } else if (this.widgets.containsKey("widget")) {
                    Map<String, Object> definition = CheckerTools.castDefinition(widget.get("widget"));
                    CheckerDesktopWidget wid = new CheckerDesktopWidget(definition);
                    this.widgets.put(wid.getID(), wid);
                } else {
                    fail("В описании widget должны содержаться ключи 'path' (для отдельного файла) или 'widget' (для локального описания)");
                }
            });
        }
     }

    /**
     * Get original control name from driver.
     *
     * @return Original name
     */
    @Override
    public String getOriginalName() {
        try {
            return "Form - " + this.getControl().getName();
        } catch (AutomationException e) {
            return "";
        }
    }

    /**
     * Get definition path.
     *
     * @return Definition path
     */
    @Override
    public String getDefinitionPath() {
        return CheckerForm.super.getDefinitionPath();
    }

    /**
     * Form widget.
     *
     * @param ID Widget ID
     * @return Widget
     */
    @Override
    public <T> T widget(String ID, Class<T> controller) {
        assertTrue(this.widgets.containsKey(ID), "Не найдено виджета с ID - " + ID);
        CheckerDesktopWidget widget = this.widgets.get(ID);
        widget.createControl(this.getControl());
        return assertDoesNotThrow(() ->
            controller.getConstructor(CheckerDesktopWidget.class).newInstance(widget),
                "Не удалось создать экземпляр контроллера");
    }
}
