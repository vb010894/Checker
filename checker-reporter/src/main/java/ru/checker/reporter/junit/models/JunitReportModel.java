package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * JUNIT Report model.
 * @author vd.zinovev
 */
@JacksonXmlRootElement(localName = "testsuite")
@Data
@Builder
public class JunitReportModel {

    /**
     * XSI service field.
     */
    @JacksonXmlProperty(localName = "xmlns:xsi", isAttribute = true)
    private final String xsi = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * Schema location field.
     */
    @JacksonXmlProperty(localName = "xsi:noNamespaceSchemaLocation", isAttribute = true)
    private final String noNamespaceSchemaLocation = "https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report-3.0.xsd";

    /**
     * Schema version.
     */
    @JacksonXmlProperty(localName = "version", isAttribute = true)
    private final String version = "3.0";

    /**
     * Test suit name.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String name = "UNKNOWN";

    /**
     * Test case runtime.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String time = "0.000";

    /**
     * Total test count.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String tests = "0";

    /**
     * Errors count.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String errors = "0";

    /**
     * Skipped count.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String skipped = "0";

    /**
     * Failures count.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String failures = "0";

    /**
     * Test cases.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TestCaseModel> testcase;
}