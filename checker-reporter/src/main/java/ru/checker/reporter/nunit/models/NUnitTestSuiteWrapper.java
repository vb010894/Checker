package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Test suite wrapper.
 * @author vd.zinovev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "test-suite")
public class NUnitTestSuiteWrapper {

    /**
     * Test suites type.
     */
    @JacksonXmlProperty(isAttribute = true)
    String type = "Assembly";

    /**
     * Test suites group id.
     */
    @JacksonXmlProperty(isAttribute = true)
    String id = "0";

    /**
     * Test suites group name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String name = "Run 1";

    /**
     * Test suites group full name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String fullname = "Run 1";

    /**
     * Test cases count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String testcasecount = "1";

    /**
     * Test suites run state.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "runstate")
    String runState = "Runnable";

    /**
     * Test suites result.
     */
    @JacksonXmlProperty(isAttribute = true)
    String result = "Passed";

    /**
     * Test case total count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String total;

    /**
     * Test case with status 'Passed' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String passed = "0";

    /**
     * Test case with status 'Failed' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String failed = "0";

    /**
     * Test case with status 'Skipped' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String skipped = "0";

    /**
     * Non-run test case count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String inconclusive = "0";

    /**
     * Test case warnings count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String warnings = "0";

    /**
     * Test case asserts count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String asserts = "0";

    /**
     * Test suite start time.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "start-time")
    String startTime;

    /**
     * Test suite end time.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "end-time")
    String endTime;

    /**
     * Test suite duration in ms.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "duration-ms")
    String duration = "40";

    /**
     * Nunit suite.
     */
    @JacksonXmlProperty(localName = "test-suite")
    NUnitTestSuite suite;

}
