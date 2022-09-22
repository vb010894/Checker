package ru.checker.tests.base.logger;

import lombok.Getter;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Дополнение к основному логированию.
 *
 * Сохраняет статистику тестового случая.
 * @author vd.zinovev
 *
 */
@Plugin(
        name = "CheckerAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE
)
public class CheckerLogAppender extends AbstractAppender {

    @Getter
    public static Map<String, List<LogEvent>> logging = new HashMap<>();

    private String step  = "1";

    public static void clearStatistic() {
        logging = new HashMap<>();
    }


    protected CheckerLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent event) {
        Level level = event.getLevel();
        if (Level.INFO.equals(level)) {
            this.addInfo(event);
        } else if (
                Level.ERROR.equals(level)
                        || level.equals(Level.WARN)
                        || level.equals(Level.ERROR)
                        || level.equals(Level.FATAL)) {
            this.addEventToStatistic(event);
        }
    }

    private void addInfo(LogEvent event) {
        if(Pattern.compile("Шаг [0-9]]").matcher(event.getMessage().getFormat()).lookingAt())
            this.switchStep(event.getMessage().getFormat());
        else
            this.addEventToStatistic(event);
    }

    private void addEventToStatistic(LogEvent event) {
        List<LogEvent> log = logging.containsKey(this.step)
                ? logging.get(this.step)
                : new ArrayList<>();
        log.add(event);
        logging.put(this.step, log);
    }

    private void switchStep(String message) {
        String[] step = message.split("Шаг");
        if(step.length > 1)
            this.step = step[1].trim();
    }
}
