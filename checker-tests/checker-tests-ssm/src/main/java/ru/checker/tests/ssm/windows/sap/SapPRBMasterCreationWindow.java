package ru.checker.tests.ssm.windows.sap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Окна "Выбор Мастеров...".
 * ID в конфигурациях - sap_order_prb_master_form.
 * Файл - SAP/ORDER_MASTER_PRB.yaml.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SapPRBMasterCreationWindow {

    /**
     * Базовое окно.
     */
    @Getter
    final CheckerDesktopWindow prb;


    /**
     * Конструктор окна "Выбор Мастеров...".
     *
     * @param window Окно.
     */
    public SapPRBMasterCreationWindow(CheckerDesktopWindow window) {
        this.prb = window;
    }


    /**
     * Получает таблицу "Мастера".
     * ID в конфигурациях - prb_master_grid.
     *
     * @return Таблица "Мастера"
     */
    public SSMGrid getMasterGrid() {
        log.info("ИПолучение таблицы 'Мастера'");
        return this.prb.custom("prb_master_grid", SSMGrid.class);
    }

    /**
     * Нажимает на кнопку "Выбрать".
     * ID в конфигурациях - prb_master_choose_button.
     */
    public void clickChoose() {
        log.info("Нажатие на кнопку 'Выбрать'");
        assertDoesNotThrow(
                () -> this.prb.button("prb_master_choose_button").click(),
                "Не удалось нажать кнопку 'Выбрать'");
        log.info("Кнопка 'Выбрать' нажата");
    }

    /**
     * Нажимает на кнопку "Закрыть".
     * ID в конфигурациях - prb_master_close_button.
     */
    public void clickClose() {
        log.info("Нажатие на кнопку 'Закрыть'");
        assertDoesNotThrow(
                () -> this.prb.button("prb_master_close_button").click(),
                "Не удалось нажать кнопку 'Закрыть'");
        log.info("Кнопка 'Закрыть' нажата");
    }

    /**
     * Получает значение поля "Поиск".
     * ID в конфигурациях - prb_master_search_edit.
     *
     * @return Значение поля "Поиск".
     */
    public String getSearchValue() {
        log.info("Получение значения строки 'Поиск' окна 'Выбор Мастеров'");
        String value = assertDoesNotThrow(
                () -> this.prb.edit("prb_master_search_edit").getValue(),
                "Не удалось получить значение поля 'Поиск'");
        log.info("Получено значение - '{}' поля 'Поиск'", value);
        return value;
    }

    /**
     * Задает значение поля "Поиск".
     * ID в конфигурациях - prb_master_search_edit.
     *
     * @param value Требуемое значение.
     */
    public void setSearchValue(String value) {
        log.info("Получение значения строки 'Поиск' окна 'Выбор Мастеров'");
        assertDoesNotThrow(
                () -> this.prb.edit("prb_master_search_edit").setValue(value),
                "Не удалось задать значение '" + value + "'  для поля 'Поиск'");
        log.info("Проверка заданного значения");
        assertEquals(this.getSearchValue(), value, "Значение поля 'Поиск' несоответствует заданному");
        log.info("Значение - '{}' для поля 'Поиск' задано", value);
    }
}
