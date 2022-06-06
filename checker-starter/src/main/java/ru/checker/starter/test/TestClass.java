package ru.checker.starter.test;

import io.qameta.allure.Attachment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestClass {

    @BeforeClass
    public static void init() {
        System.out.println("start");
    }

    @Test
    public void test() {
        System.out.println("test");
    }

    @Test
    public void test2() {
        Assumptions.assumeFalse(true, "Не выполнено");
    }

    public void test3() {
        Assertions.fail("Ошибка");
    }

    @AfterClass
    public static void end() {
        System.out.println("end");
    }
}
