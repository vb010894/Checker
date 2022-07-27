package ru.checker.starter.listener;

import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.checker.reporter.CheckerJunitReportGenerator;
import ru.checker.reporter.nunit.models.*;
import ru.checker.reporter.nunit.models.enums.NunitResultStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

/**
 * Listener for AZURE.
 *
 * @author vd.zinovev
 */
public class CheckerTFSListener implements ITestListener {

    /**
     * Root path.
     */
    private static final String rootPath = new File("").getAbsolutePath();

    /**
     * Video path.
     */
    private static final String videoPath = rootPath + "/Reports/Video";

    /**
     * Image path.
     */
    private static final String imagePath = rootPath + "/Reports/Image";

    /**
     * Report path.
     */
    private static final String nUnitReportPath = rootPath + "/Reports/Nunit";

    /**
     * NG report path.
     */
    private static final String testNGFiles = rootPath + "/Reports/TestNG";

    /**
     * Service paths.
     */
    private final static List<String> paths = List.of(
            videoPath,
            imagePath,
            nUnitReportPath,
            testNGFiles
    );

    /**
     * Test case attachment.
     */
    private static final Map<String, LinkedList<String>> attachments = new LinkedHashMap<>();

    /**
     * Test run.
     */
    private static final NUnitTestRun result = new NUnitTestRun();

    /**
     * Test suite.
     */
    private static final NUnitTestSuite suite = new NUnitTestSuite();

    /**
     * Test suite wrapper.
     */
    private static final NUnitTestSuiteWrapper wrapper = new NUnitTestSuiteWrapper();

    /**
     * Test video recorder.
     */
    private ScreenRecorder recorder;

    /**
     * Fire on test case was started.
     *
     * @param result Test result.
     */
    @Override
    public void onTestStart(ITestResult result) {
        this.startVideo(result.getTestName());
    }

