/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codeOrchestra.util.process;

import codeOrchestra.colt.core.execution.ProcessEvent;
import codeOrchestra.colt.core.execution.ProcessHandler;

/**
 * @author dyoma
 */
public class ProcessTerminatedListener extends ProcessAdapter {
  
  private static final String KEY = "processTerminatedListener";
  
  private final String myProcessFinishedMessage;
  protected static final String EXIT_CODE_ENTRY = "$EXIT_CODE$";  
  protected static final String EXIT_CODE_REGEX = "\\$EXIT_CODE\\$";

  private ProcessTerminatedListener(final String processFinishedMessage) {
    myProcessFinishedMessage = processFinishedMessage;
  }

  public static void attach(final ProcessHandler processHandler, final String message) {
    final ProcessTerminatedListener previousListener = processHandler.getUserData(KEY);
    if (previousListener != null) {
      processHandler.removeProcessListener(previousListener);
    }

    final ProcessTerminatedListener listener = new ProcessTerminatedListener( message);
    processHandler.addProcessListener(listener);
    processHandler.putUserData(KEY, listener);
  }

  public static void attach(final ProcessHandler processHandler) {
    String message = "finished.with.exit.code.text.message";
    attach(processHandler, "\n" + message + "\n");
  }

  public void processTerminated(ProcessEvent event) {
    final ProcessHandler processHandler = event.getProcessHandler();
    processHandler.removeProcessListener(this);
    final String message = myProcessFinishedMessage.replaceAll(EXIT_CODE_REGEX, String.valueOf(event.getExitCode()));
    processHandler.notifyTextAvailable(message, "SYSTEM");
  }
}
