package ru.checker.tests.ssm.controls.toogle;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.controls.Panel;
import mmarquee.automation.controls.mouse.AutomationMouse;
import org.junit.jupiter.api.function.Executable;

import java.awt.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SSMToggle {

    Panel control;
    Map<String, Object> definition;
    Color FILTER_INDICATOR = new Color(0, 93, 163);

    public SSMToggle(Panel control, Map<String, Object> definition) {
        this.control = control;
        this.definition = definition;
    }

    public void toggle(boolean state) {
        Rectangle rectangle = assertDoesNotThrow(
                () -> this.control.getBoundingRectangle().toRectangle(),
                "Не удалось получить расположение переключателя с именем - "
                        + this.definition.getOrDefault("name", ""));
        Color current_state_color = assertDoesNotThrow(
                () -> new Robot().getPixelColor( rectangle.x + 10, (int) rectangle.getCenterY()),
                "Не удалось получить цвет-индикатор состояния переключателя c именем - " + this.definition.getOrDefault("name", ""));

        if(current_state_color.equals(FILTER_INDICATOR) && !state) {
            log.info("Выключение переключателя с именем - '{}'", this.definition.getOrDefault("name", ""));
            AutomationMouse.getInstance().setLocation(rectangle.x + 10, rectangle.y + 10);
            AutomationMouse.getInstance().leftClick();
            assertDoesNotThrow(() -> {
                Thread.sleep(1000);
                if(new Robot().getPixelColor(rectangle.x + 10, rectangle.y + 10).equals(FILTER_INDICATOR))
                    throw new IllegalStateException(String.format("Состояние переключателя '%s' не изменилось", this.definition.getOrDefault("name", "")));
            }, "Не удалось выполнить переключение");
            return;
        }

        if(!current_state_color.equals(FILTER_INDICATOR) && state) {
            log.info("Включение переключателя с именем - '{}'", this.definition.getOrDefault("name", ""));
            AutomationMouse.getInstance().setLocation(rectangle.x + 10, rectangle.y + 10);
            AutomationMouse.getInstance().leftClick();
            assertDoesNotThrow(() -> {
                Thread.sleep(1000);
                Color test = new Robot().getPixelColor(rectangle.x + 10, rectangle.y + 10);
                if(!new Robot().getPixelColor(rectangle.x + 10, rectangle.y + 10).equals(FILTER_INDICATOR))
                    throw new IllegalStateException(String.format("Состояние переключателя '%s' не изменилось", this.definition.getOrDefault("name", "")));
            }, "Не удалось выполнить переключение");
            return;
        }

        log.info(
                "Состояние переключателя с именем {} соответствует состоянию {}",
                this.definition.getOrDefault("name", ""),
                ((state) ? "Активен" : "Выключен"));
    }

    public boolean getSate() {
        Rectangle rectangle = assertDoesNotThrow(
                () -> this.control.getBoundingRectangle().toRectangle(),
                "Не удалось получить расположение переключателя с именем - "
                        + this.definition.getOrDefault("name", ""));
        return assertDoesNotThrow(() ->  new Robot().getPixelColor((int) rectangle.getCenterX(), rectangle.y + 20).equals(FILTER_INDICATOR)
                , "Не удалось получить состояние переключателя с именем - " + this.definition.getOrDefault("name", ""));
    }



}
