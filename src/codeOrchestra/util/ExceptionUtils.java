package codeOrchestra.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;

/**
 * @author Alexander Eliseyev
 */
public final class ExceptionUtils {

  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
  
  public static boolean isBrokenPipe(IOException exception) {
    String message = exception.getMessage();
    if (message == null) {
      return false;
    }
    return message.contains("Broken pipe") || message.contains("Bad file descriptor");
  }

  public static boolean isSocketClosed(SocketException e) {
    return e.getMessage().equals("Socket closed");
  }

}
