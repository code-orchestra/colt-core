package codeOrchestra.util.templates;

import codeOrchestra.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class TemplateCopyUtil {
  
  public static void copy(File templateFile, File targetFile, Map<String, String> replacements) throws IOException {
    FileUtils.copyFileChecked(templateFile, targetFile, false);
    new TemplateProcessor(targetFile, replacements).process();
  }

}
