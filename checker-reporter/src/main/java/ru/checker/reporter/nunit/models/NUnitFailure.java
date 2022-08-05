package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * NUNIT failure model.
 * @author vd.zinovev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "failure")
public class NUnitFailure {

    /**
     * Error message.
     */
    @JacksonXmlCData
    String message;

    /**
     * Error stack trace.
     */
    @JacksonXmlProperty(localName = "stack-trace")
    @JacksonXmlCData
    String stackTrace;
}