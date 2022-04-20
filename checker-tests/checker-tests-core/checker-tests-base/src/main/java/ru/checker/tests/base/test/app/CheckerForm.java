package ru.checker.tests.base.test.app;

import java.util.List;

/**
 * Checker form.
 * @version vd.zinovev
 */
public interface CheckerForm<C> {


    /**
     * Get definition path.
     *
     * @return Definition path
     */
    default String getDefinitionPath() {
        return "/Tests/" + System.getProperty("app") + "/Forms/";
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

    /**
     * Form widget.
     * @param ID Widget ID
     * @return Widget
     */
    <T> T widget(String ID, Class<T> target);

}
