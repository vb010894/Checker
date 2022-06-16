package ru.checker.starter.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public abstract class AbstractTestClass {


    @Parameters({"constants.environment"})
    @BeforeClass
    public static void init(String env) {
        System.out.println("Class " + env);
    }

    @AfterClass
    public static void end() {
        System.out.println("end");
    }
}
