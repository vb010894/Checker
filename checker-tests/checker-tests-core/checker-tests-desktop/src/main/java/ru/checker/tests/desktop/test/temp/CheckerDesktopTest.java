package ru.checker.tests.desktop.test.temp;

import lombok.Getter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.checker.tests.base.test.CheckerTestCase;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.entity.CheckerDesktopApplication;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;

import java.util.Map;

public class CheckerDesktopTest extends CheckerTestCase {


    @Getter
    static CheckerDesktopApplication currentApp;

    @Parameters({"app"})
    @BeforeClass
    public void startApp(String appName) {
        System.out.println("Запуск приложения");
        Map<String, Object> definition = CheckerTools.convertYAMLToMap("/Tests/" + appName + "/application/config.yaml");
        currentApp = new CheckerDesktopApplication(definition);
        setApplication(currentApp);
        currentApp.run();
        currentApp.waitApp();
        System.out.println("Приложение запущено");
    }


    @AfterClass
    public void end() {
        System.out.println("Закрытие приложения");
        getApplication().close();
        System.out.println("Закрытие приложения");
    }

}
