package ru.checker.tests.ssm.windows.sap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.windows.core.templates.OkCancelWindow;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SapLotsmanFilterWindow extends OkCancelWindow {

    @Getter
    CheckerDesktopWindow lotsman;

    public SapLotsmanFilterWindow(CheckerDesktopWindow window) {
        super(window);
        this.lotsman = window;
    }

    public void setSearchValue(String value) {
        log.info("Ввод значения {} в поле поиска окна 'Выбор заказа Лоцман'", value);
        assertDoesNotThrow(
                () -> {
                    log.debug("Очистка поля поиска окна 'Выбор заказа Лоцман'");
                    this.lotsman.edit("field_search").setValue("");
                    log.debug("Вставка значения '{}' в поле поиска окна 'Выбор заказа Лоцман'", value);
                    this.lotsman.edit("field_search").setValue(value);
                },
                "Не удалось вставить значение в поле поиска окна 'Выбор заказа Лоцман'");
        log.info("Проверка введенного значения");
        assertEquals(this.getSearchValue(), value, "Значение поля поиска окна 'Выбор заказа Лоцман' не равно ожидаемому");
        log.info("Проверка прошла успешно. Значение в поле поиска окна 'Выбор заказа Лоцман' введено корректно");
    }

    public String getSearchValue() {
        log.info("Получение значения поля поиска окна 'Выбор заказа Лоцман'");
        String value = assertDoesNotThrow(
                () -> this.lotsman.edit("field_search").getValue(),
                "Не удалось получить значение поля поиска окна 'Выбор заказа Лоцман'");
        log.info("Получено значение - '{}'", value);
        return value;
    }

    public void clickSearch() {
        log.info("Нажатие на кнопку 'Поиск' окна 'Выбор заказа Лоцман'");
        assertDoesNotThrow(
                () ->this.lotsman.button("button_search").click(),
                "Не удалось нажать на кнопку 'Поиск' окна 'Выбор заказа Лоцман'");
        log.info("Кнопка 'Поиск' нажата");
    }

    public SSMGrid getLotsmanOrderGrid() {
        log.info("Получение таблицы 'Заказы Лоцман' окна 'Выбор заказа Лоцман'");
        SSMGrid grid = this.lotsman.custom("grid_lotsman_order", SSMGrid.class);
        log.info("Проверка активности таблицы 'Заказы Лоцман'");
        assertDoesNotThrow(() -> {
            if (!grid.getControl().isEnabled())
                throw new IllegalStateException("Таблица 'Заказы Лоцман' не активна");
        }, "Не удалось получить состояние таблицы 'Заказы Лоцман'");
        log.info("Таблица 'Заказы Лоцман' активна");
        return grid;
    }
}
