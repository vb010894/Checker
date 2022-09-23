package ru.checker.tests.ssm.windows.product;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.EditBox;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;
import ru.checker.tests.ssm.windows.core.templates.OkCancelWindow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Всплывающее окно "Выпуск продукции" модуля "Выпуск продукции".
 *
 * Файл конфигурации - "/Windows/PRODUCT/RELEASE_PRODUCT_POPUP.yaml".
 * ID - "product_release_window".
 *
 * @author vd.zinovev
 */
@Log4j2
@SuppressWarnings("unused")
public class ProductReleasePopupWindow extends OkCancelWindow {

    /**
     * Конструктор.
     *
     * @param window Текущее окно
     */
    public ProductReleasePopupWindow(CheckerDesktopWindow window) {
        super(window);
    }

    /**
     * Задать значение полю "Номер накладной"
     * @param number Значение
     */
    public void setInvoiceNumber(String number) {
        this.setValueField(number, "product_release_invoice_number", "Номер накладной");
    }

    /**
     * Получить значение поля "Номер накладной"
     */
    public String getInvoiceNumber() {
        return this.getValueField( "product_release_invoice_number", "Номер накладной");
    }

    /**
     * Задать значение полю "Номер накладной"
     * @param number Значение
     */
    public void setReleaseCount(int number) {
        EditBox edit = this.getFiled("product_release_count", "Выпустить, шт");
        log.info("Ввод значения '{}' в поле '{}. {}'", number, "product_release_count", "Выпустить, шт");
        CheckerDesktopManipulator.Keyboard.sendText(edit.getElement(), String.valueOf(number), true);
        log.debug("Проверка значения поля '{}. {}'","product_release_count", "Выпустить, шт");
        assertDoesNotThrow(() -> Thread.sleep(500), "Не удалось выполнить паузу после вставки в поле 'product_release_count. Выпустить, шт'");
        String value = this.getReleaseCount();
        assertEquals(
                value,
                String.valueOf(number),
                String.format("Значен" +
                        "ие '%s' поля 'product_release_count. Выпустить, шт' не равно вводимому '%d'", value, number));
        log.info("Значение поля 'product_release_count. Выпустить, шт' введено успешно");
    }

    /**
     * Получить значение поля "Номер накладной"
     */
    public String getReleaseCount() {
        return this.getValueField( "product_release_count", "Выпустить, шт");
    }

}
