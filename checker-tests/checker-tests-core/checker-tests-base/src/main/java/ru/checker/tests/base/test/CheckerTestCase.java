package ru.checker.tests.base.test;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.checker.tests.base.application.CheckerApplication;

/**
 * Test case.
 * @author vd.zinovev
 * @since 1.0
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2(topic = "TEST CASE")
public abstract class CheckerTestCase {

    @Getter
    @Setter
    static CheckerApplication application;

    @BeforeEach
    public void beforeEach() {
        log.warn("Метод перед тестом не инициализирован");
    }

    @AfterEach
    public void afterEach() {
        log.warn("Метод после тестом не инициализирован");
    }

}
