package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "test-result")
public class NUnitTestResults {

    @JacksonXmlProperty(isAttribute = true)
    String name;
    @JacksonXmlProperty(isAttribute = true)
    String total;
    @JacksonXmlProperty(isAttribute = true)
    String failures;
    @JacksonXmlProperty(isAttribute = true, localName = "not-run")
    String notRun;
    @JacksonXmlProperty(isAttribute = true)
    String date;
    @JacksonXmlProperty(isAttribute = true)
    String time;

    @JacksonXmlProperty(localName = "test-suite")
    List<NUnitTestSuite> suites = new LinkedList<>();

}
