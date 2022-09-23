package ru.checker.tests.ssm.windows.task;

import lombok.extern.log4j.Log4j2;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.desktop.utils.CheckerDesktopManipulator;
import ru.checker.tests.ssm.controls.toogle.SSMToggle;
import ru.checker.tests.ssm.windows.core.templates.OkCancelWindow;
import ru.checker.tests.ssm.windows.core.templates.RefreshableWindow;
import ru.checker.tests.ssm.windows.sap.SapLotsmanFilterWindow;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Окно фильтрации модуля "Управления заданиями".
 *
 * Файл конфигурации - "TASK/TASK_FILTER.yaml"
 * ID - "TASK_FILTER_FORM"
 */
@Log4j2
@SuppressWarnings("unused")
public class TaskFilter extends OkCancelWindow {

    /**
     * Конструктор.
     *
     * @param window Текущее окно
     */
    public TaskFilter(CheckerDesktopWindow window) {
        super(window);
    }

    /**
     * Получает значение поля "Год с".
     * @return Год с
     */
    public String getYearsFromValue() {
        return this.getValueField("field_year_from", "Год с");
    }

    /**
     * Задает значение поля "Год с"
     * @param value Значение Год с
     */
    public void setYearsFromValue(String value) {
        this.setValueField(value, "field_year_from", "Год с");
    }

    /**
     * Получает значение поля "Год по".
     * @return Год по
     */
    public String getYearsToValue() {
        return this.getValueField("field_year_to", "Год по");
    }

    /**
     * Задает значение поля "Год по"
     * @param value Значение Год по
     */
    public void setYearsToValue(String value) {
        this.setValueField(value, "field_year_to", "Год по");
    }

    /**
     * Получает значение поля "Цех".
     * @return Цех
     */
    public String getShopValue() {
        return this.getValueField("field_shop", "Цех");
    }

    /**
     * Задает значение поля "Цех"
     * @param value Значение Цех
     */
    public void setShopValue(String value) {
        this.setValueField(value, "field_shop", "Цех");
    }

    /**
     * Переключение поля "Мастер"
     * @param state Состояние переключателя
     */
    public void toggleMaster(boolean state) {
        this.changeToggle("toggle_master", "Мастер", state);
    }

    /**
     * Переключение поля "Закрытые"
     * @param state Состояние переключателя
     */
    public void toggleClosed(boolean state) {
        this.changeToggle("toggle_closed", "Закрытые", state);
    }

    /**
     * Переключение поля "В работе"
     * @param state Состояние переключателя
     */
    public void toggleInWork(boolean state) {
        this.changeToggle("toggle_inwork", "В работе", state);
    }

    /**
     * Переключение поля "Новые"
     * @param state Состояние переключателя
     */
    public void toggleNew(boolean state) {
        this.changeToggle("toggle_new", "Новые", state);
    }

    /**
     * Очищает поле "Заказ Лоцман"
     */
    public void clearLotsmanOrder() {
        log.info("Очистка поля 'Заказ Лоцман' окна 'Фильтр'");
        assertDoesNotThrow(
                () -> {
                    EditBox client = this.getFiled("field_lotsman_order", "Заказ Лоцман");
                    Rectangle parent = UIAutomation.getInstance()
                            .getControlViewWalker()
                            .getParentElement(client.getElement())
                            .getBoundingRectangle()
                            .toRectangle();

                    AutomationMouse.getInstance().setLocation((int) parent.getMaxX() + 10, (int) parent.getCenterY());
                    AutomationMouse.getInstance().leftClick();
                    String val;
                    if(!(val = this.getFiled("field_lotsman_order", "Заказ Лоцман").getValue()).equals(""))
                        throw new Exception("Фильтр 'Заказ Лоцман' не был очищен. Текущее значение - '" + val + "'");
                },
                "Не удалось получить значение поля 'Заказ Лоцман' окна 'Фильтр'(Управление заданиями) для проверки очистки");
        log.info("Поле 'Заказ Лоцман' очищено");
    }

    /**
     * Вызов окна заказа Лоцман в окне 'Фильтр'
     * @return Окно заказа Лоцман
     */
    public SapLotsmanFilterWindow callLotsmanOrderWindow() {
        log.info("Вызов окна выбора заказа Лоцман в окне 'Фильтр'");
        Rectangle fieldRectangle = assertDoesNotThrow(
                () -> this.getFiled("field_lotsman_order", "Заказ Лоцман").getBoundingRectangle().toRectangle(),
                "Не удалось получить положение поля 'Заказ Лоцман'. Модуль 'Управление заданиями'");
        CheckerDesktopManipulator.Mouse.click((int) (fieldRectangle.getMaxX() + 5), (int) fieldRectangle.getCenterY());

        log.info("Инициализация окна 'Заказы Лоцман'");
        SapLotsmanFilterWindow lotsmanFilterWindow = CheckerDesktopTest.getCurrentApp().window("SAP_FILTER_LOTSMAN_WINDOW", SapLotsmanFilterWindow.class);
        log.info("Окно инициализировано");
        return lotsmanFilterWindow;
    }

    /**
     * Переключает полена "активен/ не активен".
     * @param ID  ID поля
     * @param name Имя поля
     * @param state Состояние переключателя
     */
    public void changeToggle(String ID, String name, boolean state) {
        log.info("Переключение поля '{}. {}' на '{}'", ID, name, (state ? "Активен" : "Не активен"));
        SSMToggle toggle = this.getWindow().custom(ID, SSMToggle.class);
        toggle.toggle(state);
        log.info("Переключение поля '{}.{}' выполнено", ID, name);
    }
}
