package ru.checker.tests.base.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.ImageHelper;
import ru.checker.tests.base.enums.CheckerOCRLanguage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Checker OCR utils.
 * @author vd.zinovev
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckerOCRUtils {

    static int scale = 3;

    /**
     * Изменяет индекс увеличения картинки для распознавания.
     *
     * !!!ВАЖНО: После использования измененного индекса,
     * требуется вернуть все обратно,
     * так как многие функции реализованы на стандартном увеличении - 3!!!
     *
     * @param targetScale Нужный индекс увеличения
     */
    public static void changeScale(int targetScale) {
        scale = targetScale;
    }

    /**
     * Tesseract config and get.
     * @param language Tesseract language
     * @return Configured tesseract
     */
    private static Tesseract1 getTesseract(CheckerOCRLanguage language) {
        String root = new File("").getAbsolutePath();
        String dataPath = root.substring(0, root.lastIndexOf("Checker")) + "Checker/configs/tesseract";
        Tesseract1 tesseract1 = new Tesseract1();
        tesseract1.setDatapath(dataPath.replace("\\", "/"));
        tesseract1.setLanguage(language.getValue());
        return tesseract1;
    }

    /**
     * Get text from file.
     * @param file Target file
     * @param language File language
     * @return Recognized text from file
     */
    public static String getTextFromFile(File file, CheckerOCRLanguage language) {
        return assertDoesNotThrow(
                () -> {
                    BufferedImage image = ImageIO.read(file);
                    return beautifyOutput(getTesseract(language).doOCR(prepareImage(image)));
                },
                "Не удалось распознать текст из файла - " + ((file == null) ? "null" : file.getAbsolutePath()));
    }

    /**
     * Get text from file with default 'RUS' value.
     * @param file Target file
     * @return Recognized russian text from file
     */
    public static String getTextFromFile(File file) {
        return getTextFromFile(file, CheckerOCRLanguage.RUS);
    }

    /**
     * Get text from input stream with default 'RUS' value.
     * @param stream Input stream
     * @return Recognized russian text from input stream
     */
    public static String getTextFromInputStream(InputStream stream) {
        return getTextFromInputStream(stream, CheckerOCRLanguage.RUS);
    }

    /**
     * Get text from input stream.
     * @param stream Input stream
     * @param language Text language
     * @return Recognized text from input stream
     */
    public static String getTextFromInputStream(InputStream stream, CheckerOCRLanguage language) {
        return assertDoesNotThrow(() -> {
            BufferedImage image = ImageIO.read(stream);
            return beautifyOutput(getTesseract(language).doOCR(prepareImage(image)).replace("\n", "").replace("\r", ""));
        });
    }

    /**
     * Take screenshot and recognize the text from place
     * with default 'RUS' language.
     * @param rectangle Target rectangle
     * @return Recognized test from screen's rectangle
     */
    public static String getTextFromRectangle(Rectangle rectangle) {
        return getTextFromRectangle(rectangle, CheckerOCRLanguage.RUS);
    }

    public static Rectangle getTextAndMove(Rectangle rectangle, String text) {
        return getTextAndMove(rectangle, text, CheckerOCRLanguage.RUS, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);
    }

    /**
     * Recognize text and move to word's rectangle with 'RUS' default language.
     * @param rectangle Rectangle for recognize
     * @param text Searching word
     * @return Recognized text
     */
    public static Rectangle getTextAndMove(Rectangle rectangle, String text, int level) {
        return getTextAndMove(rectangle, text, CheckerOCRLanguage.RUS, level);
    }

    public static Rectangle getTextAndMove(Rectangle rectangle, String text, CheckerOCRLanguage language) {
        return getTextAndMove(rectangle,text,language, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);
    }

    /**
     * Recognize text and move to word's rectangle.
     * @param rectangle Rectangle for recognize
     * @param pattern Searching text pattern
     * @param language Recognizing language
     * @return Recognized text
     */
    public static Rectangle getTextAndMove(Rectangle rectangle, Pattern pattern, CheckerOCRLanguage language, int level) {
        return assertDoesNotThrow(() -> {
            Robot robot = new Robot();
            BufferedImage img = prepareImage(getImageFromScreen(rectangle));
            List<Word> rectangles = getTesseract(language).getWords(img, level);
            AtomicReference<Rectangle> out = new AtomicReference<>(rectangle);
            rectangles.forEach(r -> {
                System.out.println(r.getText());
                if(pattern.matcher(r.getText()).find()) {
                    Rectangle bounding = r.getBoundingBox();
                    int x = rectangle.x + (bounding.x / scale);
                    int y = rectangle.y + (bounding.y / scale);
                    int width = bounding.width / scale;
                    int height = bounding.height / scale;
                    robot.mouseMove(x + 5, y + 5);
                    out.set(new Rectangle(x, y, width, height));
                }
            });
            return out.get();
        }, "Не удалось распознать текст в области - " + rectangle);
    }

    /**
     * Recognize text and move to word's rectangle.
     * @param rectangle Rectangle for recognize
     * @param text Searching text
     * @param language Recognizing language
     * @return Recognized text
     */
    public static Rectangle getTextAndMove(Rectangle rectangle, String text, CheckerOCRLanguage language, int level) {
        return assertDoesNotThrow(() -> {
            Robot robot = new Robot();
            BufferedImage img = prepareImage(getImageFromScreen(rectangle));
            List<Word> rectangles = getTesseract(language).getWords(img, level);
            AtomicReference<Rectangle> out = new AtomicReference<>(rectangle);
            rectangles.forEach(r -> {
                System.out.println(r.getText());
                if(r.getText().contains(text)) {
                    Rectangle bounding = r.getBoundingBox();
                    int x = rectangle.x + (bounding.x / scale);
                    int y = rectangle.y + (bounding.y / scale);
                    int width = bounding.width / scale;
                    int height = bounding.height / scale;
                    robot.mouseMove(x + 5, y + 5);
                    out.set(new Rectangle(x, y, width, height));
                }
            });
            return out.get();
        }, "Не удалось распознать текст в области - " + rectangle);
    }

    /**
     * Take screenshot and recognize the text from place.
     * @param language Text language
     * @param rectangle Target rectangle
     * @return Recognized test from screen's rectangle
     */
    public static String getTextFromRectangle(Rectangle rectangle, CheckerOCRLanguage language) {
        return assertDoesNotThrow(
                () -> /*beautifyOutput*/(getTesseract(language).doOCR(prepareImage(getImageFromScreen(rectangle)))),
                "Не удалось распознать текст в области - " + rectangle);
    }

    /**
     * Take screenshot from screen area.
     * @param rectangle Screen area
     * @return Recognized text
     */
    public static BufferedImage getImageFromScreen(Rectangle rectangle) {
        return assertDoesNotThrow(
                () -> {
                    Robot robot = new Robot();
                    return robot.createScreenCapture(rectangle);
                },
                "Не удалось получить скриншот из области - " + rectangle.toString());
    }

    /**
     * Prepare image before recognizing.
     *
     * Steps:
     * 1) scale 3x,
     * 2) convert to grayscale.
     *
     * @param image Source image
     * @return Output image
     */
    private static BufferedImage prepareImage(BufferedImage image) {
        return ImageHelper.convertImageToGrayscale(ImageHelper.getScaledInstance(image, image.getWidth() * scale, image.getHeight() * scale));
    }

    /**
     * Remove some symbols.
     * @param output Raw text
     * @return Formatted text
     */
    private static String beautifyOutput(String output) {
        return output.replace("\n", "").replace("\r", "");
    }

}
