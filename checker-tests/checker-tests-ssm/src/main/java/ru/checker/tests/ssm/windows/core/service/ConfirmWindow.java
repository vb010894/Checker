package ru.checker.tests.ssm.windows.core.service;

import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;
import ru.checker.tests.ssm.windows.core.templates.GetSetWindow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Окно подтверждения действия.
 * Файл конфигурации - /Windows/CORE/CONFIRM_WINDOW.yaml.
 * ID окна - ssm_core_confirm.
 *
 * @author vd.zinovev
 */
@Log4j2
@SuppressWarnings("unused")
public class ConfirmWindow extends GetSetWindow {


    /**
     * Конструктор.
     *
     * @param window Текущее окно
     */
    public ConfirmWindow(CheckerDesktopWindow window) {
        super(window);
    }

    /**
     * Нажатие кнопки "Да".
     * ID конфигурации - ssm_core_confirm_yes.
     */
    public void clickYes() {
        log.info("Нажатие кнопки 'Да' окна подтверждения");
        assertDoesNotThrow(
                () -> this.getButton("ssm_core_confirm_yes", "Да").click(),
                "Не удалось нажать кнопку 'ssm_core_confirm_yes. Да' окна подтверждения действия");
        log.info("Кнопка 'Да' окна подтверждения нажата");
    }

    /**
     * Нажатие кнопки "Нет".
     * ID конфигурации - ssm_core_confirm_no.
     */
    public void clickNo() {
        log.info("Нажатие кнопки 'Нет' окна подтверждения");
        assertDoesNotThrow(
                () -> this.getButton("ssm_core_confirm_no", "Нет").click(),
                "Не удалось нажать кнопку 'ssm_core_confirm_no. Нет' окна подтверждения действия");
        log.info("Кнопка 'Нет' окна подтверждения нажата");
    }
}
