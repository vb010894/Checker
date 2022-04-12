package ru.checker.tests.base.enums;

import lombok.Getter;

/**
 * Checker tesseract language.
 * @author vd.zinovev
 *
 * !!! To add another language,
 * you need to follow these steps:
 *
 * 1) Add '*.traineddata', where '*' - language prefix,
 * to folder - {root}/configs/tesseract
 * (link for download - https://github.com/tesseract-ocr/tessdata),
 *
 * 2) Add enum and language prefix to this enum.
 *
 * 3) Use this enum in code.
 * @see ru.checker.tests.base.utils.CheckerOCRUtils - for usage.
 */
public enum CheckerOCRLanguage {

    /**
     * Russian language.
     */
    RUS("rus"),

    /**
     * English language.
     */
    ENG("eng");

    /**
     * Enum value.
     */
    @Getter
    private String value;

    /**
     * Enum constructor.
     * @param value Enum value
     */
    CheckerOCRLanguage(String value) {
        this.value = value;
    }
}
