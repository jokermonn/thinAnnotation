package com.joker.buildsrc;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class Log {
  private static final Logger logger = Logging.getLogger(ThinAnnotationTask.class);

  public static void log(String s) {
    logger.info(s);
  }
}
