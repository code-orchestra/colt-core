package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.controller.COLTControllerCallbackEx;
import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.rpc.command.RemoteAsyncCommand;
import codeOrchestra.colt.core.rpc.command.RemoteCommand;
import codeOrchestra.colt.core.rpc.security.COLTRemoteSecurityManager;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import codeOrchestra.colt.core.rpc.security.InvalidShortCodeException;
import codeOrchestra.colt.core.rpc.security.TooManyFailedCodeTypeAttemptsException;
import javafx.application.Platform;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractCOLTRemoteService<P extends COLTProject> implements COLTRemoteService<P> {

    private final Object monitor = new Object();

    @Override
    public String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException {
        return COLTRemoteSecurityManager.getInstance().obtainAuthToken(shortCode);
    }

    @Override
    public void requestShortCode(final String requestor) throws COLTRemoteTransferableException {
        COLTRemoteSecurityManager.getInstance().createNewTokenAndGetShortCode(requestor);
    }

    @Override
    public void checkAuth(String securityToken) throws InvalidAuthTokenException {
        if (!COLTRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }
    }

    @Override
    public int ping() {
        return 0;
    }

    private <T> T executeInDisplayAsyncAndWait(final RemoteAsyncCommand<T> command)
            throws COLTRemoteTransferableException {
        final Throwable[] exception = new Throwable[1];
        final Object[] result = new Object[1];

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                command.execute(new COLTControllerCallbackEx<T>() {
                    @Override
                    public void onComplete(T successResult) {
                        result[0] = successResult;

                        synchronized (monitor) {
                            monitor.notify();
                        }
                    }

                    @Override
                    public void onError(Throwable t, T errorResult) {
                        exception[0] = t;
                        result[0] = errorResult;

                        synchronized (monitor) {
                            monitor.notify();
                        }
                    }
                });
            }
        });

        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        if (exception[0] != null) {
            exception[0].printStackTrace();

            if (exception[0] instanceof COLTRemoteTransferableException) {
                throw (COLTRemoteTransferableException) exception[0];
            }

            ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
            throw new COLTUnhandledException(exception[0]);
        }

        return (T) result[0];
    }

    private <T> T executeInDisplayAndWait(final RemoteCommand<T> command) throws COLTRemoteTransferableException {
        final Throwable[] exception = new Throwable[1];
        final Object[] result = new Object[1];

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    result[0] = command.execute();
                } catch (Throwable t) {
                    exception[0] = t;
                } finally {
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            }
        });

        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        if (exception[0] != null) {
            exception[0].printStackTrace();

            if (exception[0] instanceof COLTRemoteTransferableException) {
                throw (COLTRemoteTransferableException) exception[0];
            }

            ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
            throw new COLTUnhandledException(exception[0]);
        }

        return (T) result[0];
    }

    private <T> T executeSecurily(String securityToken, final RemoteCommand<T> command)
            throws COLTRemoteTransferableException {
        if (!COLTRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }

        try {
            return command.execute();
        } catch (COLTRemoteException e) {
            e.printStackTrace();

            if (e instanceof COLTRemoteTransferableException) {
                throw (COLTRemoteTransferableException) e;
            }

            ErrorHandler.handle(e, "Error while handling remote command: " + command.getName());
            throw new COLTUnhandledException(e);
        }
    }

    private <T> T executeSecurilyInUI(String securityToken, final RemoteCommand<T> command)
            throws COLTRemoteTransferableException {
        if (!COLTRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }

        return executeInDisplayAndWait(command);
    }

    private <T> T executeSecurilyAsyncInUI(String securityToken, final RemoteAsyncCommand<T> command)
            throws COLTRemoteTransferableException {
        if (!COLTRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }

        return executeInDisplayAsyncAndWait(command);
    }

}
