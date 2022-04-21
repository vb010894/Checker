package ru.checker.reporter.junit.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JacksonXmlRootElement(localName = "failure")
public class ErrorModel {

    @JacksonXmlProperty(isAttribute = true)
    String message;

    @JacksonXmlProperty(isAttribute = true)
    String type;

    @JacksonXmlText
    @JacksonXmlCData
    String data;

}
