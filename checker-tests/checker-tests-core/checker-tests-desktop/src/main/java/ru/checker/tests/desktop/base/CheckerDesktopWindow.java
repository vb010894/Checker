package ru.checker.tests.desktop.base;

import com.sun.jna.ptr.PointerByReference;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationBase;
import mmarquee.automation.controls.Button;
import mmarquee.automation.controls.Window;
import ru.checker.tests.base.utils.CheckerTools;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Data
@Log4j2(topic = "TEST CASE")
@SuppressWarnings("unused")
public class CheckerDesktopWindow {

    String ID;
    String title;
    String className;
    Window window;
    long elementCount;
    Map<String, Object> definition;
    List<String> calculationLocators = List.of(
            "grid",
            "button",
            "combo",
            "list",
            "text",
            "input",
            "field",
            "radio",
            "check",
            "date",
            "calendar",
            "filter"
    );

    List<ControlType> calculationTypes = List.of(
            ControlType.List,
            ControlType.Button,
            ControlType.CheckBox,
            ControlType.DataGrid,
            ControlType.ComboBox,
            ControlType.Table,
            ControlType.RadioButton,
            ControlType.Tab,
            ControlType.Tree,
            ControlType.Text,
            ControlType.Calendar,
            ControlType.Document,
            ControlType.Edit,
            ControlType.Menu
    );

    Map<String, Object> elements = new LinkedHashMap<>();


    public CheckerDesktopWindow(Map<String, Object> definition) {
        this.definition = definition;
        assertTrue(this.definition.containsKey("id"), "Не задано ID окна приложения. Ключ 'id'");
        this.ID = (String) this.definition.get("id");
        log.info("Инициализация формы. ID - " + this.ID);
        assertTrue(
                this.definition.containsKey("title") || this.definition.containsKey("className"),
                "Должен быть задан один из ключей для поиска окна. Ключи - 'title', 'classname'");
        this.title = CheckerTools.castDefinition(this.definition.getOrDefault("title", null));
        this.className = CheckerTools.castDefinition(this.definition.getOrDefault("className", null));
        if(this.definition.containsKey("elements")) {
            List<Map<String, Object>> elements = CheckerTools.castDefinition(this.definition.get("elements"));
            elements.parallelStream().forEach(e -> {
                Map<String, Object> temp = CheckerTools.castDefinition(e.get("element"));
                assertTrue(temp.containsKey("id"), "Не задан ID для элемента");
                this.elements.put(CheckerTools.castDefinition(temp.get("id")), temp);
            });
        }
    }

    public void findWindow() {
        if(this.window == null) {
            if (title != null)
                this.window = assertDoesNotThrow(() -> CheckerDesktopApplication.getApplication().getWindow(this.title),
                        "Не удалось найти форму приложения по заголовку");
            else
                this.window = assertDoesNotThrow(() -> CheckerDesktopApplication.getApplication().getWindowByClassName(this.className),
                        "Не удалось найти форму приложения по имени класса");

            log.info("Инициализация прошла успешно");
            this.calculateDescendantElements();
        }
    }

    /**
     * Calculate elements, when the form is used.
     */
    private void calculateDescendantElements() {
        log.info("Подсчет элементов на форме {}", this.ID + "." + this.getName());
        this.elementCount = assertDoesNotThrow(() -> {
            PointerByReference ref = UIAutomation.getInstance().createTrueCondition();
            List<AutomationBase> controls = this.window.getChildren(true);
            return controls.parallelStream().filter(f -> {
                try {
                    return calculationLocators.parallelStream().anyMatch(f.getClassName().toLowerCase()::contains)
                            ||
                           calculationLocators.parallelStream().anyMatch(f.getName().toLowerCase()::contains)
                            ||
                            calculationTypes.contains(ControlType.fromValue(f.getElement().getControlType()));
                } catch (Exception ex) {
                    return false;
                }

            }).count();
        });
        log.info("На форме '{}' найдено '{}' элементов", this.ID + "." + this.getName(), this.elementCount);
    }

    public Rectangle getRectangle() {
        return assertDoesNotThrow(
                () -> this.window.getBoundingRectangle().toRectangle(),
                "Не удалось получить местоположение элемента. "
        );
    }

    public String getName() {
        if(this.definition.containsKey("name"))
            return CheckerTools.castDefinition(this.definition.get("name"));
        else
            return this.getOriginalName();
    }

    public String getOriginalName() {
        try {
            return this.window.getName();
        } catch (AutomationException e) {
            log.warn("Не удалось получить имя по аттрибуту элемента. Будет возвращено значение - ''");
            return "";
        }
    }

    public Button getButton(String id) {
        this.window.focus();
        assertTrue(this.elements.containsKey(id), "Не найден элемент с ID - " + id);
        CheckerDesktopControl<Button> control = new CheckerDesktopControl<>(CheckerTools.castDefinition(this.elements.get(id)));
        return control.findFirstControl(this.window);
    }

}
