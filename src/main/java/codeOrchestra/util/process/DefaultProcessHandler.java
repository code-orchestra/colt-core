package codeOrchestra.util.process;

import codeOrchestra.colt.core.execution.OSProcessHandler;
import codeOrchestra.colt.core.execution.ProcessListener;

public class DefaultProcessHandler extends OSProcessHandler {

  public DefaultProcessHandler(Process process, String parameters, ProcessListener processListener) {
    super(process, parameters);
    addProcessListener(processListener);
    ProcessTerminatedListener.attach(this);
  }

  public DefaultProcessHandler(Process process, String parameters) {
    super(process, parameters);
    ProcessTerminatedListener.attach(this);
  }
}
