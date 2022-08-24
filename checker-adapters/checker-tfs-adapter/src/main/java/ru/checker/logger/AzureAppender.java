package ru.checker.logger;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.io.Serializable;

@Plugin(
        name = "CheckerAzureAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE
)
public class AzureAppender extends AbstractAppender {


    protected AzureAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent event) {
        Level level = event.getLevel();
        if (Level.WARN.equals(level)) {
            System.out.println("##vso[task.logissue type=warning;] " + event.getMessage());
        } else if (Level.ERROR.equals(level)) {
            System.out.println("##vso[task.logissue type=error;] " + event.getMessage());
        }
    }
}