package ru.checker.tests.desktop.test.app;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.ElementBuilder;
import mmarquee.automation.controls.Window;
import ru.checker.tests.base.test.app.CheckerWindow;
import ru.checker.tests.base.utils.CheckerTools;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Getter
@Setter
@Log4j2(topic = "TEST CASE")
@SuppressWarnings("unused")
public class CheckerDesktopWindow extends CheckerDesktopControl<Window> implements CheckerWindow {

    Window window;
    boolean isDeepSearching = false;

    Map<String, CheckerDesktopForm> forms = new LinkedHashMap<>();
    Map<String, CheckerDesktopWidget> widgets = new LinkedHashMap<>();

    /**
     * Constructor by definition path.
     *
     * @param PATH Definition path
     */
    public CheckerDesktopWindow(String PATH) {
        super(PATH);
        this.init();
    }

    /**
     * Constructor by definition.
     *
     * @param definition Control definition
     */
    public CheckerDesktopWindow(Map<String, Object> definition) {
        super(definition);
        this.init();
    }

    /**
     * Init method.
     */
    private void init() {
        this.createWidget();
        this.createForms();
        this.isDeepSearching = CheckerTools.castDefinition(this.getDefinition().getOrDefault("deep", false));
    }

    /**
     * Create window widgets.
     */
    private void createWidget() {
        if(this.getDefinition().containsKey("widgets")) {
            List<Map<String, Object>> widgets = CheckerTools.castDefinition(this.getDefinition().get("widgets"));
            widgets.parallelStream().forEach(widget -> {
                if(widget.containsKey("path")) {
                    String path = CheckerTools.castDefinition(widget.get("path"));
                    CheckerDesktopWidget wid = new CheckerDesktopWidget(path);
                    this.widgets.put(wid.getID(), wid);
                } else if (this.widgets.containsKey("widget")) {
                    Map<String, Object> definition = CheckerTools.castDefinition(widget.get("widget"));
                    CheckerDesktopWidget wid = new CheckerDesktopWidget(definition);
                    this.widgets.put(wid.getID(), wid);
                } else {
                    fail("В описании widget должны содержаться ключи 'path' (для отдельного файла) или 'widget' (для локального описания)");
                }
            });
        }
    }

    /**
     * Create window forms.
     */
    private void createForms() {
        if(this.getDefinition().containsKey("forms")) {
            List<Map<String, Object>> forms = CheckerTools.castDefinition(this.getDefinition().get("forms"));
            forms.parallelStream().forEach(form -> {
                if(form.containsKey("path")) {
                    String path = CheckerTools.castDefinition(form.get("path"));
                    CheckerDesktopForm frm = new CheckerDesktopForm(path);
                    this.forms.put(frm.getID(), frm);
                } else if (this.forms.containsKey("form")) {
                    Map<String, Object> definition = CheckerTools.castDefinition(form.get("form"));
                    CheckerDesktopForm frm = new CheckerDesktopForm(definition);
                    this.forms.put(frm.getID(), frm);
                } else {
                    fail("В описании Forms должны содержаться ключи 'path' (для отдельного файла) или 'form' (для локального описания)");
                }
            });
        }
    }

    /**
     * Create window control.
     * @param application Current application
     */
    public void createWindow(AutomationBase application) {
        this.createControl(application);
    }

    /**
     * Create window by index
     * @param application Current application
     * @param index Window index
     */
    public void createWindow(AutomationBase application, int index) {
        this.createControl(application, index);
    }

    /**
     * Find and create control.
     *
     * @param root Root element
     */
    @Override
    protected void createControl(AutomationBase root) {
        if(isDeepSearching) {
            assertDoesNotThrow(() -> {
                log.info("Поиск формы через глубокий поиск. ID -" + this.getID());
                String name = CheckerTools.castDefinition(this.getSearch().getOrDefault("Name", null));
                String className = CheckerTools.castDefinition(this.getSearch().getOrDefault("ClassName", null));
                WinDef.HWND handle = User32.INSTANCE.FindWindow(className, name);
                assertNotNull(handle, "Не найдено окно. ID - " + this.getID());
                Element window = UIAutomation
                        .getInstance()
                        .getElementFromHandle(handle);
                this.setControl(new Window(new ElementBuilder().element(window)));
                log.info("Поиск прошел успешно");
            }, "Не удалось найти форму по углубленному поиску. ID - " + this.getID());
        } else {
            super.createControl(root);
        }
        this.calculate();
    }

    /**
     * Get original control name from driver.
     *
     * @return Original name
     */
    @Override
    public String getOriginalName() {
        try {
            return "Window - " + this.getControl().getName();
        } catch (AutomationException e) {
            return "";
        }
    }

    /**
     * Get definition path.
     *
     * @return Definition path
     */
    @Override
    public String getDefinitionPath() {
        return CheckerWindow.super.getDefinitionPath();
    }

    /**
     * Get window form.
     *
     * @param ID Control ID.
     * @return Control
     */
    @Override
    public CheckerDesktopForm form(String ID) {
        assertTrue(this.forms.containsKey(ID), "Не далось найти форму с ID - " + ID);
        CheckerDesktopForm form = this.forms.get(ID);
        form.createControl(this.getControl());
        return form;
    }

    /**
     * Get window widget.
     *
     * @param ID Control ID.
     * @return Control
     */
    @Override
    public <T> T widget(String ID, Class<T> controller) {
        assertTrue(this.widgets.containsKey(ID), "Не найден виджет с ID - " + ID);
        CheckerDesktopWidget widget = this.widgets.get(ID);
        widget.createControl(this.getControl());

        return assertDoesNotThrow(() ->
                        controller.getConstructor(CheckerDesktopWidget.class).newInstance(widget),
                "Не удалось создать экземпляр контроллера");
    }

    /**
     * Maximize window.
     */
    public void maximize() {
        assertNotNull(this.getControl(), "Элемент не инициализирован. Вызовете метод 'createControl' после создания. ID - " + this.getID());
        boolean canMax = assertDoesNotThrow(() -> this.getControl().getCanMaximize(), "Не возможно развернуть окно. Окно - " + this.getOutName());
        assertTrue(canMax, "У окна нет возможности развернуться. Окно - " + this.getOutName());
        assertDoesNotThrow(() -> this.getControl().maximize(), "Не возможно развернуть окно. Окно - " + this.getOutName());
    }

    /**
     * Move window.
     * @param x New location X
     * @param y New location Y
     */
    public void move(int x, int y) {
        assertDoesNotThrow(() -> {
            Rectangle windowRectangle = this.window.getBoundingRectangle().toRectangle();
            assertTrue(
                    User32.INSTANCE.MoveWindow(this.window.getNativeWindowHandle(), x, y, windowRectangle.width, windowRectangle.height, true),
                    "Не удалось переместить окно. Окно - " + this.getOutName());
        }, "Не удалось переместить окно. Окно - " + this.getOutName());
    }

}
