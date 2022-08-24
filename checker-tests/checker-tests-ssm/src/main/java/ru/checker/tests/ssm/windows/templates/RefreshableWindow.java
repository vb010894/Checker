package ru.checker.tests.ssm.windows.templates;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;

import java.awt.*;

/**
 * Шаблон обновляемого окна.
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class RefreshableWindow {

    /**
     * Текущее окно.
     */
    @Getter
    CheckerDesktopWindow window;

    /**
     * Конструктор шаблона.
     * @param window Текущее окно
     */
    public RefreshableWindow(CheckerDesktopWindow window) {
        this.window = window;
    }

    /**
     * Обновление окна с включенным софт режимом.
     */
    public void refresh() {
        this.refresh(true);
    }

    /**
     * Обновление окна с настройкой софт режима.
     * При включенном софт режиме выполняется проверка активности окна
     * и если оно активно действия продолжаются с активным окном.
     * Если же отключено, то получается новый экземпляр формы.
     *
     * @param isSoft Софт режим включен/выключен
     */
    public void refresh(boolean isSoft) {
        log.debug("Обновление окна '{}.{}'", this.window.getID(), this.window.getName());
        try {
            if(!isSoft)
                throw new Exception("Софт обновление окна Выключено. Получение нового экземпляра");
            if(window.getControl().isEnabled())
                log.debug("Окно активно. Продолжение теста с активным окном");
            else
                throw new IllegalStateException("Окно не активно получение нового экземпляра");
        } catch (Exception e) {
            log.debug(e.getMessage());
            window.findMySelf();
        }
        log.debug("Окно успешно обновлено");
    }
}
