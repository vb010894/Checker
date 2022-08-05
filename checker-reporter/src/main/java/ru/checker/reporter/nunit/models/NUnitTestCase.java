package ru.checker.reporter.nunit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.checker.reporter.nunit.models.enums.NunitResultStatus;

import java.util.List;

/**
 * NUNIT est case model.
 * @author vd.zinovev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NUnitTestCase {

    /**
     * Test case ID.
     */
    @JacksonXmlProperty(isAttribute = true)
    String id = "0";

    /**
     * Test case name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String name = "case 1";

    /**
     * Test case full name.
     */
    @JacksonXmlProperty(isAttribute = true)
    String fullname = "case 1";

    /**
     * Test case method.
     */
    @JacksonXmlProperty(isAttribute = true)
    String methodname = "method 1";

    /**
     * Test case run state.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "runstate")
    String runState = "Runnable";

    /**
     * Test case state
     */
    @JacksonXmlProperty(isAttribute = true)
    NunitResultStatus result = NunitResultStatus.Passed;

    /**
     * Asserts.
     */
    @JacksonXmlProperty(isAttribute = true)
    String asserts = "0";

    /**
     * Test case duration in ms.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "duration-ms")
    String duration = "40";

    /**
     * Test case classname.
     */
    @JacksonXmlProperty(isAttribute = true)
    String classname = "";

    /**
     * Test case failure.
     * If existed.
     */
    NUnitFailure failure;

    /**
     * Test case skip reason.
     * If existed.
     */
    NUnitReason reason;

    /**
     * Test case attachment.
     */
    @JacksonXmlElementWrapper(localName = "attachments")
    List<NUnitAttachments> attachment;
}