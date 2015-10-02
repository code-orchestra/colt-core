package codeOrchestra.util.process;

import codeOrchestra.colt.core.execution.OSProcessHandler;

public class DefaultProcessHandler extends OSProcessHandler {

  public DefaultProcessHandler(Process process, String parameters) {
    super(process, parameters);
    ProcessTerminatedListener.attach(this);
  }
}