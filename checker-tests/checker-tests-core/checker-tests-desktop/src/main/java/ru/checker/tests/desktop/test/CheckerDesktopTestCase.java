package ru.checker.tests.desktop.test;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import ru.checker.tests.base.test.CheckerTestCase;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.app.CheckerDesktopApplication;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Desktop test base.
 * @author vd.zinovev
 */
@Log4j2(topic = "TEST CASE")
public abstract class CheckerDesktopTestCase extends CheckerTestCase {

    /**
     * Test case definition.
     */
    @Getter
    static Map<String, Object> definition;

    /**
     * Test case constant.
     */
    @Getter
    static Map<String, Object> constants = new LinkedHashMap<>();

    /**
     * Test case name.
     */
    @Getter
    static String caseName;

    /**
     * Application's name under test.
     */
    @Getter
    static String applicationName;

    /**
     * Test case ID.
     */
    @Getter
    static String ID;

    /**
     * Test case name.
     */
    @Getter
    static String name;

    /**
     * Application under test.
     */
    @Getter
    private static CheckerDesktopApplication sApplication;

    /**
     * Prepare test case.

     * @param testCaseName Case name
     */
    public static void prepare(String testCaseName) {
        caseName = testCaseName;
        applicationName = System.getProperty("app");
        definition = CheckerTools.convertYAMLToMap(String.format("/Tests/%s/Cases/%s.yaml", applicationName, testCaseName));
        ID = assertDoesNotThrow(() -> (String) definition.get("id"), "Не удалось получить ID теста. Ключ - 'id'");
        name = assertDoesNotThrow(() -> (String) definition.get("name"), "Не удалось получить ID теста. Ключ - 'name'");
        Assumptions.assumeTrue(definition.containsKey("app"), "Не найдено описание приложения. Ключ - 'app'");
        constants = CheckerTools.castDefinition(definition.getOrDefault("constants", new LinkedHashMap<>()));
    }


    /**
     * Before test cases.
     * 1) Starting application.
     * 2) Saving application in memory.
     */
    @BeforeAll
    @SuppressWarnings("unchecked")
    public static void start() {
        sApplication = new CheckerDesktopApplication((Map<String, Object>) definition.get("app"));
        log.info(
                "Инициализация тестовых случаев '{}'.\nТестируемое приложение - '{}'",
                getID() + ". " + getName(),
                sApplication.getName());
        sApplication.run();
        log.info("Инициализация прошла успешно. Приложение запущено");
    }

    /**
     * After test cases.
     * Application doesn't close when
     * 1) stayAlive property was activated
     * 2) some problems with application process
     */
    @AfterAll
    public static void end() {
        log.info(
                "Завершение тестовых случаев - '{}'. Приложение закрыто",
                getID() + ". " + getName());
        sApplication.close();
    }

}
