package ru.checker.tests.ssm.windows.core.templates;

import com.sun.jna.platform.win32.User32;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.UIAutomation;
import org.junit.jupiter.api.Assertions;
import ru.checker.tests.desktop.test.entity.CheckerDesktopWindow;

/**
 * Шаблон обновляемого окна.
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class RefreshableWindow extends GetSetWindow {


    /**
     * Конструктор шаблона.
     * @param window Текущее окно
     */
    public RefreshableWindow(CheckerDesktopWindow window) {
        super(window);
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
            if(this.isEnabled())
                log.debug("Окно активно. Продолжение теста с активным окном");
            else
                throw new IllegalStateException("Окно не активно получение нового экземпляра");
        } catch (Exception e) {
            log.debug(e.getMessage());
            window.findMySelf();
        }
        log.debug("Окно успешно обновлено");
    }

    /**
     * Получает активность окна.
     * @return Активно/ Неактивно
     */
    public boolean isEnabled() {
        log.debug("Получение активности окна.");
        boolean result;
        try {
            Thread.sleep(1000);
            result =  User32.INSTANCE.IsWindowVisible(window.getControl().getNativeWindowHandle());
        } catch (Exception ex) {
            result = false;
        }
        log.debug("Окно {}", (result ? "Активно" : "Неактивно"));
        return result;
    }

    /**
     * Проверка активности окна.
     * @param state Состояние окна
     */
    public void checkActivity(boolean state) {
        log.debug("Проверка активности окна '{}. {}'", this.window.getID(), this.window.getName());
        Assertions.assertEquals(
                this.isEnabled(),
                state,
                String.format("Окно '%s. %s' %s, при ожидании - '%s'",
                        this.window.getID(),
                        this.window.getName(),
                        (this.isEnabled() ? "активен" : "неактивен"),
                        (state ? "активен" : "неактивен")));
        log.debug("Проверка прошла успешно");

    }
}
