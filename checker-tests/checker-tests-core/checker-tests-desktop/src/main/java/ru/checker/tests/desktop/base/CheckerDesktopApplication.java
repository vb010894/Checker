package ru.checker.tests.desktop.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.Application;
import org.junit.jupiter.api.Assertions;
import ru.checker.tests.base.application.CheckerApplication;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checker desktop application.
 * @author vd.zinovev
 */
@EqualsAndHashCode(callSuper = true)
@Data
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

    /**
     * Constructor.
     */
    public CheckerDesktopApplication(Map<String, Object> definition) {
        this.definition = definition;
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
