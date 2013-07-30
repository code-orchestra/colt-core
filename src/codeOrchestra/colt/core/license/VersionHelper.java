package codeOrchestra.colt.core.license;

/**
 * @author Alexander Eliseyev
 */
public final class VersionHelper {

  private static final String VERSION_CODE_NAME = "COLT1.0.2";

  public static final boolean IS_RELEASE_VERSION = true;

  private VersionHelper() {
  }

  public static String getVersionCodeName() {
      return VERSION_CODE_NAME;
  }

}
