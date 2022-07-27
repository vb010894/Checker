package ru.checker.tests.desktop.base.robot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.mouse.AutomationMouse;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Checker control marker for desktop.
 *
 * @author vd.zinovev
 */
@SuppressWarnings("unused")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckerDesktopMarker {

    /**
     * Control rectangle.
     */
    Rectangle rectangle;

    /**
     * Constructor by rectangle.
     *
     * @param rectangle Rectangle.
     */
    public CheckerDesktopMarker(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * Constructor by control.
     *
     * @param control Target control
     */
    public CheckerDesktopMarker(AutomationBase control) {
        this.rectangle = assertDoesNotThrow(() -> control.getBoundingRectangle().toRectangle(), "Не удалось получить положение элемента");
    }

    /**
     * Draw.
     * The cursor circles the element.
     */
    public void draw() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < this.rectangle.width; i++) {
                AutomationMouse.getInstance().setLocation(rectangle.x + i, (int) rectangle.getMinY());
                Thread.sleep(2);
            }

            for (int i = 0; i < this.rectangle.height; i++) {
                AutomationMouse.getInstance().setLocation((int) rectangle.getMaxX(), rectangle.y + i);
                Thread.sleep(2);
            }

            for (int i = 0; i < this.rectangle.width; i++) {
                AutomationMouse.getInstance().setLocation(rectangle.x + rectangle.width - i, (int) rectangle.getMaxY());
                Thread.sleep(2);
            }

            for (int i = 0; i < this.rectangle.height; i++) {
                AutomationMouse.getInstance().setLocation((int) rectangle.getMinX(), rectangle.y + rectangle.height - i);
                Thread.sleep(2);
            }
        }, "Не удалось выделить элемент");
    }

}
