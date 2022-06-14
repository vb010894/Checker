package ru.checker.starter;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.testng.TestNG;
import ru.checker.starter.listener.CheckerTestListener;

import java.io.File;
import java.util.List;

@ComponentScan("ru.checker.tests")
@SpringBootApplication
@Log4j2(topic = "root")
public class CheckerStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckerStarterApplication.class, args);
        String root = new File("").getAbsolutePath();
        File out = new File(root + "/Reports/TestNG");
        if (!out.exists())
            if (!out.mkdirs()) {
                log.warn("Не удалось создать папку с отчетами NG");
            }

        String XML = root + "/Tests/SSM/Plans/Test.xml";
        TestNG test = new TestNG();
        test.setTestSuites(List.of(XML));
        test.setOutputDirectory(out.getAbsolutePath());
        test.run();
    }
}
