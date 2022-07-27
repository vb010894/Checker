package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

/**
 * JUNIT test case model.
 * @author vd.zinovev
 */
@Data
@Builder
@JacksonXmlRootElement(localName = "testcase")
public class TestCaseModel {

    /**
     * Test case name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /**
     * Test case class name.
     */
    @JacksonXmlProperty(localName = "classname", isAttribute = true)
    String className;

    /**
     * Test case runtime.
     */
    @JacksonXmlProperty(isAttribute = true)
    String time;

    /**
     * Test case error.
     */
    @JacksonXmlProperty(localName = "failure")
    ErrorModel error;

    /**
     * Test case skipped.
     */
    SkippedModel skipped;

    /**
     * Error message.
     */
    @JacksonXmlProperty(localName = "system-err")
    String stack;

    /**
     * Error stack trace.
     */
    @JacksonXmlProperty(localName = "stackTrace")
    String stackTrace;

    /**
     * Test case log.
     */
    @JacksonXmlProperty(localName = "system-out")
    String out;
}
