package ru.checker.tests.base.application;

import lombok.Data;

/**
 * Application window setting.
 * @author vd.zinovev
 */
@Data
public class CheckerWindow {

    /**
     * Maximize application window
     */
    private boolean maximize = false;

    /**
     * Application widow width.
     * (if maximize is false).
     */
    private int width;

    /**
     * Application start height.
     * (if maximize is false).
     */
    private int height;

}
