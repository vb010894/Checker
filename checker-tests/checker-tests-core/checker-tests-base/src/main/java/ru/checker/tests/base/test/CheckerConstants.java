package ru.checker.tests.base.test;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

/**
 * Хранит константы тестового случая.
 *
 * @author vd.zinovev
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CheckerConstants {

    /**
     * Локальные константы.
     * <p>
     * !!! Сбрасываются перед каждым тестовым случаем!!!
     */
    static Map<String, Object> localConstants = new HashMap<>();

    /**
     * Глобальные переменные.
     * <p>
     * !!! Сохраняется все время выполнения тестового запуска.
     */
    static final Map<String, Object> globalConstants = new HashMap<>();

    /**
     * Получает константу по имени.
     *
     * @param name Имя константы
     * @return Значение константы
     */
    public static Object getConstant(String name) {
        return globalConstants.getOrDefault(name, localConstants.getOrDefault(name, null));
    }

    /**
     * Очищает константы.
     */
    public static void clearConstants() {
        localConstants = new HashMap<>();
    }

    /**
     * Сохраняет константу в локальные.
     * @param name Имя константы
     * @param value Значение константы
     */
    public static void saveConstant(String name, Object value) {
        saveConstant(name, value, false);
    }

    /**
     * Сохраняет константу.
     * @param name Имя константы
     * @param value Значение константы
     * @param toGlobal В глобальные/локальные
     */
    public static void saveConstant(String name, Object value, boolean toGlobal) {
        if(toGlobal)
            globalConstants.put(name, value);
        else
            localConstants.put(name,value);
    }

}
