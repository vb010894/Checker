package ru.checker.tests.base.test;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test case.
 * @author vd.zinovev
 * @since 1.0
 */
@Data
@Log4j2(topic = "Base test case")
public abstract class CheckerTestCase {

    @BeforeEach
    public void beforeEach() {
        log.warn("Метод перед тестом не инициализирован");
    }

    @AfterEach
    public void afterEach() {
        log.warn("Метод после тестом не инициализирован");
    }

}
