package ru.checker.tests.ssm.temp.windows;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.desktop.base.robot.CheckerDesktopMarker;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.controls.toogle.SSMToggle;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SapFilterWindow {

    @Getter
    final CheckerDesktopWindow filter;


    public SapFilterWindow(CheckerDesktopWindow window) {
        this.filter = window;
    }

    public void selectShop(String shop) {
        log.info("Выбор цеха в окне 'Фильтр'(Заказы SAP)");
        assertDoesNotThrow(
                () -> this.filter.edit("field_shop").setValue(shop),
                String.format("Не удалось вставить значение %s в поле 'Цех' окна 'Фильтр'(Заказы SAP)", shop));
        log.info("Цех успешно выбран");
    }

    public void selectYear(String year) {
        log.info("Выбор года в окне 'Фильтр'(Заказы SAP)");
        assertDoesNotThrow(
                () -> this.filter.edit("field_year").setValue(year),
                String.format("Не удалось вставить значение %s в поле 'Год' окна 'Фильтр'(Заказы SAP)", year));
        log.info("Год успешно выбран");
    }

    public void toggleOpened(boolean sate) {
        SSMToggle opened = this.filter.custom("toggle_opened", SSMToggle.class);
        opened.toggle(sate);
    }

    public void toggleClosed(boolean sate) {
        SSMToggle opened = this.filter.custom("toggle_closed", SSMToggle.class);
        opened.toggle(sate);
    }

    public void setClient(String client) {
        log.info("Выбор клиента в окне 'Фильтр'(Заказы SAP)");
        assertDoesNotThrow(
                () -> this.filter.edit("field_client").setValue(client),
                String.format("Не удалось вставить значение %s в поле 'клиент' окна 'Фильтр'(Заказы SAP)", client));
        log.info("Клиент успешно выбран");
    }

    public void clickOK() {
       assertDoesNotThrow(() -> this.filter.button("button_ok").click(), "Не удалось нажать кнопку 'OK' окна 'Фильтр'(Заказы SAP)");
    }

    public void clickCancel() {
        assertDoesNotThrow(() -> this.filter.button("button_cancel").click(), "Не удалось нажать кнопку 'Отмена' окна 'Фильтр'(Заказы SAP)");
    }

    public void refresh() {
        this.filter.findMySelf();
    }
}
