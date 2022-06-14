package ru.checker.starter.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.testng.SkipException;
import org.testng.annotations.*;

public class TestClass extends AbstractTestClass {


    @BeforeMethod()
    public void beforeEach() {
        System.out.println("beforeEach");
    }

    @Test(testName = "Тест Успешно")
    public void test() {
        System.out.println("app");
    }

    @Test(testName = "Тест Пропущено")
    public void test2() {
        Assumptions.assumeFalse(true, "Не выполнено");
    }

    @Test(testName = "Тест Ошибка")
    public void test3() {
        Assertions.fail("Ошибка");
    }

    @Test(testName = "Тест Пропущено программно", description = "descr")
    public void test4() {
        System.out.println("test");
        throw new SkipException("Пропуск");
    }


}
