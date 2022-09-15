package ru.checker.tests.ssm.windows.org;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.EditBox;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;
import ru.checker.tests.ssm.windows.core.templates.OkCancelWindow;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Всплывающее окно "Цех...".
 *
 * ID окна - "org_shop_details".
 * Файл конфигурации - "/Windows/ORGANIZATION/SHOP_DETAILS.yaml".
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class OrganizationShopPopupWindow extends OkCancelWindow {


    /**
     * Конструктор.
     *
     * @param window Текущее окно
     */
    public OrganizationShopPopupWindow(CheckerDesktopWindow window) {
        super(window);
    }

    /**
     * Получает элемент управления поля 'Номер'.
     * @return Элемент управления поля 'Номер'
     */
    public EditBox getNumberControl() {
        log.debug("Получение поля 'Номер'");
        EditBox number = super.getWindow().edit("org_shop_edit_number");
        log.debug("Поле 'Номер' получено");
        return number;
    }

    /**
     * Задает значение поля 'Номер'
     * @param number Значение
     */
    public void setNumber(long number) {
        assertFalse(number < 1, "Значение поля 'Номер' всплывающего окна 'Цех' формы 'Справочник организации' не может быть меньше 1");
        log.info("Задание значения '{}' поля 'Номер'", number);
        assertDoesNotThrow(
                () -> {
                    CheckerDesktopManipulator
                            .Keyboard
                            .sendText(this.getNumberControl().getElement(), String.valueOf(number), true);
                    Thread.sleep(500);
                },
                "Не удалось ставить значение '" + number + "' поля 'Номер' всплывающего окна 'Цех' формы 'Справочник организации'");
        String value = this.getNumber();
        log.info("Проверка значений ");
        assertEquals(value, String.valueOf(number), "Значение поля 'Номер' ('" + value + "')" +
                " всплывающего окна 'Цех' формы 'Справочник организации' не равно ('" + number + "')");
    }

    /**
     * Получает значение поля 'Номер'.
     * @return Значение поля
     */
    public String getNumber() {
        log.info("Получение значения поля 'Номер'");
        String value = assertDoesNotThrow(
                () -> this.getNumberControl().getValue(),
                "Не удалось получить значение поля 'Номер' всплывающего окна 'Цех' формы 'Справочник организации'");
        log.info("Значение '{}' поля 'Номер' получено", value);
        return value;
    }

    /**
     * Получает элемент управления поля 'Наименование'.
     * @return Элемент управления поля 'Наименование'
     */
    public EditBox getNameControl() {
        log.debug("Получение поля 'Наименование'");
        EditBox number = super.getWindow().edit("org_shop_edit_name");
        log.debug("Поле 'Наименование' получено");
        return number;
    }

    /**
     * Задает значение поля 'Наименование'
     * @param name Значение
     */
    public void setName(String name) {
        log.info("Задание значения '{}' поля 'Наименование'", name);
        assertDoesNotThrow(
                () -> {
                    this.getNameControl().setValue(name);
                },
                "Не удалось ставить значение '" + name + "' поля 'Наименование' всплывающего окна 'Цех' формы 'Справочник организации'");
        String value = this.getName();
        log.info("Проверка значений ");
        assertEquals(value, name, "Значение поля 'Наименование' ('" + value + "')" +
                " всплывающего окна 'Цех' формы 'Справочник организации' не равно ('" + name + "')");
    }

    /**
     * Получает значение поля 'Наименование'.
     * @return Значение поля
     */
    public String getName() {
        log.info("Получение значения поля 'Наименование'");
        String value = assertDoesNotThrow(
                () -> this.getNameControl().getValue(),
                "Не удалось получить значение поля 'Наименование' всплывающего окна 'Цех' формы 'Справочник организации'");
        log.info("Значение '{}' поля 'Наименование' получено", value);
        return value;
    }

    /**
     * Получает элемент управления поля 'Полное наименование'.
     * @return Элемент управления поля 'Полное наименование'
     */
    public EditBox getFullNameControl() {
        log.debug("Получение поля 'Полное наименование'");
        EditBox number = super.getWindow().edit("org_shop_edit_full_name");
        log.debug("Поле 'Полное наименование' получено");
        return number;
    }

    /**
     * Задает значение поля 'Полное наименование'
     * @param name Значение
     */
    public void setFullName(String name) {
        log.info("Задание значения '{}' поля 'Полное наименование'", name);
        assertDoesNotThrow(
                () -> this.getFullNameControl().setValue(name),
                "Не удалось ставить значение '" + name + "' поля 'Полное наименование' всплывающего окна 'Цех' формы 'Справочник организации'");
        String value = this.getFullName();
        log.info("Проверка значений");
        assertEquals(value, name, "Значение поля 'Полное наименование' ('" + value + "')" +
                " всплывающего окна 'Цех' формы 'Справочник организации' не равно ('" + name + "')");
    }

    /**
     * Получает значение поля 'Полное наименование'.
     * @return Значение поля
     */
    public String getFullName() {
        log.info("Получение значения поля 'Полное наименование'");
        String value = assertDoesNotThrow(
                () -> this.getFullNameControl().getValue(),
                "Не удалось получить значение поля 'Полное наименование' всплывающего окна 'Цех' формы 'Справочник организации'");
        log.info("Значение '{}' поля 'Полное наименование' получено", value);
        return value;
    }

    /**
     * Получает элемент управления поля 'Номер SAP'.
     * @return Элемент управления поля 'Номер SAP'
     */
    public EditBox getSapNumberControl() {
        log.debug("Получение поля 'Номер SAP'");
        EditBox number = super.getWindow().edit("org_shop_edit_number_sap");
        log.debug("Поле 'Номер SAP' получено");
        return number;
    }

    /**
     * Задает значение поля 'Номер SAP'
     * @param name Значение
     */
    public void setSapNumber(String name) {
        log.info("Задание значения '{}' поля 'Номер SAP'", name);
        assertDoesNotThrow(
                () -> this.getSapNumberControl().setValue(name),
                "Не удалось ставить значение '" + name + "' поля 'Номер SAP' всплывающего окна 'Цех' формы 'Справочник организации'");
        String value = this.getSapNumber();
        log.info("Проверка значений");
        assertEquals(value, name, "Значение поля 'Номер SAP' ('" + value + "')" +
                " всплывающего окна 'Цех' формы 'Справочник организации' не равно ('" + name + "')");
    }

    /**
     * Получает значение поля 'Номер SAP'.
     * @return Значение поля
     */
    public String getSapNumber() {
        log.info("Получение значения поля 'Полное наименование'");
        String value = assertDoesNotThrow(
                () -> this.getSapNumberControl().getValue(),
                "Не удалось получить значение поля 'Номер SAP' всплывающего окна 'Цех' формы 'Справочник организации'");
        log.info("Значение '{}' поля 'Номер SAP' получено", value);
        return value;
    }

    /**
     * Получает элемент управления поля 'Отклонение'.
     * @return Элемент управления поля 'Отклонение'
     */
    public EditBox getDeviationControl() {
        log.debug("Получение поля 'Отклонение'");
        EditBox number = super.getWindow().edit("org_shop_edit_deviation");
        log.debug("Поле 'Отклонение' получено");
        return number;
    }

    /**
     * Задает значение поля 'Номер'
     * @param number Значение
     */
    public void setDeviation(long number) {
        assertFalse(number < 1, "Значение поля 'Отклонение' всплывающего окна 'Цех' формы 'Справочник организации' не может быть меньше 1");
        log.info("Задание значения '{}' поля 'Номер'", number);
        assertDoesNotThrow(
                () -> {
                    CheckerDesktopManipulator
                            .Keyboard
                            .sendText(this.getDeviationControl().getElement(), String.valueOf(number), true);
                    Thread.sleep(500);
                },
                "Не удалось ставить значение '" + number + "' поля 'Отклонение' всплывающего окна 'Цех' формы 'Справочник организации'");
        String value = this.getDeviation();
        log.info("Проверка значений ");
        assertEquals(value, String.valueOf(number), "Значение поля 'Отклонение' ('" + value + "')" +
                " всплывающего окна 'Цех' формы 'Справочник организации' не равно ('" + number + "')");
    }

    /**
     * Получает значение поля 'Номер'.
     * @return Значение поля
     */
    public String getDeviation() {
        log.info("Получение значения поля 'Отклонение'");
        String value = assertDoesNotThrow(
                () -> this.getDeviationControl().getValue(),
                "Не удалось получить значение поля 'Отклонение' всплывающего окна 'Цех' формы 'Справочник организации'");
        log.info("Значение '{}' поля 'Отклонение' получено", value);
        return value;
    }

}
