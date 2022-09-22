package ru.checker.tests.base.test;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.checker.tests.base.application.CheckerApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Test case.
 * @author vd.zinovev
 * @since 1.0
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2(topic = "TEST CASE")
public abstract class CheckerTestCase {

    /**
     * Тестируемое приложение.
     */
    @Getter
    @Setter
    static CheckerApplication application;

    /**
     * Перед каждым тестовым случаем.
     */
    @BeforeEach
    public void beforeEach() {
        log.warn("Метод перед тестом не инициализирован");
    }

    /**
     * После каждого тестового случая.
     */
    @AfterEach
    public void afterEach() {
        log.warn("Метод после тестом не инициализирован");
    }
}
