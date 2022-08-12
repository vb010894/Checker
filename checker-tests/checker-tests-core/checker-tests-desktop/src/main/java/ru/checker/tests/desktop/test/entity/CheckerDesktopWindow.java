package ru.checker.tests.desktop.test.entity;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.Application;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import mmarquee.automation.controls.mouse.AutomationMouse;
import ru.checker.tests.base.utils.CheckerTools;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checker window class.
 *
 * @author vd.zinovev
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
@Getter
public class CheckerDesktopWindow extends CheckerBaseEntity<Window, Application> {

    /**
     * Form definitions.
     */
    final Map<String, Map<String, Object>> formsDefinitions = new HashMap<>();

    /**
     * Widget definition.
     */
    final Map<String, Map<String, Object>> widgetsDefinitions = new HashMap<>();

    /**
     * Constructor.
     *
     * @param root       Root control
     * @param definition Control definition
     */
    public CheckerDesktopWindow(Application root, Map<String, Object> definition) {
        super(root, definition);
        this.addChildrenDefinition("widgets", widgetsDefinitions);
        this.addChildrenDefinition("forms", formsDefinitions);
    }


    /**
     * Get window form.
     *
     * @param ID Form ID
     * @return Form
     */
    public CheckerDesktopForm form(String ID) {
        return this.form(ID, true);
    }

    public boolean checkFormExist(String ID) {
        CheckerDesktopForm form = new CheckerDesktopForm(this.getControl(), this.formsDefinitions.get(ID));
        form.setWaitTimeout(3);
        return form.findMySelf(false);
    }

    /**
     * Get window form.
     *
     * @param ID Form ID
     * @return Form
     */
    public CheckerDesktopForm form(String ID, boolean needToThrow) {
        assertTrue(formsDefinitions.containsKey(ID), String.format("Форма с ID - %s не описана", ID));
        CheckerDesktopForm form = new CheckerDesktopForm(this.getControl(), this.formsDefinitions.get(ID));
        return form.findMySelf(needToThrow) ? form : null;
    }

    /**
     * Get window form.
     *
     * @param ID Form ID
     * @return Form
     */
    public <F> F form(String ID, Class<F> wrapper) {
        assertTrue(formsDefinitions.containsKey(ID), String.format("Форма с ID - %s не описана", ID));
        CheckerDesktopForm form = new CheckerDesktopForm(this.getControl(), this.formsDefinitions.get(ID));
        form.findMySelf();
        return assertDoesNotThrow(() -> wrapper.getConstructor(CheckerDesktopForm.class).newInstance(form), "Не удалось обернуть форму с ID - " + ID);
    }

    /**
     * Get window widget.
     *
     * @param ID Widget ID
     * @return Widget
     */
    public <W> W widget(String ID, Class<W> wrapper) {
        assertTrue(widgetsDefinitions.containsKey(ID), String.format("Форма с ID - %s не описана", ID));
        CheckerDesktopWidget widget = new CheckerDesktopWidget(this.getControl(), this.widgetsDefinitions.get(ID));
        widget.findMySelf();
        return assertDoesNotThrow(() -> wrapper.getConstructor(CheckerDesktopWidget.class).newInstance(widget), "Не удалось обернуть виджет с ID - " + ID);
    }

    /**
     * Find and save self-control.
     */
    @Override
    public boolean findMySelf() {
        if (this.getDefinition().containsKey("deep")) {
            if (CheckerTools.castDefinition(this.getDefinition().get("deep"))) {
                AtomicReference<String> ID = new AtomicReference<>();
                AtomicReference<String> name = new AtomicReference<>();
                this.getIndicators(ID, name);
                this.setID(ID.get());
                this.setName(name.get());
                this.setControl(this.deepSearch());
                return true;
            }
        }
        return super.findMySelf();
    }

    private Window deepSearch() {

        Window window = null;
        Map<String, Object> search = CheckerTools.castDefinition(this.getDefinition().get("search"));
        String name = CheckerTools.castDefinition(search.getOrDefault("Name", null));
        String className = CheckerTools.castDefinition(search.getOrDefault("ClassName", null));
        boolean found = false;

        assertTrue(this.getDefinition().containsKey("search"), "Не найден ключ 'search'");

        assertTrue(
                search.containsKey("ClassName") || search.containsKey("Name"),
                String.format("Для глубокого поиска окна необходимо заполнить ключ %s или %s", "search -> ClassName", "search -> Name"));


        WinDef.HWND handle = null;
        int limit = this.getWaitTimeout();
        while (limit > 0 & !found) {
            handle = assertDoesNotThrow(() -> {
                Thread.sleep(1000);
                return User32.INSTANCE.FindWindow(className, name);
            }, "Не удалось дождаться окно");
            if (handle == null) {
                limit--;
            } else {
                WinDef.HWND h = handle;
                Element el = assertDoesNotThrow(() -> UIAutomation.getInstance().getElementFromHandle(h), "Не удалось получить элемент из handle");
                window = new Window(new ElementBuilder().element(el));
                try {
                    found = window.isEnabled();
                    if (!found) {
                        window = new Window(new ElementBuilder().element(el));
                        limit--;
                    } else {
                        break;
                    }
                } catch (AutomationException e) {
                    log.debug("Не удалось получить состояние окна. Повторная итерация поиска");
                } finally {
                    assertDoesNotThrow(() -> Thread.sleep(1000), "Не удалось подождать активности формы - " + this.getDefinition().get("id"));
                    limit--;
                }
            }
        }

        assertNotNull(handle, "Не найден handle окна. Окно не найдено");
        assertTrue(found, "Найденное окно не активно");
        assertNotNull(window, "Не удалось конвертировать в окно");

        return window;
    }


    /**
     * Maximize window.
     */
    public void maximize() {
        assertDoesNotThrow(() -> {
            if (!this.getControl().getCanMaximize()) {
                throw new IllegalStateException(String.format("Окно c ID - '%s', имя - '%s' не может быть развернуто", this.getID(), this.getName()));
            }
            this.getControl().maximize();
        });
    }

    /**
     * Minimize window.
     */
    public void minimize() {
        assertDoesNotThrow(() -> {
            if (!this.getControl().getCanMinimize()) {
                throw new IllegalStateException(String.format("Окно c ID - '%s', имя - '%s' не может быть свернуто", this.getID(), this.getName()));
            }
            this.getControl().minimize();
        });
    }

    /**
     * Move window.
     */
    public void location(int X, int Y) {
        assertDoesNotThrow(() -> {
            Rectangle rect = this.getControl().getBoundingRectangle().toRectangle();
            if (!User32.INSTANCE.MoveWindow(this.getControl().getNativeWindowHandle(), X, Y, rect.width, rect.height, true)) {
                throw new IllegalStateException(String.format("Окно c ID - '%s', имя - '%s' не может быть перенесено", this.getID(), this.getName()));
            }
        });
    }
}

