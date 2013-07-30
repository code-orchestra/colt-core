package codeOrchestra.colt.core.errorhandling;

public class ErrorHandler {

  public static void handle(final Throwable t) {
    // TODO: implement
//    IStatus status = new Status(IStatus.ERROR, "code-orchestra-lcs", 0, null, t);
//    Platform.getLog(Activator.getDefault().getBundle()).log(status);
  }
  
  public static void handle(final Throwable t, final String message) {
      // TODO: implement
//    Display.getDefault().asyncExec(new Runnable() {
//      @Override
//      public void run() {
//        StringWriter s = new StringWriter();
//        t.printStackTrace(new PrintWriter(s));
//
//        IStatus status = new Status(IStatus.ERROR, "code-orchestra-lcs", 0, null, t);
//
//        ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error", message, status);
//        Platform.getLog(Activator.getDefault().getBundle()).log(status);
//      }
//    });
  }
  
  public static void handle(final String message) {
    handle(message, "Error");
  }
  
  public static void handle(final String message, final String title) {

      // TODO: implement
//    Display.getDefault().asyncExec(new Runnable() {
//      @Override
//      public void run() {
//        MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
//
//        IStatus status = new Status(IStatus.ERROR, "code-orchestra-lcs", 0, null, null);
//        Platform.getLog(Activator.getDefault().getBundle()).log(status);
//      }
//    });
  }
  
}
