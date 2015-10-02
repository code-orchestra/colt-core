package codeOrchestra.util.process;

import codeOrchestra.colt.core.execution.ExecutionException;
import codeOrchestra.colt.core.execution.ProcessHandler;
import codeOrchestra.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessHandlerBuilder {
  private final List<String> myCommandLine = new ArrayList<>();

  public ProcessHandlerBuilder() {
  }

  public List<String> getCommandLine() {
    return myCommandLine;
  }

  public ProcessHandlerBuilder append(String command) {
    if (!(StringUtils.isEmpty(command))) {
      for (String part : splitCommandInParts(command)) {
        myCommandLine.add(part);
      }
    }
    return this;
  }

  public ProcessHandlerBuilder appendKey(String key, String parameter) {
    if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(parameter)) {
      return append("-" + key).append(parameter);
    }
    return this;
  }

  public ProcessHandler build() throws ExecutionException {
    return build(new File(System.getProperty("user.dir")));
  }

  public ProcessHandler build(File workingDirectory) throws ExecutionException {
    if (!(workingDirectory.exists())) {
      throw new ExecutionException("Working directory " + workingDirectory + " does not exist.");
    }
    ProcessBuilder builder = new ProcessBuilder(myCommandLine);
    builder.environment().put("PATH", System.getenv("PATH")); // TODO: test
    builder.directory(workingDirectory);
    try {
      Process process = builder.start();
      return new DefaultProcessHandler(process, StringUtils.foldLeft(myCommandLine, "", new StringUtils.ILeftCombinator() {
        public String combine(String s, String it) {
          return (StringUtils.isEmpty(s) ?
            it :
            s + " " + it
          );
        }
      }));
    } catch (IOException e) {
      throw new ProcessNotCreatedException("Start process failed", e);
    } catch (NullPointerException e) {
      throw new ProcessNotCreatedException("Start process failed: one of the command line arguments is null", e);
    } catch (Throwable t) {
      throw new ProcessNotCreatedException("Start process failed", t);
    }
  }

  public static Iterable<String> splitCommandInParts(String command) {
    List<String> result = new ArrayList<>();
    boolean insideQuotes = false;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < command.length(); i++) {
      char currentChar = command.charAt(i);
      if (currentChar == '"' && (i == 0 || command.charAt(i - 1) != '\\')) {
        insideQuotes = !(insideQuotes);
        continue;
      }
      if (currentChar == ' ' && !(insideQuotes)) {
        // word ended 
        if (sb.length() > 0) {
          result.add(sb.toString());
          sb = new StringBuilder();
        }
      } else if (currentChar != '\\' || (i >= command.length() - 1 || command.charAt(i + 1) != '"')) {
        // inside word
        sb.append(currentChar);
      }
    }
    if (sb.length() > 0) {
      result.add(sb.toString());
    }
    return result;
  }
}