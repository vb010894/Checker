package ru.checker.tests.desktop.test.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.Application;
import mmarquee.automation.controls.Window;
import org.junit.jupiter.api.Assertions;
import ru.checker.tests.base.application.CheckerApplication;
import ru.checker.tests.base.utils.CheckerTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    final String name;
    @Getter
    final Map<String, Object> definition;

    final Map<String, Map<String, Object>> windowDefinitions = new HashMap<>();

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
                    Map<String, Object> definition = CheckerTools.convertYAMLToMap(String.format(path));
                    childDefinition = definition;
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

    public CheckerDesktopWindow window(String ID) {
        assertTrue(this.windowDefinitions.containsKey(ID), "Не найдено описание окна приложения с ID - " + ID);
        CheckerDesktopWindow window = new CheckerDesktopWindow(application, this.windowDefinitions.get(ID));
        window.findMySelf();
        return window;
    }

    @Override
    public String getName() {
        return this.name;
    }

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
