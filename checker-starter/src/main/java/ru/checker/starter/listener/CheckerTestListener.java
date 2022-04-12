package ru.checker.starter.listener;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import ru.checker.reporter.junit.models.ErrorModel;
import ru.checker.reporter.junit.models.JunitReportModel;
import ru.checker.reporter.junit.models.SkippedModel;
import ru.checker.reporter.junit.models.TestCaseModel;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class CheckerTestListener implements TestExecutionListener {

    @Getter
    private final List<JunitReportModel> reports = new LinkedList<>();
    private final List<TestCaseModel> cases = new LinkedList<>();

    private JunitReportModel.JunitReportModelBuilder model;
    private TestCaseModel.TestCaseModelBuilder test;
    private TestPlan plan;
    private long testStartTime;
    private long startCaseTime = System.currentTimeMillis();
    private long testCount = 0;
    private long skippedCount = 0;
    private long failureCount = 0;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.plan = testPlan;
        log.info("Старт тестов");
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        log.info("Завершение тестов");
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        log.info("Старт динамических тестов");
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        SkippedModel skippedModel = SkippedModel
                .builder()
                .message(reason)
                .build();
        this.skippedCount++;
        this.test.skipped(skippedModel);
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isContainer()) {
            this.startCaseTime = 0;
            this.model = JunitReportModel.builder();
            this.model.name(testIdentifier.getLegacyReportingName());
        }
        if(testIdentifier.isTest()) {
            this.test = TestCaseModel.builder();
            this.testStartTime = System.currentTimeMillis();
            this.test.name(testIdentifier.getDisplayName());
            this.testCount++;
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if(testIdentifier.isContainer()) {
            this.model.tests(String.valueOf(this.testCount));
            this.model.failures("0");
            this.model.skipped(String.valueOf(this.skippedCount));
            this.model.errors(String.valueOf(this.failureCount));
            this.model.time(String.valueOf(System.currentTimeMillis() - this.startCaseTime));
            this.model.testcase(this.cases);
            this.reports.add(this.model.build());
        }
        if(testIdentifier.isTest()) {
            switch (testExecutionResult.getStatus()) {
                case FAILED:
                        ErrorModel errorModel = ErrorModel
                                .builder()
                                .type(testExecutionResult.getThrowable().orElseThrow().getClass().getName())
                                .data(testExecutionResult.getThrowable().orElseThrow().getMessage())
                                .message(testExecutionResult.getThrowable().orElseThrow().getMessage())
                                .build();
                        this.test.error(errorModel);
                        this.test.stack(Stream.of(testExecutionResult.getThrowable().orElseThrow().getStackTrace()).map(String::valueOf).collect(Collectors.joining("\n")));
                        failureCount++;
                    break;
                case ABORTED:
                    SkippedModel skippedModel = SkippedModel
                            .builder()
                            .message(testExecutionResult.getThrowable().orElseThrow().getMessage())
                            .build();
                    this.test.skipped(skippedModel);
                    skippedCount++;
                    break;
            }
            String type = testIdentifier
                    .getUniqueId()
                    .split("/")[1]
                    .replace("[", "")
                    .replace("]", "")
                    .replace("class:", "");
            this.test.className(type);
            this.test.time(String.valueOf(System.currentTimeMillis() - this.testStartTime));
            this.cases.add(this.test.build());
        }

    }

}
