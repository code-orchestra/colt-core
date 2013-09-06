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

  public ProcessHandlerBuilder append(String... command) {
    for (String commandPart : command) {
      append(commandPart);
    }
    return this;
  }

  public ProcessHandlerBuilder append(List<String> command) {
    for (String commandPart : command) {
      append(commandPart);
    }
    return this;
  }

  public ProcessHandlerBuilder appendKey(String key, String parameter) {
    if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(parameter)) {
      return append("-" + key).append(parameter);
    }
    return this;
  }

  public ProcessHandlerBuilder appendKey(String key, String... parameter) {
    if (StringUtils.isNotEmpty(key) && parameter.length > 0) {
      return append("-" + key).append(parameter);
    }
    return this;
  }

  public ProcessHandlerBuilder appendKey(String key, List<String> parameters) {
    if (StringUtils.isNotEmpty(key) && !parameters.isEmpty()) {
      return append("-" + key).append(parameters);
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
    builder.directory(workingDirectory);
    try {
      Process process = builder.start();
      DefaultProcessHandler processHandler = new DefaultProcessHandler(process, StringUtils.foldLeft(myCommandLine, "", new StringUtils.ILeftCombinator() {
        public String combine(String s, String it) {
          return (StringUtils.isEmpty(s) ?
            it :
            s + " " + it
          );
        }
      }));
      return processHandler;
    } catch (IOException e) {
      throw new ProcessNotCreatedException("Start process failed", e, getCommandLine(workingDirectory.getAbsolutePath()));
    } catch (NullPointerException e) {
      throw new ProcessNotCreatedException("Start process failed: one of the command line arguments is null", e, getCommandLine(workingDirectory.getAbsolutePath()));
    } catch (Throwable t) {
      throw new ProcessNotCreatedException("Start process failed", t, getCommandLine(workingDirectory.getAbsolutePath()));
    }
  }

  private GeneralCommandLine getCommandLine(String workingDirectory) {
    GeneralCommandLine commandLine = new GeneralCommandLine();

    if (myCommandLine.size() > 0) {
      commandLine.setExePath(myCommandLine.get(0));
    }
    commandLine.setWorkDirectory(workingDirectory);
    
    if (myCommandLine.size() > 1) {
      for (int i = 1; i < myCommandLine.size(); i++) {
        myCommandLine.add(myCommandLine.get(i));
      }
    }
    
    return commandLine;
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
      } else if (currentChar == '\\' && (i < command.length() - 1 && command.charAt(i + 1) == '"')) {
        continue;
      } else {
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
