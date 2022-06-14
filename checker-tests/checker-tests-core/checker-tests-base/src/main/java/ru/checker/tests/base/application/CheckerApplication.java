package ru.checker.tests.base.application;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Closeable;
import java.util.Map;

/**
 * Checker app base.
 * @author vd.zinovev
 *
 * Application config support formats:
 * 1) YAML
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class CheckerApplication implements Closeable, Runnable {

   public abstract String getName();
   public abstract void run();
   public abstract void close();


}
