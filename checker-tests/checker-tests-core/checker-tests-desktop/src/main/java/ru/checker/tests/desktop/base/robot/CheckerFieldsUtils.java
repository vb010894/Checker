package ru.checker.tests.desktop.base.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Panel;
import ru.checker.tests.base.utils.CheckerOCRUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Utils for working with controls.
 *
 * @author vd.zinovev
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public final class CheckerFieldsUtils {

    /**
     * Get automation control by label.
     *
     * @param elements List of elements
     * @param label    Searching label
     * @return Automation control
     */
    public static <T extends AutomationBase> T filterFieldsByLabel(List<T> elements, String label) {
        return elements.parallelStream().filter(el -> {
            try {
                Element element = el.getElement();
                Rectangle el_rect = element.getBoundingRectangle().toRectangle();
                Rectangle rectangle = el_rect;
                Element parent;
                while (Math.abs(rectangle.height - el_rect.height) < 10) {
                    parent = UIAutomation.getInstance().getControlViewWalker().getParentElement(element);
                    rectangle = parent.getBoundingRectangle().toRectangle();
                    element = parent;
                }
                int width = Math.abs(rectangle.x - el_rect.x) - 5;
                Rectangle labelPos = new Rectangle(rectangle.x, el_rect.y, width, el_rect.height);
                String[] labels = CheckerOCRUtils.getTextFromRectangle(labelPos).split("\\|");
                System.out.println(labels[labels.length - 1].trim());
                return labels[labels.length - 1].trim().contains(label);
            } catch (Exception ex) {
                return false;
            }
        }).findFirst().orElseThrow();
    }

    private static List<Rectangle> getXNeighbor(AutomationBase parent, Rectangle element, int elementX) {
        List<Rectangle> result = new LinkedList<>();
        try {
            parent.getChildren(true).parallelStream().filter(child -> {
                try {
                    Rectangle childRect = child.getBoundingRectangle().toRectangle();
                    return childRect.contains(child.getBoundingRectangle().toRectangle().x, element.y) && childRect.x < elementX && !child.getElement().getBoundingRectangle().toRectangle().equals(element);
                } catch (AutomationException e) {
                    return false;
                }
            }).findAny().ifPresent(child -> {
                try {
                    result.add(child.getBoundingRectangle().toRectangle());
                } catch (AutomationException e) {
                    log.warn("Не удалось получить местоположения соседнего элемента");
                }
            });
            return result;
        } catch (AutomationException e) {
           return new LinkedList<>();
        }
    }

    private static Rectangle getLabelRectangle(Element element) throws AutomationException {
        Rectangle el_rect = element.getBoundingRectangle().toRectangle();
        Rectangle rectangle = el_rect;
        Element parent = null;
        while (Math.abs(rectangle.height - el_rect.height) < 10) {
            parent = UIAutomation.getInstance().getControlViewWalker().getParentElement(element);
            rectangle = parent.getBoundingRectangle().toRectangle();
            element = parent;
        }

        if(parent != null) {
            Panel parentPanel = new Panel(new ElementBuilder().element(parent));
            List<Rectangle> neighbors = getXNeighbor(parentPanel, element.getBoundingRectangle().toRectangle(), el_rect.x);
            Rectangle target;
            if(neighbors.isEmpty()) {
                Rectangle parentRect = parent.getBoundingRectangle().toRectangle();
                return new Rectangle(parentRect.x + 2, (int) el_rect.getY(), (int) Math.abs(el_rect.getMinX() - parentRect.getX() - 2), (int) el_rect.getHeight());
            }
            Rectangle neighborRect = neighbors.parallelStream().max(Comparator.comparing(Rectangle::getX)).get();
            return new Rectangle(neighborRect.x + neighborRect.width + 2, (int) el_rect.getY(), (int) Math.abs(el_rect.getMinX() - neighborRect.getMaxX() - 2), (int) el_rect.getHeight());
        } else {
            return null;
        }
    }

    public static String getLabel(Element element) {
        String result;
        try {
            Rectangle rectangle = getLabelRectangle(element);
            if(rectangle == null)
                result = "";
            else
                result = CheckerOCRUtils.getTextFromRectangle(rectangle);
        } catch (AutomationException e) {
            result = "";
        }
        log.info("Найдена надпись поля - " + result);
        return result;
    }
}