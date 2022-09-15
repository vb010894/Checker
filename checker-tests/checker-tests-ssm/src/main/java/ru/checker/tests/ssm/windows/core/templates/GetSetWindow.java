package ru.checker.tests.ssm.windows.core.templates;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Button;
import mmarquee.automation.controls.EditBox;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Шаблон окна со вставкой и получением значения полей.
 *
 * @author vd.zinovev
 */
@Log4j2
public abstract class GetSetWindow {

    /**
     * Текущее окно.
     */
    @Getter
    CheckerDesktopWindow window;

    /**
     * Конструктор шаблона.
     * @param window Текущее окно
     */
    public GetSetWindow(CheckerDesktopWindow window) {
        this.window = window;
    }

    /**
     * Задание поля-значения.
     * @param value Значение
     * @param ID ID поля
     * @param name Имя поля
     */
    public  void setValueField(String value, String ID, String name) {
        log.debug("Задание значения '{}' полю '{}. {}'", value, ID, name);
        assertDoesNotThrow(
                () -> this.getFiled(ID, name).setValue(value),
                String.format("Не удалось задать значение '%s' для поля '%s. %s'", value, ID, name));
        log.debug("Значение '{}' поля-значения '{}. {}' задано. Проверка значения...", value, ID, name);
        String check = this.getValueField(ID, name);
        assertEquals(check, value, String.format(
                "Заданное значение '%s' поля '%s. %s' не соответствует требуемому '%s'",
                check,
                ID,
                name,
                value));
        log.debug("Значение поля-значения '{}. {}'  соответствует требуемому '{}'", ID, name, value);
    }

    /**
     * Получает значение поля-значения.
     * @param ID ID поля
     * @param name Имя поля
     * @return Значение
     */
    public String getValueField(String ID, String name) {
        log.debug("Чтение значения из поля '{}. {}'", ID, name);
        String value = assertDoesNotThrow(() ->
                        this.getFiled(ID, name).getValue()
                , String.format("Не удалось прочитать значение из поля '%s. %s'", ID, name));
        log.debug("Значение '{}' поля '{}. {}' получено", value, ID, name);
        return value;
    }

    /**
     * Получение поля.
     * @param ID ID поля
     * @param name Имя поля
     * @return Поле
     */
    protected EditBox getFiled(String ID, String name) {
        log.debug("Получение поля '{}. {}'", ID, name);
        EditBox edit = this.getWindow().edit(ID);
        log.debug("Поля '{}. {}' получено", ID, name);
        return edit;
    }

    /**
     * Получение кнопки формы
     * @param ID ID кнопки
     * @param name Имя кнопки
     * @return Кнопка
     */
    protected Button getButton(String ID, String name) {
        log.debug("Получение кнопки '{}. {}'", ID, name);
        Button found = this.getWindow().button(ID);
        log.debug("Кнопка '{}. {}' получена", ID, name);
        return found;
    }

}
