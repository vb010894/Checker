package ru.checker.tests.desktop.utils;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import mmarquee.automation.Element;
import ru.checker.tests.base.utils.CheckerTools;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CheckerSampleTools {

    static String SAMPLE_FOLDER = CheckerTools.getRootPath() + "/" + CheckerDesktopTest.getCurrentApp().getName() + "/Samples";

    @SneakyThrows
    public static BufferedImage createElementScreenshot(Element element) {
        return new Robot().createScreenCapture(element.getBoundingRectangle().toRectangle());
    }

    @SneakyThrows
    public static BufferedImage getSample(String ID) {
        File file = new File(SAMPLE_FOLDER + "/" + ID + ".bmp");
        if(!file.exists())
            throw new FileNotFoundException("Не найден файл эталона - " + file.getAbsolutePath());
        return ImageIO.read(file);
    }

    @SneakyThrows
    public static void saveToSample(String ID, BufferedImage sample) {
        File file = new File(SAMPLE_FOLDER + "/" + ID + ".bmp");
        if(!ImageIO.write(sample,"bmp", file))
            throw new IOException("Не удалось сохранить эталон - " + file.getAbsolutePath());
    }

    public static boolean compareSample(String ID, BufferedImage candidate, AtomicReference<BufferedImage> diffMarkedImage) {
        BufferedImage sample = getSample(ID);
        return compareImage(sample, candidate, diffMarkedImage);
    }

    @SneakyThrows
    public static boolean compareImage(BufferedImage image1, BufferedImage image2, AtomicReference<BufferedImage> diffMarkedImage) {
        int img1Width = image1.getWidth();
        int img1Height = image1.getHeight();
        boolean checkResult = true;
        if(!(img1Width == image2.getWidth() && img1Height == image2.getHeight()))
            return false;

        BufferedImage temp = image2;

        for (int y = 0; y < img1Height; y++) {
            for (int x = 0; x < img1Width; x++) {
                if(image1.getRGB(x, y) != image2.getRGB(x, y)) {
                    Color red = new Color(255, 0, 0, 70);
                    temp.setRGB(x, y, red.getRGB());
                    if(checkResult)
                        checkResult = false;
                }
            }
        }

        diffMarkedImage.set(temp);
        return checkResult;
    }
}
