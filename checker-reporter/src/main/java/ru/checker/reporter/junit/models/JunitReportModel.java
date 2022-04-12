package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JacksonXmlRootElement(localName = "testsuite")
@Data
@Builder
public class JunitReportModel {

    @JacksonXmlProperty(localName = "xmlns:xsi", isAttribute = true)
    private final String xsi = "http://www.w3.org/2001/XMLSchema-instance";

    @JacksonXmlProperty(localName = "xsi:noNamespaceSchemaLocation", isAttribute = true)
    private final String noNamespaceSchemaLocation = "https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report-3.0.xsd";

    @JacksonXmlProperty(localName = "version", isAttribute = true)
    private final String version = "3.0";

    @JacksonXmlProperty(isAttribute = true)
    private String name = "UNKNOWN";

    @JacksonXmlProperty(isAttribute = true)
    private String time = "0.000";

    @JacksonXmlProperty(isAttribute = true)
    private String tests = "0";

    @JacksonXmlProperty(isAttribute = true)
    private String errors = "0";

    @JacksonXmlProperty(isAttribute = true)
    private String skipped = "0";

    @JacksonXmlProperty(isAttribute = true)
    private String failures = "0";

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TestCaseModel> testcase;

}
