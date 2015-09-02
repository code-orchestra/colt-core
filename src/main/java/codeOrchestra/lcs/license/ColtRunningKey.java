package codeOrchestra.lcs.license;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class ColtRunningKey {
  
  public static void setRunning(boolean running) {
    Preferences prefs = Preferences.userNodeForPackage(ColtRunningKey.class);
    prefs.put("running", String.valueOf(running));   
    try {
      prefs.sync();
    } catch (BackingStoreException e) {
      // ignore
    }
  }

}
