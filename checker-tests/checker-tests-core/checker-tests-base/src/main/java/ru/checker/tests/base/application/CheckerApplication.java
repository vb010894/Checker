package ru.checker.tests.base.application;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Closeable;

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

   /**
    * App name getter.
    * @return App name
    */
   public abstract String getName();

   /**
    * App run.
    */
   public abstract void run();

   /**
    * App close.
    */
   public abstract void close();
}