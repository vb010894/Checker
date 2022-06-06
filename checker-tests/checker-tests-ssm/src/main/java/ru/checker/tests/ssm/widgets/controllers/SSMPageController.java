package ru.checker.tests.ssm.widgets.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.AutomationException;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.mouse.AutomationMouse;
import net.sourceforge.tess4j.ITessAPI;
import ru.checker.tests.base.utils.CheckerOCRUtils;
import ru.checker.tests.desktop.test.app.CheckerDesktopApplication;
import ru.checker.tests.desktop.test.app.CheckerDesktopWidget;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SSMPageController {

    final CheckerDesktopWidget widget;
    final AutomationBase control;

    /**
     * Constructor.
     *
     * @param widget widget
     */
    public SSMPageController(CheckerDesktopWidget widget) {
        this.widget = widget;
        this.control = assertDoesNotThrow(() -> widget.getControl().getChildren(true).parallelStream().filter(control -> {
            try {
                return control.getClassName().equals("TcxPageControl");
            } catch (AutomationException e) {
                return false;
            }
        }).findFirst().orElseThrow(), "Не удалось получить элемент страниц");
    }

    public Rectangle getRectangle() {
        return assertDoesNotThrow(() -> this.control.getBoundingRectangle().toRectangle(), "Не удалось получить положение страниц");
    }

    public void selectTab(String locator) {
        Rectangle root = this.getRectangle();
        Rectangle place = new Rectangle(root.x, root.y, root.width, 20);
        CheckerOCRUtils.getTextAndMove(place, locator, ITessAPI.TessPageIteratorLevel.RIL_WORD);
        AutomationMouse.getInstance().leftClick();
    }

}
