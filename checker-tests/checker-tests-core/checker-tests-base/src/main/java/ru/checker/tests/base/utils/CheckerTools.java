package ru.checker.tests.base.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Checker base tools.
 * @author vd.zinovev
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckerTools {

    /**
     * Root path getter.
     * @return Root path
     */
    public static String getRootPath() {
        String root = new File("").getAbsolutePath();
        return root.substring(0, root.lastIndexOf("Checker")) + "Checker";
    }

    /**
     * Convert YAML to required class.
     * @param partPath YAML file part path
     * @param implementation Target class
     * @param <T> Target class instance
     * @return Instance of target class
     */
    public static <T> T convertYAMLToClass(String partPath, Class<T> implementation) {
        return assertDoesNotThrow(() -> {
            Yaml yaml = new Yaml(new Constructor());
            InputStream config = new FileInputStream(CheckerTools.getRootPath() + partPath);
            return yaml.loadAs(config, implementation);
        }, "Не удалось загрузить описание тестового случая - " + partPath);
    }

    public static Map<String, Object> convertYAMLToMap(String partPath) {
        return assertDoesNotThrow(() -> {
            Yaml yaml = new Yaml(new Constructor());
            InputStream config = new FileInputStream(CheckerTools.getRootPath() + partPath);
            return yaml.load(config);
        }, "Не удалось загрузить описание тестового случая - " + partPath);
    }

}
