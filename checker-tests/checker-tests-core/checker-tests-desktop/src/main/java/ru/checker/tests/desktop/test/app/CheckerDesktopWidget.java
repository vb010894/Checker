package ru.checker.tests.desktop.test.app;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.controls.AutomationBase;
import ru.checker.tests.base.test.app.CheckerWidget;

import java.util.Map;

@Log4j2(topic = "TEST CASE")
public class CheckerDesktopWidget extends CheckerDesktopControl<AutomationBase> implements CheckerWidget<AutomationBase> {

    /**
     * Constructor by definition.
     *
     * @param definition Control definition
     */
    public CheckerDesktopWidget(Map<String, Object> definition) {
        super(definition);
    }

    /**
     * Constructor by definition path.
     *
     * @param PATH Definition path
     */
    public CheckerDesktopWidget(String PATH) {
        super(PATH);
    }

    /**
     * Get original control name from driver.
     *
     * @return Original name
     */
    @Override
    public String getOriginalName() {
        try {
            return "Widget - " + this.getControl().getName();
        } catch (AutomationException e) {
            return "";
        }
    }

    /**
     * Calculate useful controls.
     */
    @Override
    public void calculate() {
        log.debug("Для виджета нет подсчета дочерних элементов");
    }

    /**
     * Get definition path.
     *
     * @return Definition path
     */
    @Override
    public String getDefinitionPath() {
        return CheckerWidget.super.getDefinitionPath();
    }



}
