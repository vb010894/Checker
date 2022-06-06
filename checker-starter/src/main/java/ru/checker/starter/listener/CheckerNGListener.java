package ru.checker.starter.listener;

import lombok.Getter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.checker.reporter.junit.CheckerJunitReportGenerator;
import ru.checker.reporter.nunit.models.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckerNGListener implements ITestListener {

    @Getter
    private static final NUnitTestResults result = new NUnitTestResults();
    @Getter
    private static final NUnitTestSuite suite = new NUnitTestSuite();

    private List<NUnitTestCase> caseList = new LinkedList<>();


    private static String getMethodName(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getName();
    }


    @Override
    public void onTestStart(ITestResult result) {
        System.out.println(result.getName() + " Запущен");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(result.getName() + " закончен успешно");
        NUnitTestCase test = new NUnitTestCase();
        test.setExecuted("True");
        test.setSuccess("True");
        test.setTime((result.getEndMillis() - result.getStartMillis()) / 1000 + ".000");
        caseList.add(test);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println(result.getName() + " закончен с ошибкой");

        NUnitTestCase test = new NUnitTestCase();
        test.setExecuted("True");
        test.setSuccess("False");
        test.setTime((result.getEndMillis() - result.getStartMillis()) / 1000 + ".000");
        NUnitFailure failure = new NUnitFailure();
        failure.setMessage(result.getThrowable().getMessage());
        failure.setStackTrace(Stream.of(result.getThrowable().getStackTrace())
                .filter(t -> t.getClassName().contains("ru.checker"))
                .map(t -> t .toString())
                .collect(Collectors.joining("\n")));
        test.setFailure(failure);
        String root = new File("").getAbsolutePath();
        List<String> attachment = new LinkedList<>();
        attachment.add(root + "\\Reports\\Images\\dialog-windows.bmp");
        test.setAttachment(attachment);
        caseList.add(test);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println(result.getName() + " закончен пропущен");
        NUnitTestCase test = new NUnitTestCase();
        test.setExecuted("False");
        NUnitReason reason = new NUnitReason();
        reason.setMessage(result.getThrowable().getMessage());
        caseList.add(test);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println(result.getName() + " закончен частично");
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        System.out.println(result.getName() + " превысил лимит времени");
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println(context.getName() + " начат");
        result.setDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        result.setDate(new SimpleDateFormat("HH:mm").format(new Date()));
        result.setTotal(String.valueOf(context.getExcludedMethods().size()));
        result.setName("Test.jar");
        suite.setName(context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println(context.getName() + " закончен");
        result.setFailures(String.valueOf(context.getFailedTests().getAllMethods().size()));
        result.setNotRun(String.valueOf(context.getSkippedTests().getAllMethods().size()));
        suite.setCases(caseList);
        result.setSuites(List.of(suite));
        try {
            CheckerJunitReportGenerator.generateNUnitReport(result);
        } catch (IOException e) {
            new RuntimeException("Не удалось сохранить отчет");
        }
    }
}
