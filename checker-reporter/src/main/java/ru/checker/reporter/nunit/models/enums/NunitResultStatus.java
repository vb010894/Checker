package ru.checker.reporter.nunit.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Test case status.
 * @author vd.zinovev
 */
public enum NunitResultStatus {

    /**
     * Skipped.
     */
    @JsonProperty("Skipped")
    Skipped,
    /**
     * Passed.
     */
    @JsonProperty("Passed")
    Passed,

    /**
     * Failed.
     */
    @JsonProperty("Failed")
    Failed,

    /**
     * Failed.
     */
    @JsonProperty("Inconclusive")
    Inconclusive
}
