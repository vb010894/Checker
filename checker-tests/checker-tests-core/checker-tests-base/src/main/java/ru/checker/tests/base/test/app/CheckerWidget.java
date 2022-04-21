package ru.checker.tests.base.test.app;

import java.util.List;

/**
 * Checker widget.
 * @author vd.zinovev
 */
@SuppressWarnings("unused")
public interface CheckerWidget<C> {


    /**
     * Get definition path.
     *
     * @return Definition path
     */
    default String getDefinitionPath() {
        return "/Tests/" + System.getProperty("app") + "/Widgets/";
    }

    /**
     * Get widget found elements.
     * @param ID Element ID
     * @return Element
     */
     List<C> element(String ID);

    /**
     * Get widget element by index.
     * @param ID Element ID
     * @return Element
     */
    C element(String ID, int index);

    /**
     * Get widget first found element.
     * @param ID Element ID
     * @return Element
     */
    C firstElement(String ID);

}
