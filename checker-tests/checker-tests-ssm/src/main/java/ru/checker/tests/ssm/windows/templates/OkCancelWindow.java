package ru.checker.tests.ssm.windows.templates;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.desktop.utils.CheckerDesktopMarker;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Шаблон с кнопками ok/отмена
 *
 * Функции:
 *
 * 1) Нажатие на кнопку "ОК"
 * 2) Нажатие на кнопку "Отмена"
 * 3) Обновление окна
 *
 * @author vd.zinovev
 *
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class OkCancelWindow extends RefreshableWindow {

    /**
     * Текущее окно.
     */
    @Getter
    CheckerDesktopWindow window;

    /**
     * Конструктор шаблона.
     * @param window Текущее окно
     */
    public OkCancelWindow(CheckerDesktopWindow window) {
        super(window);
        this.window = window;
    }

    /**
     * Нажатие на кнопку 'Ok'
     * В конфигурациях нужно добавить кнопку с ID - 'button_ok'
     */
    public void clickOK() {
        log.info("Нажатие кнопки 'ОК'");
        try {
            this.window.button("button_ok").click();
        } catch (Exception e) {
            log.warn("Не удалось нажать кнопку 'ОК' чер WinApi. Нажатие с помощью мыши");
            Rectangle rect = assertDoesNotThrow(
                    () -> this.window.button("button_ok").getBoundingRectangle().toRectangle(),
                    "Не удалось получить положение кнопки 'ОК'");
            new CheckerDesktopMarker(rect).draw();
            log.debug("Положение кнопки - '{}'", rect);
            AutomationMouse.getInstance().setLocation(rect.x + 10, rect.y + 10);
            log.debug("Нажатие на точку:\n x -'{}'\n y - '{}'", rect.x + 10, rect.y + 10);
            AutomationMouse.getInstance().leftClick();
        }
        log.info("Кнопка 'ОК' нажата");

    }

    /**
     * Нажатие на кнопку 'Cancel'
     * В конфигурациях нужно добавить кнопку с ID - 'button_cancel'
     */
    public void clickCancel() {
        log.info("Нажатие кнопки 'Отмена' окна 'Фильтр'");
        assertDoesNotThrow(() -> this.window.button("button_cancel").click(), "Не удалось нажать кнопку 'Отмена'");
        log.info("Кнопка 'Отмена' нажата");
    }
}
