package ru.checker.starter.listener;

import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.checker.reporter.junit.CheckerJunitReportGenerator;
import ru.checker.reporter.nunit.models.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class CheckerNGListener implements ITestListener {

    private static final String rootPath = new File("").getAbsolutePath();
    private static final String videoPath = rootPath + "/Reports/Video";
    private static final String imagePath = rootPath + "/Reports/Image";
    private static final String nUnitReportPath = rootPath + "/Reports/Nunit";
    private static final String testNGFiles = rootPath + "/Reports/TestNG";

    private static List<String> paths = List.of(
            videoPath,
            imagePath,
            nUnitReportPath,
            testNGFiles
    );

    private static final Map<String, LinkedList<String>> attachments = new LinkedHashMap<>();
    private static final NUnitTestRun result = new NUnitTestRun();
    private static final NUnitTestSuite suite = new NUnitTestSuite();
    private static final NUnitTestSuiteWrapper wrapper = new NUnitTestSuiteWrapper();

    private ScreenRecorder recorder;

    @Override
    public void onTestStart(ITestResult result) {
        this.startVideo(result.getTestName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), false);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), true);
        this.createScreenshot(result.id(), result.getTestName());
        System.out.println("##[error] " + result.getThrowable().getMessage());
        result.getThrowable().printStackTrace();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), false);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), false);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), false);
    }

    @Override
    public void onStart(ITestContext context) {
        paths.parallelStream().forEach(path -> {
            File file = new File(path);
            File[] files = file.listFiles();
            Stream.of(files).parallel().forEach(file1 -> {
                if(!file1.delete())
                    System.out.println("##[warning] Не удалось отчистить файл - " + file1.getAbsolutePath());
            });
        });
    }

    @Override
    public void onFinish(ITestContext context) {
        String caseName = context.getSuite().getName();
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(context.getEndDate());
        String total = String.valueOf(context.getSuite().getAllMethods().size());
        String passed = String.valueOf(context.getPassedTests().size());
        String warnings = "0";
        String failed = String.valueOf(context.getFailedTests().size());
        String inconclusive = String.valueOf(context.getFailedButWithinSuccessPercentageTests().size());
        String skipped = String.valueOf(context.getSkippedTests().size());
        String duration = String.valueOf(context.getEndDate().getTime() - context.getStartDate().getTime());


        result.setTotal(total);
        result.setPassed(passed);
        result.setFailed(failed);
        result.setWarnings(warnings);
        result.setInconclusive(inconclusive);
        result.setSkipped(skipped);
        result.setEndTime(endTime);
        result.setDuration(duration);
        result.setResult("Passed");

        wrapper.setTotal(total);
        wrapper.setName("Root case");
        wrapper.setFullname("Root case");
        wrapper.setName(caseName);
        wrapper.setPassed(passed);
        wrapper.setFailed(failed);
        wrapper.setWarnings(warnings);
        wrapper.setInconclusive(inconclusive);
        wrapper.setSkipped(skipped);
        wrapper.setEndTime(endTime);
        wrapper.setDuration(duration);
        wrapper.setResult("Passed");

        suite.setTotal(total);
        suite.setName(caseName);
        suite.setPassed(passed);
        suite.setFailed(failed);
        suite.setWarnings(warnings);
        suite.setInconclusive(inconclusive);
        suite.setSkipped(skipped);
        suite.setEndTime(endTime);
        suite.setDuration(duration);
        suite.setResult("Passed");

        suite.getCases().addAll(this.getPassed(context));
        suite.getCases().addAll(this.getFailed(context));
        suite.getCases().addAll(this.getSkipped(context));
        suite.getCases().addAll(this.getInconclusive(context));

        wrapper.setSuite(suite);
        result.setSuiteWrapper(wrapper);
        try {
            CheckerJunitReportGenerator.generateNUnitReport(result);
        } catch (IOException e) {
            new RuntimeException("Не удалось сохранить отчет", e);
        }
    }

    private void startVideo(String testName) {
        File videoPathFile = new File(videoPath);
        if (!videoPathFile.exists())
            if (!videoPathFile.mkdirs()) {
                System.out.println("##[warning] Не удалось создать папки для записи видео");
                return;
            }


        this.recorder = null;
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        try {
            this.recorder = new ScreenRecorder(
                    graphicsConfiguration,
                    graphicsConfiguration.getBounds(),
                    new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                    null,
                    videoPathFile);
            this.recorder.start();
        } catch (Exception ex) {
            System.out.printf("##[warning] Не записать видео для теста '%s'. Сообщение:\n%s\n",testName, ex.getMessage());
        }

    }

    private void createScreenshot(String testID, String testName) {
        File imageFile = new File(imagePath + "/" + testID + ".bmp");
        if(!imageFile.getParentFile().exists()) {
            if(!imageFile.getParentFile().mkdirs()) {
                System.out.printf("##[warning] Не удалось создать папку для сохранения изображений.\n");
                return;
            }
        }

        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, "bmp", imageFile);
            LinkedList<String> attachment = attachments.getOrDefault(testID, new LinkedList<>());
            attachment.add(imageFile.getAbsolutePath());
            attachments.put(testID, attachment);
        } catch (Exception ex) {
            System.out.printf("##[warning] Не удалось создать скриншот для теста '%s'. Сообщение:\n%s\n",testName, ex.getMessage());
        }
    }

    private void stopVideo(String testID, String testName, boolean needSave) {
        try {
            this.recorder.stop();
            LinkedList<String> attachment = attachments.getOrDefault(testID, new LinkedList<>());
            this.recorder.getCreatedMovieFiles().forEach(file -> {
                if (needSave) {
                    attachment.add(file.getAbsolutePath());
                } else {
                    if(!file.delete())
                        System.out.printf("##[warning] Не удалось удалить видео '%s'.\n", file.getAbsolutePath());
                }
            });
        } catch (Exception ex) {
            System.out.printf("##[warning] Не удалось остановить и сохранить видео для теста '%s'. Сообщение:\n%s\n",testName, ex.getMessage());
        }


    }

    private List<NUnitTestCase> getInconclusive(ITestContext context) {
        return context
                .getFailedButWithinSuccessPercentageTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);

                    test.setResult("Inconclusive");
                    return test;
                }).collect(Collectors.toList());
    }

    private List<NUnitTestCase> getSkipped(ITestContext context) {
        return context
                .getSkippedTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);
                    NUnitReason reason = new NUnitReason();
                    reason.setMessage("Тест пропущен. Сообщение - " + result.getThrowable().getMessage());
                    test.setReason(reason);
                    test.setResult("Skipped");
                    return test;
                }).collect(Collectors.toList());
    }

    private List<NUnitTestCase> getPassed(ITestContext context) {
        return context
                .getPassedTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);
                    test.setResult("Passed");
                    return test;
                }).collect(Collectors.toList());
    }

    private List<NUnitTestCase> getFailed(ITestContext context) {
        return context
                .getFailedTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);
                    test.setResult("Failed");
                    NUnitFailure failure = new NUnitFailure();
                    failure.setMessage(result.getThrowable().getMessage());
                    failure.setStackTrace(Stream.of(result.getThrowable().getStackTrace())
                            .filter(t -> t.getClassName().contains("ru.checker"))
                            .map(StackTraceElement::toString)
                            .collect(Collectors.joining("\n")));

                    test.setFailure(failure);

                    if (attachments.containsKey(test.getId())) {
                        List<NUnitAttachments> attachment = attachments
                                .get(test.getId())
                                .parallelStream()
                                .map(this::getAttachments)
                                .collect(Collectors.toList());
                        test.setAttachment(attachment);
                    }
                    return test;
                }).collect(Collectors.toList());
    }

    private NUnitAttachments getAttachments(String path) {
        NUnitAttachments attachments = new NUnitAttachments();
        attachments.setFilePath(path);
        return attachments;
    }

    private NUnitTestCase getFormattedCase(ITestResult resultContext) {
        NUnitTestCase test = new NUnitTestCase();
        test.setId(resultContext.id());
        test.setName(resultContext.getMethod().getDescription());
        test.setDuration(String.valueOf(resultContext.getEndMillis() - resultContext.getStartMillis()));
        test.setFullname(resultContext.getMethod().getQualifiedName());
        test.setAsserts("0");
        test.setMethodname(resultContext.getMethod().getQualifiedName());
        test.setClassname(resultContext.getTestClass().getName());

        return test;
    }
}
