package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Builder;
import lombok.Data;

/**
 * JUNIT Error model.
 * @author vd.zinovev
 */
@Data
@Builder
@JacksonXmlRootElement(localName = "failure")
public class ErrorModel {

    /**
     * Error message.
     */
    @JacksonXmlProperty(isAttribute = true)
    String message;

    /**
     * Error type.
     */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /**
     * Error data.
     */
    @JacksonXmlText
    @JacksonXmlCData
    String data;
}