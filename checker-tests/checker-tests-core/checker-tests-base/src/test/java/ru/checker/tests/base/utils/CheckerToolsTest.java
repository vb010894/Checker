package ru.checker.tests.base.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CheckerTools
 * @author vd.zinovev
 * @see CheckerTools - class under test
 */
@Log4j2(topic = "CheckerTools")
class CheckerToolsTest {

    /**
     * Positive. Get application root path.
     */
    @Test
    void getRootPath() {
        String rootPath = CheckerTools.getRootPath();
        assertNotNull(
                rootPath,
                "Не удалось найти корневую директорию. Результат поиска - null");
        assertFalse(Strings.isBlank(rootPath), "Не удалось найти корневую директорию. Результат поиска - ''");

        File root = new File(rootPath);
        assertTrue(root.exists(), "Корневая директория не существует. Искомая директория - " + rootPath);
        log.info("Корневая директория найдена и существует - " + rootPath);
    }

    @Test
    void convertYAMLToMap() {
        Map<String, Object> map = CheckerTools.convertYAMLToMap("/Tests/Notepad/Cases/Notepad.yaml");
    }

}