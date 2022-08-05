package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Nunit test run model.
 * @author vd.zinovev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "test-run")
public class NUnitTestRun {

    /**
     * Test run ID.
     */
    @JacksonXmlProperty(isAttribute = true)
    String id = "0";

    /**
     * NUNIT engine version.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "engine-version")
    String engineVersion = "3.11.1.0";

    /**
     * NUNIT clr version.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "clr-version")
    String clrVersion = "4.0.30319.42000";

    /**
     * Test run name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String name = "Checker.jar";

    /**
     * Test run full name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String fullname = "Checker tests";

    /**
     * Test cases count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String testcasecount = "1";

    /**
     * Test run status.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "runstate")
    String runState = "Runnable";

    /**
     * Test run result.
     */
    @JacksonXmlProperty(isAttribute = true)
    String result = "Passed";

    /**
     * Test cases count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String total;

    /**
     * Test cases with status 'Passed' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String passed = "0";

    /**
     * Test cases with status 'Failed' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String failed = "0";

    /**
     * Test cases with status 'Skipped' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String skipped = "0";

    /**
     * Test cases with status 'Not Run' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String inconclusive = "0";

    /**
     * Test cases warning count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String warnings = "0";

    /**
     * Test cases asserts.
     */
    @JacksonXmlProperty(isAttribute = true)
    String asserts = "0";

    /**
     * Test run start time.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "start-time")
    String startTime;

    /**
     * Test run end time.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "end-time")
    String endTime;

    /**
     * Test run duration in ms.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "duration-ms")
    String duration = "40";

    /**
     * Test suites wrapper.
     */
    @JacksonXmlProperty(localName = "test-suite")
    NUnitTestSuiteWrapper suiteWrapper;
}