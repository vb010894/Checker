package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

/**
 * NUNIT test suite model.
 * @author vd.zinovev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JacksonXmlRootElement(localName = "test-suite")
public class NUnitTestSuite {

    /**
     * Test suite ID.
     */
    @JacksonXmlProperty(isAttribute = true)
    String id = "0";

    /**
     * Test suite type.
     */
    @JacksonXmlProperty(isAttribute = true)
    String type = "TestSuite";

    /**
     * Test suite name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String name = "Suite 1";

    /**
     * Test suite full name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String fullname = "Suite 1";

    /**
     * Test cases total count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String testcasecount = "1";

    /**
     * Test case run state.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "runstate")
    String runState = "Runnable";

    /**
     * Test case result.
     */
    @JacksonXmlProperty(isAttribute = true)
    String result = "Passed";

    /**
     * Test case with status 'Passed' count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String passed = "0";

    /**
     * Test case total count.
     */
    @JacksonXmlProperty(isAttribute = true)
    String total;

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
     * Test cases.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "test-case")
    List<NUnitTestCase> cases = new LinkedList<>();

}
