package ru.checker.tests.ssm.windows.sap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.toogle.SSMToggle;
import ru.checker.tests.ssm.windows.core.templates.OkCancelWindow;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SapFilterWindow extends OkCancelWindow {

    @Getter
    final CheckerDesktopWindow filter;


    public SapFilterWindow(CheckerDesktopWindow window) {
        super(window);
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
        log.info("Выбор значения {} в окне 'Фильтр'(Заказы SAP)", year);
        assertDoesNotThrow(
                () -> this.filter.edit("field_year").setValue(year),
                String.format("Не удалось вставить значение %s в поле 'Год' окна 'Фильтр'(Заказы SAP)", year));
        log.info("Год успешно выбран");
    }

    public void toggleOpened(boolean sate) {
        log.info("Переключение состояния переключателя 'Открытые' на {}", (sate ? "Включен" : "Выключен"));
        SSMToggle opened = this.filter.custom("toggle_opened", SSMToggle.class);
        opened.toggle(sate);
    }

    public void toggleClosed(boolean sate) {
        log.info("Переключение состояния переключателя 'Закрытые' на {}", (sate ? "Включен" : "Выключен"));
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

    public void clearClient() {
        log.info("Очистка поля 'Клиент' окна 'Фильтр'");
        assertDoesNotThrow(
                () -> {
                    EditBox client = this.filter.edit("field_client");
                    client.setValue("");
                    String val;
                    if(!(val = this.filter.edit("field_client").getValue()).equals(""))
                        throw new Exception("Фильтр 'Клиент' не был очищен. Текущее значение - '" + val + "'");
                },
                String.format("Не удалось получить значение поля 'клиент' окна 'Фильтр'(Заказы SAP) для проверки очистки"));
        log.info("Поле 'Клиент' очищено");
    }

    public void clearLotsmanOrder() {
        log.info("Очистка поля 'Заказ Лоцман' окна 'Фильтр'");
        assertDoesNotThrow(
                () -> {
                    EditBox client = this.filter.edit("field_lotsman_order");
                    Rectangle parent = UIAutomation.getInstance()
                            .getControlViewWalker()
                            .getParentElement(client.getElement())
                            .getBoundingRectangle()
                            .toRectangle();

                    AutomationMouse.getInstance().setLocation((int) parent.getMaxX() + 10, (int) parent.getCenterY());
                    AutomationMouse.getInstance().leftClick();
                    String val;
                    if(!(val = this.filter.edit("field_lotsman_order").getValue()).equals(""))
                        throw new Exception("Фильтр 'Заказ Лоцман' не был очищен. Текущее значение - '" + val + "'");
                },
                String.format("Не удалось получить значение поля 'Заказ Лоцман' окна 'Фильтр'(Заказы SAP) для проверки очистки"));
        log.info("Поле 'Заказ Лоцман' очищено");
    }

    /**
     * Вызов окна заказа Лоцман в окне 'Фильтр'
     * @return Окно заказа Лоцман
     */
    public SapLotsmanFilterWindow callLotsmanOrderWindow() {
        log.info("Вызов окна выбора заказа Лоцман в окне 'Фильтр'");
        Rectangle fieldRectangle = assertDoesNotThrow(
                () -> this.filter.edit("field_lotsman_order").getBoundingRectangle().toRectangle(),
                "Не удалось получить положение поля 'Заказ Лоцман'");
        AutomationMouse.getInstance().setLocation((int) (fieldRectangle.getMaxX() + 5), (int) fieldRectangle.getCenterY());
        AutomationMouse.getInstance().leftClick();

        log.info("Инициализация окна 'Заказы Лоцман'");
        SapLotsmanFilterWindow lotsmanFilterWindow = CheckerDesktopTest.getCurrentApp().window("SAP_FILTER_LOTSMAN_WINDOW", SapLotsmanFilterWindow.class);
        log.info("Окно инициализировано");
        return lotsmanFilterWindow;
    }
}
