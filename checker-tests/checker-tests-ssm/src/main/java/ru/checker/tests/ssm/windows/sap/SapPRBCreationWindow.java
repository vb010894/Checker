package ru.checker.tests.ssm.windows.sap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.EditBox;
import mmarquee.automation.controls.mouse.AutomationMouse;
import org.junit.jupiter.api.function.ThrowingSupplier;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.desktop.test.temp.CheckerDesktopTest;
import ru.checker.tests.ssm.controls.grid.SSMGrid;
import ru.checker.tests.ssm.windows.templates.OkCancelWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class SapPRBCreationWindow extends OkCancelWindow {

    @Getter
    final CheckerDesktopWindow prb;


    public SapPRBCreationWindow(CheckerDesktopWindow window) {
        super(window);
        this.prb = window;
    }

    /**
     * Получает таблицу 'Мастер'.
     * ID в конфигурации - 'prb_masters_grid'.
     *
     * @return Таблицу 'Мастер'.
     */
    public SSMGrid getMasterGrid() {
        log.info("Получение таблицы 'Мастер' окна 'Задание ПРБ - создание'");
        return this.prb.custom("prb_masters_grid", SSMGrid.class);
    }

    /**
     * Получает значение поля 'Планируемое'.
     * ID в конфигурации - 'prb_plan_count_field'.
     *
     * @return Значение поля 'Планируемое'
     */
    public String getPlanCountValue() {
        log.info("Получение значения поля 'Планируемое'");
        EditBox planField = this.prb.edit("prb_plan_count_field");
        String value = assertDoesNotThrow(planField::getValue, "Не удалось получить значение поля 'Планируемое'");
        log.info("Получено значение '{}' поля 'Планируемое'", value);
        return value;
    }

    /**
     * Задает значение поля 'Планируемое'.
     * ID в конфигурации - 'prb_plan_count_field'.
     *
     */
    public void setPlanCountValue(String value) {
        log.info("Получение значения поля 'Планируемое'");
        EditBox planField = this.prb.edit("prb_plan_count_field");

        Rectangle rect = assertDoesNotThrow(
                () -> planField.getBoundingRectangle().toRectangle(),
                String.format("Не удалось задать значение '%s' поля 'Планируемое'. Не удалось получить положение поля", value));
        this.setValueByKeyBoard(rect, value);

        assertEquals(this.getPlanCountValue(), value, "Значение поля 'Планируемое' несоответствует введенному '" + value + "'");
        log.info("Значение '{}' поля 'Планируемое' задано", value);
    }

    /**
     * Вызывает окно "Выбор Мастеров".
     * ID группы в конфигурациях - prb_master_group.
     *
     * @return Окно "Выбор Мастеров".
     */
    public SapPRBMasterCreationWindow callAddMaster() {
        log.info("Нажатие на кнопку 'Добавить' на панели 'Мастер'");
        log.debug("Получение расположения панели 'Мастер'");
        Rectangle rectangle = assertDoesNotThrow(
                () -> this.prb.panel("prb_master_group").getBoundingRectangle().toRectangle(),
                "Не удалось получить положение группы кнопок 'Мастер'");
        int x = rectangle.x + 5;
        int y = rectangle.y + 5;
        log.debug("Нажатие на кнопку 'Добавить' по координатам: x - {}, y - {}", x, y);
        AutomationMouse.getInstance().setLocation(x, y);
        AutomationMouse.getInstance().leftClick();
        log.info("Получение окна 'Выбор Мастеров'");
        SapPRBMasterCreationWindow master = CheckerDesktopTest.getCurrentApp().window("sap_order_prb_master_form", SapPRBMasterCreationWindow.class);
        log.info("Окно получено");
        return master;
    }


    private void setValueByKeyBoard(Rectangle controlRectangle, String value) {
        AutomationMouse.getInstance().setLocation((int) controlRectangle.getCenterX(), (int) controlRectangle.getCenterY());
        AutomationMouse.getInstance().leftClick();

        Robot robot = assertDoesNotThrow((ThrowingSupplier<Robot>) Robot::new, "Не удалось получить доступ к клавиатуре");
        int key = KeyStroke.getKeyStroke(value).getKeyCode();

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);

        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);

        robot.keyPress(key);
        robot.keyRelease(key);

    }
}
