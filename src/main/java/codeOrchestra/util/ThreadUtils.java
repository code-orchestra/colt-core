package codeOrchestra.util;

import javafx.application.Platform;

/**
 * @author Alexander Eliseyev
 */
public final class ThreadUtils {

  public static void executeInFXThread(Runnable runnable) {
      if (Platform.isFxApplicationThread()) {
          runnable.run();
      } else {
          Platform.runLater(runnable);
      }
  }

  public static void sleep(long millis) {
    try { Thread.sleep(millis); } catch (InterruptedException e) {}
  }

}
