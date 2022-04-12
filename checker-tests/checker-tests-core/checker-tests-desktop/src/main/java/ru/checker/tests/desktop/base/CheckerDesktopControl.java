package ru.checker.tests.desktop.base;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.AutomationException;
import mmarquee.automation.PropertyID;
import mmarquee.automation.controls.Window;
import ru.checker.tests.base.utils.CheckerTools;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Getter
@Setter
@Log4j2
public class CheckerDesktopControl<T> {

    String ID;
    String name;
    Map<String, Object> definition;
    Map<String, Object> search;

    public CheckerDesktopControl(Map<String, Object> definition) {
        this.definition = definition;
        assertTrue(this.definition.containsKey("id"), "Для элемента не задано ID. Ключ - 'id'");
        assertTrue(this.definition.containsKey("search"), "Не найдены поисковые локаторы для элемента. Ключ - 'search'");
        this.search = CheckerTools.castDefinition(definition.get("search"));
    }

    public T findFirstControl(Window window) {
        return this.findControlByIndex(window, 0);
    }

    public T findControlByIndex(Window window, int index) {
        List<T> controls = this.findControl(window);
        assertNotNull(controls, "Не найдено ни одного элемента по заданным кретериям поиска. ID - " + this.ID);
        assertTrue(controls.isEmpty(), "Не найдено ни одного элемента по заданным кретериям поиска. ID - " + this.ID);
        return assertDoesNotThrow(
                () -> controls.get(index),
                String.format("Элемента с индексом '%d' не существует", index));
    }

    @SuppressWarnings("unchecked")
    public List<T> findControl(Window window) {
       return assertDoesNotThrow(() -> window.getChildren(true)
               .parallelStream()
               .filter(f -> this.search
                       .entrySet()
                       .parallelStream()
                       .allMatch(
                               entry -> {
                                   try {
                                       return Objects.equals(
                                               f.getElement()
                                                       .getPropertyValue(
                                                               PropertyID.valueOf(entry.getKey()).getValue()),
                                                               entry.getValue());
                                   } catch (AutomationException e) {
                                       return false;
                                   }
                               }))
               .map(m -> (T) CheckerTools.castDefinition(m))
               .collect(Collectors.toList()), "Не удалось найти элемент. ID - %s, name - %s ");
    }


}
