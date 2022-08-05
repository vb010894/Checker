package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

/**
 * JUNIT Skipped test case model.
 * @author vd.zinovev
 */
@Data
@Builder
@JacksonXmlRootElement(localName = "skipped")
public class SkippedModel {

    /**
     * Reason.
     */
    @JacksonXmlProperty(isAttribute = true)
    String message;
}