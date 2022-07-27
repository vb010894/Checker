package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * NUNIT skip reason model.
 * @author vd.zinovev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "reason")
public class NUnitReason {

    /**
     * Reason.
     */
    @JacksonXmlCData
    String message;
}