package ru.checker.tests.desktop.test.entity;

import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.Panel;

import java.util.Map;

public class CheckerDesktopWidget extends CheckerBaseEntity <Panel, AutomationBase>{
    /**
     * Constructor.
     *
     * @param root       Root control
     * @param definition Control definition
     */
    public CheckerDesktopWidget(AutomationBase root, Map<String, Object> definition) {
        super(root, definition);
    }
}
