package ru.checker.tests.desktop.test.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.Application;
import org.junit.jupiter.api.Assertions;
import ru.checker.tests.base.application.CheckerApplication;
import ru.checker.tests.base.utils.CheckerTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checker Desktop application.
 * @author vd.zinovev
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckerDesktopApplication extends CheckerApplication {

    /**
     * UI automation application.
     */
    @Getter
    static Application application;

    /**
     * Application process.
     * Need when application pre start activated.
     */
    static Process appProcess;

    /**
     * App name.
     */
    final String name;

    /**
     * App definition.
     */
    @Getter
    final Map<String, Object> definition;

    /**
     * Window definition cache.
     */
    final Map<String, Map<String, Object>> windowDefinitions = new HashMap<>();

    /**
     * App constructor.
     * @param definition App definition
     */
    public CheckerDesktopApplication(Map<String, Object> definition) {
        this.definition = definition;
        assertTrue(definition.containsKey("name"), "Не задан ключ 'name' в конфигурации приложения");
        this.name = CheckerTools.castDefinition(definition.get("name"));
        this.addWindow();
    }

    /**
     * Add window definition.
     */
    private void addWindow() {
        if (this.definition.containsKey("windows")) {
            List<Map<String, Object>> nodes = CheckerTools.castDefinition(this.definition.get("windows"));
            nodes.parallelStream().forEach(n -> {
                Map<String, Object> childDefinition;
                if (n.containsKey("path")) {
                    String path = String.format("/Tests/%s/%s/%s", this.name, "Windows", n.get("path"));
                    childDefinition = CheckerTools.convertYAMLToMap(path);
                } else {
                    childDefinition = n;
                }
                assertTrue(
                        childDefinition.containsKey("id"),
                        "Не задан ID ля элемента. \n"
                                + childDefinition
                                .entrySet()
                                .stream()
                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                .collect(Collectors.joining(",", "{", "}")));
                String id = CheckerTools.castDefinition(childDefinition.get("id"));
                this.windowDefinitions.put(id, childDefinition);
            });
        }
    }

    /**
     * Get app window.
     * @param ID Window ID.
     * @return App window
     */
    public CheckerDesktopWindow window(String ID) {
        assertTrue(this.windowDefinitions.containsKey(ID), "Не найдено описание окна приложения с ID - " + ID);
        CheckerDesktopWindow window = new CheckerDesktopWindow(application, this.windowDefinitions.get(ID));
        window.findMySelf();
        return window;
    }

    /**
     * Get app window.
     * @param ID Window ID.
     * @return App window
     */
    public <W> W window(String ID, Class<W> target) {
        assertTrue(this.windowDefinitions.containsKey(ID), "Не найдено описание окна приложения с ID - " + ID);
        CheckerDesktopWindow window = new CheckerDesktopWindow(application, this.windowDefinitions.get(ID));
        window.findMySelf();
        window.getControl().focus();
        return assertDoesNotThrow(
                () -> target.getConstructor(CheckerDesktopWindow.class).newInstance(window),
                "Не удалось конвертировать окно в обертку - " + target.getSimpleName());
    }

    /**
     * Get app name.
     * @return App name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Run app.
     * If config file has key 'preStart' with value 'true',
     * app will start with help process futures and then UI Automation attach process
     * else UI automation start app directly.
     */
    @Override
    public void run() {
        boolean isPreStart = (Boolean) this.definition.getOrDefault("preStart", false);
        Assertions.assertDoesNotThrow(() -> {
            String location;
            assertNotNull((location = (String) this.definition.get("location")), "Не заполнено местоположение приложения. Ключ - 'location'");
            System.out.println("Местоположение - " + location);
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
     * Close app.
     * if the config file has key 'stayAlive' with value 'true',
     * app will not be close.
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

    /**
     * Wait app.
     */
    public void waitApp() {
        application.waitForInputIdle();
    }
}
