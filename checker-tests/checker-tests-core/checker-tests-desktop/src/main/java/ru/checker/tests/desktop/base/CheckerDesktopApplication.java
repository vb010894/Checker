package ru.checker.tests.desktop.base;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.Application;
import org.junit.jupiter.api.Assertions;
import ru.checker.tests.base.application.CheckerApplication;
import ru.checker.tests.base.utils.CheckerTools;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checker desktop application.
 * @author vd.zinovev
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckerDesktopApplication extends CheckerApplication {

    /**
     * UI automation application.
     */
    static Application application;

    /**
     * Application process.
     * Need when application pre start activated.
     */
    static Process appProcess;

    /**
     * Application definition.
     */
    @Getter
    final Map<String, Object> definition;

    Map<String, CheckerDesktopWindow> windows = new HashMap<>();


    /**
     * Constructor.
     */
    public CheckerDesktopApplication(Map<String, Object> definition, String windowPath) {
        this.definition = definition;
        List<LinkedHashMap<String, Object>> windows = CheckerTools.castDefinition(definition.get("windows"));
        windows.parallelStream().forEach(window -> {
            assertTrue(window.containsKey("path"), "Не найден ключ местонахождения описания формы. Ключ - 'path'");
            CheckerDesktopWindow win =  assertDoesNotThrow(() ->
                    new CheckerDesktopWindow(CheckerTools.convertYAMLToMap(windowPath  + window.get("path"))),
                    "Не удалось создать экземпляр окна приложения");
            this.windows.put(win.getID(), win);
        });
    }

    /**
     * Getting application window.
     * @param id Window ID
     * @return Application window
     */
    public CheckerDesktopWindow getWindow(String id) {
        assertTrue(this.windows.containsKey(id), String.format("Форма с id - %s не найдена. Добавьте форму в описание приложения", id));
        CheckerDesktopWindow window = this.windows.get(id);
        window.findWindow();
        return window;
    }

    /**
     * Get UI Automation application.
     * @return UI Automation application
     */
    public static Application getApplication() {
        assertNotNull(application, "Приложение не инициализированно");
        return application;
    }

    /**
     * Get App name.
     * @return Application name
     */
    @Override
    public String getName() {
        return (String) this.definition.getOrDefault("name", "UNNAMED");
    }

    /**
     * Run application.
     */
    @Override
    public void run() {
        boolean isPreStart = (Boolean) this.definition.getOrDefault("preStart", false);
        Assertions.assertDoesNotThrow(() -> {
            String location;
            assertNotNull((location = (String) this.definition.get("location")), "Не заполнено местоположение приложения. Ключ - 'location'");
            if(isPreStart) {
                String[] arguments;
                arguments = (String[]) this.definition.getOrDefault("arguments", null);
                appProcess = Runtime.getRuntime().exec(
                        location,
                        arguments,
                        new File(location).getParentFile());

            }
            application = UIAutomation.getInstance().launchOrAttach(location);
            application.waitForInputIdle();
        });
    }

    /**
     * Close application.
     */
    @Override
    public void close() {
        Boolean isStayAlive = (Boolean) this.definition.getOrDefault("stayAlive", false);
        if(!isStayAlive) {
            if (appProcess != null)
                appProcess.destroyForcibly();
            else
                application.end();
        }
    }
}
