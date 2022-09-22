package ru.checker.tests.desktop.test.entity;

import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.Panel;

import java.util.Map;

/**
 * Checker desktop widget.
 *
 * @author vd.zinovev
 */
public class CheckerDesktopWidget extends CheckerBaseEntity<Panel, AutomationBase> {

    /**
     * Constructor.
     *
     * @param root       Root control
     * @param definition Control definition
     */
    public CheckerDesktopWidget(AutomationBase root, Map<String, Object> definition) {
        super(root, definition);
    }

    /**
     * Constructor.
     *
     * @param root       Root control
     * @param definition Control definition
     */
    public CheckerDesktopWidget(AutomationBase root, Panel control, Map<String, Object> definition) {
        super(root, definition);
        this.setControl(control);
    }
}
