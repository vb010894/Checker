package ru.checker.tests.base.application;

import lombok.Data;

import java.io.Closeable;

/**
 * Checker app base.
 * @author vd.zinovev
 *
 * Application config support formats:
 * 1) YAML
 */
@Data
public abstract class CheckerApplication implements Closeable, Runnable {

   public abstract String getName();
   public abstract void run();
   public abstract void close();


}
