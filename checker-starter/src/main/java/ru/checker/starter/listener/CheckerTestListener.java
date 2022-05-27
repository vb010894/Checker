package ru.checker.starter.listener;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import ru.checker.reporter.junit.models.ErrorModel;
import ru.checker.reporter.junit.models.JunitReportModel;
import ru.checker.reporter.junit.models.SkippedModel;
import ru.checker.reporter.junit.models.TestCaseModel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

@Log4j2
@SuppressWarnings("unused")
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
    private String logStorage = "";

    private ScreenRecorder recorder;

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

            System.setOut(new PrintStream(System.out) {
                public void println(String s) {
                    logStorage += s + "\n";
                    super.println(s);
                }
            });
            this.test = TestCaseModel.builder();
            this.testStartTime = System.currentTimeMillis();
            this.test.name(testIdentifier.getDisplayName());
            this.testCount++;
            String root = new File("").getAbsolutePath();
            String path = root.substring(0, root.indexOf("Checker")) + "/Checker/Reports/Video" + testIdentifier.getDisplayName();
            File pathFile = new File(path);
            if(!pathFile.exists()) {
                if (!pathFile.mkdirs())
                    log.warn("Не удалось создать папку для записи видео");
            } else {
                Arrays.stream(Objects.requireNonNull(pathFile.listFiles())).forEach(file -> {
                    if(!file.delete())
                        log.warn("Не удалось удалить видео - " + file.getAbsolutePath());
                });
            }
            GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            try {
                this.recorder = new ScreenRecorder(
                        graphicsConfiguration,
                        graphicsConfiguration.getBounds(),
                        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                                DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                                QualityKey, 1.0f,
                                KeyFrameIntervalKey, 15 * 60),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                        null,
                        pathFile);
                this.recorder.start();
            } catch (Exception e) {
                log.warn("Не удалось записать видео.\nСообщение:\n" + e.getMessage());
            }
        }

    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if(testIdentifier.isContainer()) {
            if(testExecutionResult.getStatus().equals(TestExecutionResult.Status.FAILED)) {
                System.out.println(testExecutionResult.getThrowable().orElseThrow().getMessage());
                testExecutionResult.getThrowable().orElseThrow().printStackTrace();
            }
            this.model.tests(String.valueOf(this.testCount));
            this.model.failures("0");
            this.model.skipped(String.valueOf(this.skippedCount));
            this.model.errors(String.valueOf(this.failureCount));
            double seconds = (System.currentTimeMillis() - this.startCaseTime) / 1000.0;
            this.model.time(String.format("%.3f", seconds).replace(",", "."));
            this.model.testcase(this.cases);
            this.reports.add(this.model.build());
        }
        if(testIdentifier.isTest()) {
            switch (testExecutionResult.getStatus()) {
                case FAILED:
                    log.error(testExecutionResult.getThrowable().orElseThrow().getMessage(), testExecutionResult.getThrowable().orElseThrow());
                        ErrorModel errorModel = ErrorModel
                                .builder()
                                .type(testExecutionResult.getThrowable().orElseThrow().getClass().getName())
                                .data(testExecutionResult.getThrowable().orElseThrow().getMessage())
                                .message(testExecutionResult.getThrowable().orElseThrow().getMessage())
                                .build();
                        this.test.error(errorModel);
                        this.test.stack(Stream.of(testExecutionResult.getThrowable().orElseThrow().getStackTrace()).filter(stack -> stack.getClassName().contains("ru.checker")).map(String::valueOf).collect(Collectors.joining("\n")));
                        this.test.stackTrace(Stream.of(testExecutionResult.getThrowable().orElseThrow().getStackTrace()).filter(stack -> stack.getClassName().contains("ru.checker")).map(String::valueOf).collect(Collectors.joining("\n")));
                        failureCount++;
                    break;
                case ABORTED:
                    log.error(testExecutionResult.getThrowable().orElseThrow().getMessage(), testExecutionResult.getThrowable().orElseThrow());
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
            double seconds = (System.currentTimeMillis() - this.testStartTime) / 1000;
            this.test.time(String.format("%.3f", seconds).replace(",", "."));
            try {
                this.recorder.stop();
                log.info("Видео успешно записано");
                String root = new File("").getAbsolutePath();
                String path = root.substring(0, root.indexOf("Checker")) + "/Checker/Reports/Video/" + testIdentifier.getDisplayName();
                File pathFile = new File(path);
                if(pathFile.listFiles() != null) {
                    Arrays.stream(Objects.requireNonNull(pathFile.listFiles()))
                            .parallel()
                            .forEach(file -> System.out.println(this.logStorage += String.format("[[ATTACHMENT|%s]]\n", file.getAbsolutePath())));
                }
            } catch (IOException e) {
                log.warn("Не удалось остановить запись");
            }
            this.test.out(logStorage);
            this.cases.add(this.test.build());
            logStorage = "";
        }

    }

}
