package ru.checker.tests.desktop.test;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
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
     * Application under test.
     */
    @Getter
    private static CheckerDesktopApplication sApplication;



    /**
     * Before test cases.
     * 1) Starting application.
     * 2) Saving application in memory.
     */
    @Parameters({"app"})
    @BeforeClass
    public static void start(String app) {
        System.out.println("Запуск приложения");
        definition = CheckerTools.convertYAMLToMap("/Tests/" + app + "/application/config.yaml");
        sApplication = new CheckerDesktopApplication(definition);
        sApplication.run();
        log.info("Приложение запущено");
    }

    /**
     * After test cases.
     * Application doesn't close when
     * 1) stayAlive property was activated
     * 2) some problems with application process
     */
    @AfterClass
    public static void end() {
        System.out.println("Закрытие приложения");
        sApplication.close();
        System.out.println("Успешно закрыто");
    }

}
