package ru.checker.tests.base.test.app;

public interface CheckerWindow {

    /**
     * Get definition path.
     *
     * @return Definition path
     */
    default String getDefinitionPath() {
        return "/Tests/" + System.getProperty("app") + "/Windows/";
    }

    /**
     * Get window form.
     * @param ID Control ID.
     * @return Control
     */
    CheckerForm form(String ID);

    /**
     * Get window widget.
     * @param ID Control ID.
     * @return Control
     */
    <T> T widget(String ID, Class<T> controller);

}
