package ru.checker.tests.desktop.test.app;

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
    public CheckerDesktopApplication(Map<String, Object> definition) {
        this.definition = definition;
        if(this.getDefinition().containsKey("windows")) {
            List<Map<String, Object>> forms = CheckerTools.castDefinition(this.getDefinition().get("windows"));
            forms.parallelStream().forEach(form -> {
                if(form.containsKey("path")) {
                    String path = CheckerTools.castDefinition(form.get("path"));
                    CheckerDesktopWindow frm = new CheckerDesktopWindow(path);
                    this.windows.put(frm.getID(), frm);
                } else if (this.windows.containsKey("window")) {
                    Map<String, Object> def = CheckerTools.castDefinition(form.get("window"));
                    CheckerDesktopWindow frm = new CheckerDesktopWindow(def);
                    this.windows.put(frm.getID(), frm);
                } else {
                    fail("В описании Forms должны содержаться ключи 'path' (для отдельного файла) или 'form' (для локального описания)");
                }
            });
        }
    }

    /**
     * Getting application window.
     * @param id Window ID
     * @return Application window
     */
    public CheckerDesktopWindow window(String id) {
        assertTrue(this.windows.containsKey(id), String.format("Форма с id - %s не найдена. Добавьте форму в описание приложения", id));
        CheckerDesktopWindow window = this.windows.get(id);
        window.createWindow(getApplication());
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
                ArrayList<String> args = CheckerTools.castDefinition(this.definition.getOrDefault("arguments", null));

                if(args != null) {
                    args.add(0, location);
                    arguments = new String[args.size()];
                    args.toArray(arguments);
                } else {
                    arguments = new String[] {location};
                }

                appProcess = new ProcessBuilder()
                        .directory(new File(location).getParentFile())
                        .command(arguments)
                        .start();

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

    public void waitApp() {
        application.waitForInputIdle();
    }
}
