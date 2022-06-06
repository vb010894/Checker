package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "test-case")
public class NUnitTestCase {

    @JacksonXmlProperty(isAttribute = true)
    String name;
    @JacksonXmlProperty(isAttribute = true)
    String executed;
    @JacksonXmlProperty(isAttribute = true)
    String success;
    @JacksonXmlProperty(isAttribute = true)
    String time;

    NUnitFailure failure;

    NUnitReason reason;

    @JacksonXmlElementWrapper(localName = "attachments")
    @JacksonXmlProperty(localName = "attachment")
    List<String> attachment;

}
