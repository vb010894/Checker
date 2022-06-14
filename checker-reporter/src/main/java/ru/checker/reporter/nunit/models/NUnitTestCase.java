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
public class NUnitTestCase {

    @JacksonXmlProperty(isAttribute = true)
    String id = "0";

    @JacksonXmlProperty(isAttribute = true)
    String name = "case 1";

    @JacksonXmlProperty(isAttribute = true)
    String fullname = "case 1";

    @JacksonXmlProperty(isAttribute = true)
    String methodname = "method 1";

    @JacksonXmlProperty(isAttribute = true, localName = "runstate")
    String runState = "Runnable";

    @JacksonXmlProperty(isAttribute = true)
    String result = "Passed";

    @JacksonXmlProperty(isAttribute = true)
    String asserts = "0";

    @JacksonXmlProperty(isAttribute = true, localName = "duration-ms")
    String duration = "40";

    @JacksonXmlProperty(isAttribute = true)
    String classname = "";

    NUnitFailure failure;

    NUnitReason reason;

    @JacksonXmlElementWrapper(localName = "attachments")
    List<NUnitAttachments> attachment;

}
