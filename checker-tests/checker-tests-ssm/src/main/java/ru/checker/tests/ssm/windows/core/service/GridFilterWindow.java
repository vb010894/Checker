package ru.checker.tests.ssm.windows.core.service;

import junit.framework.AssertionFailedError;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.windows.core.templates.OkCancelWindow;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Окно фильтрации таблицы.
 * Файл конфигурации - /Windows/CORE/FILTER_DIALOG.yaml.
 * ID окна - ssm_core_filter.
 *
 * @author vd.zinovev
 */
@Log4j2
public class GridFilterWindow extends OkCancelWindow {


    /**
     * Конструктор шаблона.
     *
     * @param window Текущее окно
     */
    public GridFilterWindow(CheckerDesktopWindow window) {
        super(window);
    }

    /**
     * Задает первое условие фильтра.
     * @param condition Условие
     * @param value Значение
     */
    public void setFirstCondition(SSMGrid.Condition condition, String value) {
        this.setConditionField(condition, "ssm_core_filter_first_condition", "Первое условие с таблицы");
        this.setValueField(value, "ssm_core_filter_first_value", "Первое значение фильтра таблицы");
    }

    /**
     * Задание значение фильтра колонки таблицы с помощью конфигуратора.
     * @see ru.checker.tests.ssm.controls.grid.SSMGrid.ConditionConfigurer
     * @param configurer Конфигуратор
     */
    public void setFilterByConfigurer(SSMGrid.ConditionConfigurer configurer) {
        assertNotNull(configurer.getCondition1(), "Не задано первое поле-условие фильтра колонки таблицы");
        assertNotNull(configurer.getValue1(), "Не задано перове поле-значение фильтра колонки таблицы");
        this.setFirstCondition(configurer.getCondition1(), configurer.getValue1());
        if(Objects.nonNull(configurer.getSeparator()) && !configurer.getSeparator().equals(SSMGrid.Separator.NONE)) {
            assertNotNull(configurer.getCondition2(), "Не задано второе поле-условие фильтра колонки таблицы");
            assertNotNull(configurer.getValue2(), "Не задано второе поле-значение фильтра колонки таблицы");
            this.setSecondCondition(configurer.getCondition2(), configurer.getValue2());
        }
    }

    /**
     * Задает первое условие фильтра.
     * @param condition Условие
     * @param value Значение
     */
    public void setSecondCondition(SSMGrid.Condition condition, String value) {
        this.setConditionField(condition, "ssm_core_filter_second_condition", "Второе условие с таблицы");
        this.setValueField(value, "ssm_core_filter_second_value", "Второе значение фильтра таблицы");
    }

    /**
     * Выполняет переключение соединителя условий окна фильтрации.
     * @param separator Соединитель
     */
    public void setSeparator(SSMGrid.Separator separator) {
        switch (separator) {
            default:
                return;
            case AND:
                this.enableRadio("ssm_core_filter_and_radio", "И");
                break;
            case OR:
                this.enableRadio("ssm_core_filter_or_radio", "ИЛИ");
                break;
        }
    }

    private void enableRadio(String ID, String name) {
       log.debug("Переключение радиокнопки '{}. {}'", ID, name);
       assertDoesNotThrow(() -> this.getWindow().button(ID).click(), "Не удалось переключить радиокнопку на '" + name + "'");
       log.debug("Радиокнопка переключена '{}. {}'", ID, name);
    }


    /**
     * Задание поля-условия.
     * @param condition Условие
     * @param ID ID поля
     * @param name Имя поля
     */
    public  void setConditionField(SSMGrid.Condition condition, String ID, String name) {
        log.debug("Задание значения '{}' полю-условию '{}. {}'", condition.getValue(), ID, name);
        assertDoesNotThrow(
                () -> this.getFiled(ID, name).setValue(condition.getValue()),
                String.format("Не удалось задать значение '%s' для поля-условия '%s. %s'", condition.getValue(), ID, name));
        log.debug("Значение '{}' поля-условия '{}. {}' задано. Проверка значения...", condition.getValue(), ID, name);
        SSMGrid.Condition check = this.getConditionField(ID, name);
        assertEquals(check, condition, String.format(
                "Заданное значение '%s' поля '%s. %s' не соответствует требуемому '%s'",
                check.getValue(),
                ID,
                name,
                condition.getValue()));
        log.debug("Значение поля-условия '{}. {}'  соответствует требуемому '{}'", ID, name, condition.getValue());
    }

    /**
     * Получает значение поля-условия фильтра.
     * @param ID ID поля
     * @param name Имя поля
     * @return Значение перечисления условий
     */
    public SSMGrid.Condition getConditionField(String ID, String name) {
        log.debug("Чтение значения из поля-условия '{}. {}'", ID, name);
        String value = assertDoesNotThrow(() ->
                        this.getFiled(ID, name).getValue()
        , String.format("Не удалось прочитать значение из поля-условия '%s. %s'", ID, name));
        log.debug("Значение '{}' поля-условия '{}. {}' получено", value, ID, name);
        return Arrays.stream(SSMGrid.Condition.values())
                                .parallel()
                                .filter(cond -> cond.getValue().equals(value)).findFirst()
                                .orElseThrow(() -> {throw new AssertionFailedError("В перечисленных значениях условий '" + value + "' не найдено");});
    }

}