    /**
     * Fire on test case finished with status 'Passed'.
     *
     * @param result Test result.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), true);
    }

    /**
     * Fire on test case finished with status 'Failed'.
     *
     * @param result Test result.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), true);
        this.createScreenshot(result.id(), result.getTestName());
        System.out.println("##vso[task.logissue type=error;] " + result.getThrowable().getMessage());
        result.getThrowable().printStackTrace();
    }

    /**
     * Fire on test case finished with status 'Skipped'.
     *
     * @param result Test result.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), false);
    }

    /**
     * Fire on test case finished with status 'with issues'.
     *
     * @param result Test result.
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        this.stopVideo(result.id(), result.getTestName(), false);
    }

    /**
     * Fire on test case failed with timeout.
     *
     * @param result Test result.
     */
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
                if (!file1.delete())
                    System.out.println("##vso[task.logissue type=warning;] Не удалось отчистить файл - " + file1.getAbsolutePath());
            });
        });
    }

    /**
     * Fire when test run was finished.
     *
     * @param context Test run context.
     */
    @Override
    public void onFinish(ITestContext context) {
        String caseName = context.getSuite().getName();
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(context.getEndDate());
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS'Z'").format(context.getStartDate());
        String total = String.valueOf(context.getSuite().getAllMethods().size());
        String passed = String.valueOf(context.getPassedTests().size());
        String warnings = "0";
        String failed = String.valueOf(context.getFailedTests().size());
        String inconclusive = String.valueOf(context.getFailedButWithinSuccessPercentageTests().size());
        String skipped = String.valueOf(context.getSkippedTests().size());
        String duration = String.valueOf(Math.round(context.getEndDate().getTime() - context.getStartDate().getTime() / 1000));


        result.setTotal(total);
        result.setPassed(passed);
        result.setFailed(failed);
        result.setWarnings(warnings);
        result.setInconclusive(inconclusive);
        result.setSkipped(skipped);
        result.setEndTime(endTime);
        result.setStartTime(startTime);
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
        wrapper.setStartTime(startTime);
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
        suite.setStartTime(startTime);
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

    /**
     * Start video recorder.
     *
     * @param testName Test case name
     */
    private void startVideo(String testName) {
        File videoPathFile = new File(videoPath);
        if (!videoPathFile.exists())
            if (!videoPathFile.mkdirs()) {
                System.out.println("##vso[task.logissue type=warning;] Не удалось создать папки для записи видео");
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
            System.out.printf("##vso[task.logissue type=warning;] Не записать видео для теста '%s'. Сообщение:\n%s\n", testName, ex.getMessage());
        }

    }

    /**
     * Create screenshot.
     *
     * @param testID   Test ID
     * @param testName Test name
     */
    private void createScreenshot(String testID, String testName) {
        File imageFile = new File(imagePath + "/" + testID + ".bmp");
        if (!imageFile.getParentFile().exists()) {
            if (!imageFile.getParentFile().mkdirs()) {
                System.out.printf("##vso[task.logissue type=warning;] Не удалось создать папку для сохранения изображений.\n");
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
            System.out.printf("##vso[task.logissue type=warning;] Не удалось создать скриншот для теста '%s'. Сообщение:\n%s\n", testName, ex.getMessage());
        }
    }

    private void stopVideo(String testID, String testName, boolean needSave) {
        try {
            this.recorder.stop();
            LinkedList<String> attachment = attachments.getOrDefault(testID, new LinkedList<>());
            this.recorder.getCreatedMovieFiles().forEach(file -> {
                if (needSave) {
                    attachment.add(file.getAbsolutePath());
                    attachments.put(testID, attachment);
                } else {
                    if (!file.delete())
                        System.out.printf("##vso[task.logissue type=warning;] Не удалось удалить видео '%s'.\n", file.getAbsolutePath());
                }
            });
        } catch (Exception ex) {
            System.out.printf("##vso[task.logissue type=warning;] Не удалось остановить и сохранить видео для теста '%s'. Сообщение:\n%s\n", testName, ex.getMessage());
        }


    }

    /**
     * Get test cases with 'Inconclusive' status.
     *
     * @param context Test run context
     * @return Inconclusive tests
     */
    private List<NUnitTestCase> getInconclusive(ITestContext context) {
        return context
                .getFailedButWithinSuccessPercentageTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);

                    test.setResult(NunitResultStatus.Inconclusive);
                    return test;
                }).collect(Collectors.toList());
    }

    /**
     * Get test cases with 'Skipped' status.
     *
     * @param context Test run context
     * @return Skipped tests
     */
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
                    test.setResult(NunitResultStatus.Skipped);
                    return test;
                }).collect(Collectors.toList());
    }

    /**
     * Get test cases with 'Passed' status.
     *
     * @param context Test run context
     * @return Passed tests
     */
    private List<NUnitTestCase> getPassed(ITestContext context) {
        return context
                .getPassedTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);
                    test.setResult(NunitResultStatus.Passed);
                    return test;
                }).collect(Collectors.toList());
    }

    /**
     * Get test cases with 'Failed' status.
     *
     * @param context Test run context
     * @return Failed tests
     */
    private List<NUnitTestCase> getFailed(ITestContext context) {
        return context
                .getFailedTests()
                .getAllResults()
                .parallelStream()
                .map(result -> {
                    this.getFormattedCase(result);
                    NUnitTestCase test = this.getFormattedCase(result);
                    test.setResult(NunitResultStatus.Failed);
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

    /**
     * Get test attachments.
     *
     * @param path Attachment path
     * @return Attachments
     */
    private NUnitAttachments getAttachments(String path) {
        NUnitAttachments attachments = new NUnitAttachments();
        attachments.setFilePath(path);
        return attachments;
    }

    /**
     * Get formatted test case model.
     *
     * @param resultContext Test run context.
     * @return Formatted test case
     */
    private NUnitTestCase getFormattedCase(ITestResult resultContext) {
        NUnitTestCase test = new NUnitTestCase();
        test.setId(resultContext.id());
        test.setName(resultContext.getMethod().getDescription());
        test.setDuration(String.valueOf(Math.round(resultContext.getEndMillis() - resultContext.getStartMillis()) / 1000));
        test.setFullname(resultContext.getMethod().getQualifiedName());
        test.setAsserts("0");
        test.setMethodname(resultContext.getMethod().getQualifiedName());
        test.setClassname(resultContext.getTestClass().getName());

        return test;
    }
}
