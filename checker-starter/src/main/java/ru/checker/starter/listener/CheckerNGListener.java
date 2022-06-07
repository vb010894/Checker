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
    private static final NUnitTestRun result = new NUnitTestRun();

    @Getter
    private static final NUnitTestSuite suite = new NUnitTestSuite();

    @Getter
    private static final NUnitTestSuiteWrapper wrapper = new NUnitTestSuiteWrapper();

    private final NUnitTestCase test = new NUnitTestCase();

    private final List<NUnitTestCase> caseList = new LinkedList<>();

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println(result.getName() + " Запущен");
        NUnitTestCase test = new NUnitTestCase();
        test.setId(result.getTestName() + " " + new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(new Date()));
        test.setName(result.getName());
        test.setFullname(result.getTestName());
        test.setMethodname(result.getMethod().getMethodName());
        test.setClassname(result.getTestClass().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(result.getName() + " закончен успешно");
        this.test.setResult("Passed");
        caseList.add(this.test);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println(result.getName() + " закончен с ошибкой");
        this.test.setResult("Filed");

        NUnitFailure failure = new NUnitFailure();
        failure.setMessage(result.getThrowable().getMessage());
        failure.setStackTrace(Stream.of(result.getThrowable().getStackTrace())
                .filter(t -> t.getClassName().contains("ru.checker"))
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n")));
        test.setFailure(failure);

        String root = new File("").getAbsolutePath();
        List<NUnitAttachments> attachments = new LinkedList<>();
        NUnitAttachments attachment = new NUnitAttachments();
        attachment.setFilePath(root + "\\Reports\\Images\\dialog-windows.bmp");
        attachments.add(attachment);

        test.setAttachment(attachments);
        caseList.add(test);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println(result.getName() + " закончен пропущен");
        NUnitTestCase test = new NUnitTestCase();
        test.setDuration((result.getEndMillis() - result.getStartMillis()) / 1000 + "");
        test.setId(result.getTestName() + " " + new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(new Date()));
        test.setName(result.getName());
        test.setFullname(result.getTestName());
        test.setMethodname(result.getMethod().getMethodName());
        test.setClassname(result.getTestClass().getName());
        NUnitReason reason = new NUnitReason();
        reason.setMessage(result.getThrowable().getMessage());
        caseList.add(test);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println(result.getName() + " закончен частично");
        this.onTestFailure(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        System.out.println(result.getName() + " превысил лимит времени");
        System.out.println(result.getName() + " закончен с ошибкой");
        this.test.setResult("Filed");

        NUnitFailure failure = new NUnitFailure();
        failure.setMessage("Тест превысил лимит времени");
        test.setFailure(failure);

        String root = new File("").getAbsolutePath();
        List<NUnitAttachments> attachments = new LinkedList<>();
        NUnitAttachments attachment = new NUnitAttachments();
        attachment.setFilePath(root + "\\Reports\\Images\\dialog-windows.bmp");
        attachments.add(attachment);

        test.setAttachment(attachments);
        caseList.add(test);
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println(context.getName() + " начат");
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(context.getStartDate());
        result.setStartTime(startTime);
        result.setId("RUN " + context.getName() + " " + startTime);
        result.setTestcasecount("1");

        wrapper.setStartTime(startTime);
        wrapper.setId("SUITE WRAPPER " + context.getName() + " " + startTime);
        wrapper.setName(context.getSuite().getName());
        wrapper.setFullname(context.getSuite().getName());
        wrapper.setTestcasecount("1");

        suite.setStartTime(startTime);
        suite.setId("SUITE " + context.getName() + " " + startTime);
        suite.setName(context.getSuite().getName());
        suite.setFullname(context.getSuite().getName());
        suite.setTestcasecount("1");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println(context.getName() + " закончен");
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(context.getEndDate());
        String total = String.valueOf(context.getSuite().getAllInvokedMethods().size());

        result.setTotal(total);
        result.setPassed(String.valueOf(context.getPassedTests().size()));
        result.setFailed(String.valueOf(context.getFailedTests().size()));
        result.setWarnings(String.valueOf(context.getFailedButWithinSuccessPercentageTests()));
        result.setInconclusive(String.valueOf(context.getExcludedGroups().length));
        result.setSkipped(String.valueOf(context.getSkippedTests().size()));
        result.setEndTime(endTime);
        result.setDuration(String.valueOf((context.getEndDate().getTime() - context.getStartDate().getTime()) / 1000));
        result.setResult("Passed");

        wrapper.setTotal(total);
        wrapper.setPassed(String.valueOf(context.getPassedTests().size()));
        wrapper.setFailed(String.valueOf(context.getFailedTests().size()));
        wrapper.setWarnings(String.valueOf(context.getFailedButWithinSuccessPercentageTests()));
        wrapper.setInconclusive(String.valueOf(context.getExcludedGroups().length));
        wrapper.setSkipped(String.valueOf(context.getSkippedTests().size()));
        wrapper.setEndTime(endTime);
        wrapper.setDuration(String.valueOf((context.getEndDate().getTime() - context.getStartDate().getTime()) / 1000));
        wrapper.setResult("Passed");

        suite.setTotal(total);
        suite.setPassed(String.valueOf(context.getPassedTests().size()));
        suite.setFailed(String.valueOf(context.getFailedTests().size()));
        suite.setWarnings(String.valueOf(context.getFailedButWithinSuccessPercentageTests()));
        suite.setInconclusive(String.valueOf(context.getExcludedGroups().length));
        suite.setSkipped(String.valueOf(context.getSkippedTests().size()));
        suite.setEndTime(endTime);
        suite.setDuration(String.valueOf((context.getEndDate().getTime() - context.getStartDate().getTime()) / 1000));
        suite.setResult("Passed");

        suite.setCases(caseList);

        wrapper.setSuite(suite);
        result.setSuiteWrapper(wrapper);
        try {
            CheckerJunitReportGenerator.generateNUnitReport(result);
        } catch (IOException e) {
            new RuntimeException("Не удалось сохранить отчет", e);
        }
    }
}
