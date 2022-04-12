package ru.checker.tests.base.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.util.ImageHelper;
import ru.checker.tests.base.enums.CheckerOCRLanguage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Checker OCR utils.
 * @author vd.zinovev
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckerOCRUtils {

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

    /**
     * Take screenshot and recognize the text from place.
     * @param language Text language
     * @param rectangle Target rectangle
     * @return Recognized test from screen's rectangle
     */
    public static String getTextFromRectangle(Rectangle rectangle, CheckerOCRLanguage language) {
        return assertDoesNotThrow(
                () -> beautifyOutput(getTesseract(language).doOCR(prepareImage(getImageFromScreen(rectangle)))),
                "Не удалось распознать текст в области - " + rectangle);
    }

    /**
     * Take screenshot from screen area.
     * @param rectangle Screen area
     * @return Recognized text
     */
    public static BufferedImage getImageFromScreen(Rectangle rectangle) {
        return assertDoesNotThrow(
                () ->  new Robot().createScreenCapture(rectangle),
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
        return ImageHelper.convertImageToGrayscale(ImageHelper.getScaledInstance(image, image.getWidth() * 3, image.getHeight() * 3));
    }

    /**
     * Remove some symbols.
     * @param output Raw text
     * @return Formatted texttymrf
     */
    private static String beautifyOutput(String output) {
        return output.replace("\n", "").replace("\r", "");
    }

}
