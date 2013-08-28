package codeOrchestra.util.templates;

import codeOrchestra.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateProcessor {
  
  private File file;  
  private Map<String, String> tokens = new HashMap<>();
  
  public TemplateProcessor(File file, Map<String, String> tokens) {
    this.file = file;
    this.tokens = tokens;
  }
  
  public void process() throws IOException {
    String indexContent = FileUtils.read(file);
    
    for (String token : tokens.keySet()) {
      indexContent = indexContent.replace(token, tokens.get(token));      
    }
    
    FileUtils.write(file, indexContent);

  }

}
