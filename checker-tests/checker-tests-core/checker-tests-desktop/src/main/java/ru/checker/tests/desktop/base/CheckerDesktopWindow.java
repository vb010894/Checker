package ru.checker.tests.desktop.base;

import lombok.Data;
import mmarquee.automation.controls.Application;
import mmarquee.automation.controls.Window;

public class CheckerDesktopWindow {

    private Window window;

    public CheckerDesktopWindow(Application application, String pathToDefinition) {

    }


    @Data
    public static class CheckerWindowDefinition {
        private String title;
        private String className;
    }

}
