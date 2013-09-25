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
package codeOrchestra.colt.core.execution;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.util.Consumer;
import codeOrchestra.util.StringUtils;
import codeOrchestra.util.process.ProcessAdapter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.*;
import java.util.concurrent.ExecutionException;

public class OSProcessHandler extends ProcessHandler {
  
  private static final Logger LOG = Logger.getLogger(OSProcessHandler.class);
  private final Process myProcess;
  private final String myCommandLine;

  private final ProcessWaitFor myWaitFor;

  private static class ExecutorServiceHolder {
    private static final ExecutorService ourThreadExecutorsService = createServiceImpl();

    private static ThreadPoolExecutor createServiceImpl() {
      return new ThreadPoolExecutor(10, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        @SuppressWarnings({"HardCodedStringLiteral"})
        public Thread newThread(Runnable r) {
          return new Thread(r, "OSProcessHandler pooled thread");
        }
      });
    }
  }

  public static void dispose() {
      ExecutorServiceHolder.ourThreadExecutorsService.shutdown();
  }

  /**
   * Override this method in order to execute the task with a custom pool
   *
   * @param task a task to run
   */
  protected Future<?> executeOnPooledThread(Runnable task) {
    return ExecutorServiceHolder.ourThreadExecutorsService.submit(task);
  }

  public OSProcessHandler(final Process process, final String commandLine) {
    myProcess = process;
    myCommandLine = commandLine;
    myWaitFor = new ProcessWaitFor(process);
  }
    public OSProcessHandler(File worDir, String... command) throws IOException {
        this(new ProcessBuilder(command).directory(worDir).start(), StringUtils.join(command, " "));
    }

  private class ProcessWaitFor {
    private final Future<?> myWaitForThreadFuture;
    private final BlockingQueue<Consumer<Integer>> myTerminationCallback = new ArrayBlockingQueue<>(1);

    public void detach() {
      myWaitForThreadFuture.cancel(true);
    }


    public ProcessWaitFor(final Process process) {
      myWaitForThreadFuture = executeOnPooledThread(new Runnable() {
        public void run() {
          int exitCode = 0;
          try {
            while (true) {
              try {
                exitCode = process.waitFor();
                break;
              }
              catch (InterruptedException e) {
                return;
              }
            }
          }
          finally {
            try {
              myTerminationCallback.take().consume(exitCode);
            }
            catch (InterruptedException e) {
              LOG.info(e);
            }
          }
        }
      });
    }

    public void setTerminationCallback(Consumer<Integer> r) {
      myTerminationCallback.offer(r);
    }
  }

  public Process getProcess() {
    return myProcess;
  }

  public void startNotify() {
    final ReadProcessThread stdoutThread = new ReadProcessThread(createProcessOutReader()) {
      protected void textAvailable(String s) {
        notifyTextAvailable(s, "STDOUT");
      }
    };

    final ReadProcessThread stderrThread = new ReadProcessThread(createProcessErrReader()) {
      protected void textAvailable(String s) {
        notifyTextAvailable(s, "STDERR");
      }
    };

    notifyTextAvailable(myCommandLine + '\n', "SYSTEM");

    addProcessListener(new ProcessAdapter() {
      public void startNotified(final ProcessEvent event) {
        try {
          final Future<?> stdOutReadingFuture = executeOnPooledThread(stdoutThread);
          final Future<?> stdErrReadingFuture = executeOnPooledThread(stderrThread);

          myWaitFor.setTerminationCallback(new Consumer<Integer>() {
            @Override
            public void consume(Integer exitCode) {
              try {
                // tell threads that no more attempts to read process' output should be made
                stderrThread.setProcessTerminated(true);
                stdoutThread.setProcessTerminated(true);

                stdErrReadingFuture.get();
                stdOutReadingFuture.get();
              }
              catch (InterruptedException ignored) {
              }
              catch (ExecutionException e) {
                LOG.error(e);
              }
              finally {
                onOSProcessTerminated(exitCode);
              }
            }
          });
        }
        finally {
          removeProcessListener(this);
        }
      }
    });

    super.startNotify();
  }

  protected void onOSProcessTerminated(final int exitCode) {
    notifyProcessTerminated(exitCode);
  }

  protected Reader createProcessOutReader() {
    return new InputStreamReader(myProcess.getInputStream(), getCharset());
  }

  protected Reader createProcessErrReader() {
    return new InputStreamReader(myProcess.getErrorStream(), getCharset());
  }

  protected void destroyProcessImpl() {
    try {
      closeStreams();
    }
    finally {
      myProcess.destroy();
    }
  }

  protected void detachProcessImpl() {
    final Runnable runnable = new Runnable() {
      public void run() {
        closeStreams();

        myWaitFor.detach();
        notifyProcessDetached();
      }
    };

    executeOnPooledThread(runnable);
  }

  private void closeStreams() {
    try {
      myProcess.getOutputStream().close();
    }
    catch (IOException e) {
      LOG.error(e);
    }
  }

  public boolean detachIsDefault() {
    return false;
  }

  public OutputStream getProcessInput() {
    return myProcess.getOutputStream();
  }

  // todo: to remove
  public String getCommandLine() {
    return myCommandLine;
  }

  public Charset getCharset() {    
    return Charset.forName(StringUtils.getOutputEncoding());
  }

  private abstract static class ReadProcessThread implements Runnable {
    private final Reader myReader;
    private boolean skipLF = false;

    private boolean myIsProcessTerminated = false;
    private final char[] myBuffer = new char[8192];

    public ReadProcessThread(final Reader reader) {
      myReader = reader;
    }

    public synchronized void setProcessTerminated(boolean isProcessTerminated) {
      myIsProcessTerminated = isProcessTerminated;
    }

    public void run() {
      try {
        while (true) {
          final int rc = readAvailable();
          if (rc == DONE) break;
          Thread.sleep(rc == READ_SOME ? 1L : 50L);
        }
      }
      catch (InterruptedException ignore) {
      }
      catch (IOException e) {
        LOG.info(e);
      }
      catch (Exception e) {
        LOG.error(e);
      }
    }

    private static final int DONE = 0;
    private static final int READ_SOME = 1;
    private static final int READ_NONE = 2;

    private synchronized int readAvailable() throws IOException {
      char[] buffer = myBuffer;
      StringBuilder token = new StringBuilder();
      int rc = READ_NONE;
      while (myReader.ready()) {
        int n = myReader.read(buffer);
        if (n <= 0) break;
        rc = READ_SOME;

        for (int i = 0; i < n; i++) {
          char c = buffer[i];
          if (skipLF && c != '\n') {
            token.append('\r');
          }

          if (c == '\r') {
            skipLF = true;
          }
          else {
            skipLF = false;
            token.append(c);
          }

          if (c == '\n') {
            textAvailable(token.toString());
            token.setLength(0);
          }
        }
      }

      if (token.length() != 0) {
        textAvailable(token.toString());
        token.setLength(0);
      }

      if (myIsProcessTerminated) {
        try {
          myReader.close();
        }
        catch (IOException e1) {
          // supressed
        }

        return DONE;
      }

      return rc;
    }

    protected abstract void textAvailable(final String s);
  }
}
