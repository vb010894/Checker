package ru.checker.tests.base.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Инструменты добавления вложений.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public final class AttachmentUtils {

    /**
     * Папка с дополнительными вложениями.
     */
    static String attachmentFolder = CheckerTools.getRootPath() + "/Reports/Attachment";

    /**
     * Создание папки с дополнительными вложениями.
     */
    private static void makeDir() {
        File path = new File(attachmentFolder);
        if(!path.exists()) {
            log.debug("Не найдена папка с вложениями ({}). Создание...", attachmentFolder);
            if(!path.mkdirs())
                log.error("Не удалось создать папку с с вложениями ({})", attachmentFolder);
        }
    }

    /**
     * Добавить строковое вложение в виде файла.
     *  Добавляет файл *.txt в папку с доп вложениями,
     *  которые после считается слушателем теста.
     *
     * @param name Имя файла
     * @param content Контент файла
     */
    @SneakyThrows
    public static void addStringAttachment(String name, String content) {
        log.debug("Добавление строкового вложения");
        makeDir();
        File contentFile = new File(attachmentFolder + "/" + name + ".txt");
        log.debug("Запись контента в файл '{}'. Кодировка UTF-8", contentFile.getAbsolutePath());
        FileUtils.write(contentFile, content, StandardCharsets.UTF_8);
        log.debug("Файл успешно записан");
    }

    @SneakyThrows
    public static void addImageAttachment(String name, BufferedImage image) {
        log.debug("Добавление строкового вложения");
        makeDir();
        File contentFile = new File(attachmentFolder + "/" + name + ".bmp");
        log.debug("Сохранение изображения в файл '{}'.", contentFile.getAbsolutePath());
        ImageIO.write(image, "bmp", contentFile);
        log.debug("Изображение успешно сохранено");
    }

}
