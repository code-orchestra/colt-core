package codeOrchestra.util;

import codeOrchestra.colt.core.logging.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class BrowserUtil {

  private static final String FILE = "file";
  private static final String JAR = "jar";
  private static final String MAILTO = "mailto";

  private static final Logger LOG = Logger.getLogger("#" + BrowserUtil.class.getName());

  // The pattern for 'scheme' mainly according to RFC1738.
  // We have to violate the RFC since we need to distinguish
  // real schemes from local Windows paths; The only difference
  // with RFC is that we do not allow schemes with length=1 (in other case
  // local paths like "C:/temp/index.html" whould be erroneously interpreted as
  // external URLs.)
  private static final Pattern ourExternalPrefix = Pattern.compile("^[\\w\\+\\.\\-]{2,}:");

  private BrowserUtil() {
  }

  public static boolean isAbsoluteURL(String url) {
    return ourExternalPrefix.matcher(url.toLowerCase()).find();
  }

  public static URL getURL(String url) throws java.net.MalformedURLException {
    if (!isAbsoluteURL(url)) {
      return new URL("file", "", url);
    }

    return convertToURL(url);
  }

  private static ProcessBuilder launchBrowser(final String url, String[] command) {
    try {
      URL curl = BrowserUtil.getURL(url);

      if (curl != null) {
        final String urlString = curl.toString();
        String[] commandLine;
        if (SystemInfo.isWindows) {
        	commandLine = new String[] { String.format("cmd /C \"start %1s\"", escapeUrl_(urlString)) };
        } else {
          commandLine = new String[command.length + 1];
          System.arraycopy(command, 0, commandLine, 0, command.length);
          commandLine[commandLine.length - 1] = escapeUrl(urlString);
        }
        return new ProcessBuilder(commandLine);
      }
    } catch (final IOException e) {
//      showErrorMessage("Can't start a browser", "Error");
    }
    return null;
  }

  public static ProcessBuilder launchBrowser(final String url, boolean multiple) {
    return launchBrowser(url, getDefaultBrowserCommand(multiple));
  }

  public static String escapeUrl(String url) {
    if (SystemInfo.isWindows) {
      return "\"" + url + "\"";
    } else {
      return url.replaceAll(" ", "%20");
    }
  }

  public static String escapeUrl_(String url) {
	  return url.replaceAll(" ", "%20");
  }

  private static String[] getDefaultBrowserCommand(boolean multiple) {
    if (SystemInfo.isWindows9x) {
      return new String[] { "command.com", "/c", "start" };
    }
    if (SystemInfo.isWindows) {
      return new String[] { "cmd.exe", "/c", "start" };
    }
    if (SystemInfo.isMac) {
      return multiple ? new String[] { "open", "-n" } : new String[] { "open" };
    }
    if (SystemInfo.isUnix) {
      return new String[] { "xdg-open" };
    }
    return null;
  }

  /**
   * Converts VsfUrl info java.net.URL. Does not support "jar:" protocol.
   * 
   * @param vfsUrl
   *          VFS url (as constructed by VfsFile.getUrl())
   * @return converted URL or null if error has occured
   */
  public static URL convertToURL(String vfsUrl) {
    if (vfsUrl.startsWith(JAR)) {
      LOG.error("jar: protocol not supported.");
      return null;
    }

    // [stathik] for supporting mail URLs in Plugin Manager
    if (vfsUrl.startsWith(MAILTO)) {
      try {
        return new URL(vfsUrl);
      } catch (MalformedURLException e) {
        return null;
      }
    }

    String[] split = vfsUrl.split("://");

    if (split.length != 2) {
      LOG.debug("Malformed VFS URL: " + vfsUrl);
      return null;
    }

    String protocol = split[0];
    String path = split[1];

    try {
      if (protocol.equals(FILE)) {
        return new URL(protocol, "", path);
      }
      return new URL(vfsUrl);
    } catch (MalformedURLException e) {
      LOG.debug("MalformedURLException occured:" + e.getMessage());
      return null;
    }
  }

}