package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "test-suite")
public class NUnitTestSuiteWrapper {

    @JacksonXmlProperty(isAttribute = true)
    String type = "Assembly";

    @JacksonXmlProperty(isAttribute = true)
    String id = "0";

    @JacksonXmlProperty(isAttribute = true)
    String name = "Run 1";

    @JacksonXmlProperty(isAttribute = true)
    String fullname = "Run 1";

    @JacksonXmlProperty(isAttribute = true)
    String testcasecount = "1";

    @JacksonXmlProperty(isAttribute = true, localName = "runstate")
    String runState = "Runnable";

    @JacksonXmlProperty(isAttribute = true)
    String result = "Passed";

    @JacksonXmlProperty(isAttribute = true)
    String total;

    @JacksonXmlProperty(isAttribute = true)
    String passed = "0";

    @JacksonXmlProperty(isAttribute = true)
    String failed = "0";

    @JacksonXmlProperty(isAttribute = true)
    String skipped = "0";

    @JacksonXmlProperty(isAttribute = true)
    String inconclusive = "0";

    @JacksonXmlProperty(isAttribute = true)
    String warnings = "0";

    @JacksonXmlProperty(isAttribute = true)
    String asserts = "0";

    @JacksonXmlProperty(isAttribute = true, localName = "start-time")
    String startTime;

    @JacksonXmlProperty(isAttribute = true, localName = "end-time")
    String endTime;

    @JacksonXmlProperty(isAttribute = true)
    String duration = "40";

    @JacksonXmlProperty(localName = "test-suite")
    NUnitTestSuite suite;

}