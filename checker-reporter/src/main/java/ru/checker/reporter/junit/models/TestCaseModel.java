package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JacksonXmlRootElement(localName = "testcase")
public class TestCaseModel {

    @JacksonXmlProperty(isAttribute = true)
    String name;

    @JacksonXmlProperty(localName = "classname", isAttribute = true)
    String className;

    @JacksonXmlProperty(isAttribute = true)
    String time = "0.000";


    @JacksonXmlProperty(localName = "failure")
    ErrorModel error;

    SkippedModel skipped;


    @JacksonXmlProperty(localName = "system-err")
    String stack;


}
