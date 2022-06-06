package ru.checker.starter;

import lombok.extern.log4j.Log4j2;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.testng.IReporter;
import org.testng.Reporter;
import org.testng.TestNG;
import ru.checker.reporter.junit.CheckerJunitReportGenerator;
import ru.checker.starter.listener.CheckerNGListener;
import ru.checker.starter.listener.CheckerTestListener;
import ru.checker.starter.test.TestClass;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

@ComponentScan("ru.checker.tests")
@SpringBootApplication
@Log4j2(topic = "root")
public class CheckerStarterApplication implements CommandLineRunner {

    private static  ApplicationContext application;

    private final CheckerTestListener listener = new CheckerTestListener();

    @Autowired
    public CheckerStarterApplication(ApplicationContext context) {
        application = context;
    }

    public static void main(String[] args) {
        application = SpringApplication.run(CheckerStarterApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String root = new File("").getAbsolutePath();
        File out = new File(root + "/Reports/TestNG");
        if (!out.exists())
            if (!out.mkdirs()) {
                log.warn("Не удалось создать папку со скриншотами");
            }

        TestNG test = new TestNG();
        test.setTestClasses(new Class[] {TestClass.class});
        test.setListenerClasses(List.of(CheckerNGListener.class));
        test.setOutputDirectory(out.getAbsolutePath());
        test.run();
        /*if((boolean) System.getProperties().getOrDefault("localTest", false))
            return;
        String testCase = args[0];
        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequestBuilder request = LauncherDiscoveryRequestBuilder
                .request();
       if(System.getProperties().containsKey("test")) {
            request.selectors(selectMethod(application.getBean(testCase).getClass(), System.getProperty("test").trim()));
        } else {
            request.selectors(selectClass(application.getBean(testCase).getClass()));
        }
        TestPlan plan = launcher.discover(request.build());
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(plan);
        CheckerJunitReportGenerator.generateJunitReports(listener.getReports());*/
    }
}
