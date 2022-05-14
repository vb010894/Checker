package ru.checker.tests.desktop.base.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import ru.checker.tests.base.utils.CheckerOCRUtils;

import java.awt.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckerFieldsUtils {

    public static AutomationBase filterFieldsByLabel(List<AutomationBase> elements, String label) {
        return elements.parallelStream().filter(el ->  {
            try {
                Element element = el.getElement();
                Rectangle el_rect = element.getBoundingRectangle().toRectangle();
                Rectangle rectangle = el_rect;
                Element parent = null;
                while (Math.abs(rectangle.height - el_rect.height) < 10) {
                    parent = UIAutomation.getInstance().getControlViewWalker().getParentElement(element);
                    rectangle = parent.getBoundingRectangle().toRectangle();
                    element = parent;
                }
                int width = Math.abs(rectangle.x -  el_rect.x) - 5;
                Rectangle labelPos = new Rectangle(rectangle.x, el_rect.y, width, el_rect.height);
                String[] labels = CheckerOCRUtils.getTextFromRectangle(labelPos).split("\\|");
                System.out.println(labels[labels.length - 1].trim());
                return labels[labels.length - 1].trim().contains(label);
            } catch (Exception ex) {
                return false;
            }
        }).findFirst().orElseThrow();
    }

}
