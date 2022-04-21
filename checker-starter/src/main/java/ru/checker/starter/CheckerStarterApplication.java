package ru.checker.starter;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.checker.reporter.junit.CheckerJunitReportGenerator;
import ru.checker.starter.listener.CheckerTestListener;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

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
        String testCase = args[0];
        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(selectClass(application.getBean(testCase).getClass())).build();
        TestPlan plan = launcher.discover(request);
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(plan);
        CheckerJunitReportGenerator.generateJunitReports(listener.getReports());
    }
}
