package codeOrchestra.util;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public final class NameUtil {

  public static String namespaceFromPath(String path) {
    return path.replace('/', '.').replace(File.separatorChar, '.');
  }
  
  public static String shortNameFromLongName(String fqName) {
    if (fqName == null) return fqName;
    int offset = fqName.lastIndexOf('.');
    if (offset < 0) return fqName;

    return fqName.substring(offset + 1);
  }
  
  public static String longNameFromNamespaceAndShortName(String namespace, String name) {
    if (StringUtils.isEmpty(namespace)) {
      return name;
    }
    return namespace + '.' + name;
  }
  
  public static boolean isEmpty(String command) {
    return command == null || command.trim().isEmpty();
  }

}
