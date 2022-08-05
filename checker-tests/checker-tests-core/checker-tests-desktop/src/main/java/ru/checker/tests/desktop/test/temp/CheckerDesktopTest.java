package ru.checker.tests.desktop.test.temp;

import lombok.Getter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import ru.checker.tests.base.test.CheckerTestCase;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopApplication;

import java.util.Map;

/**
 * Checker desktop test.
 *
 * @author vd.zinovev
 */
public class CheckerDesktopTest extends CheckerTestCase {

    /**
     * Current app.
     */
    @Getter
    static CheckerDesktopApplication currentApp;

    /**
     * Start app method.
     *
     * @param appName App name parameter
     *                in testNG config file
     *                need to add parameter 'app'
     */
    @Parameters({"app"})
    @BeforeClass
    public void startApp(String appName) {
        System.out.println("Запуск приложения");
        System.out.println(CheckerTools.getRootPath() + "/Tests/" + appName + "/application/config.yaml");
        Map<String, Object> definition = CheckerTools.convertYAMLToMap("/Tests/" + appName + "/application/config.yaml");
        currentApp = new CheckerDesktopApplication(definition);
        setApplication(currentApp);
        currentApp.run();
        currentApp.waitApp();
        System.out.println("Приложение запущено");
    }

    /**
     * Close app.
     */
    @AfterClass
    public void end() {
        System.out.println("Закрытие приложения");
        getApplication().close();
        System.out.println("Закрытие приложения");
    }
}