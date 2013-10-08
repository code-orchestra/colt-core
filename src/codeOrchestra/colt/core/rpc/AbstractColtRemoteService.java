package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.*;
import codeOrchestra.colt.core.controller.ColtControllerCallbackEx;
import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.Project;
import codeOrchestra.colt.core.rpc.command.RemoteAsyncCommand;
import codeOrchestra.colt.core.rpc.command.RemoteCommand;
import codeOrchestra.colt.core.rpc.model.ColtConnection;
import codeOrchestra.colt.core.rpc.model.ColtRemoteProject;
import codeOrchestra.colt.core.rpc.model.ColtState;
import codeOrchestra.colt.core.rpc.security.ColtRemoteSecurityManager;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import codeOrchestra.colt.core.rpc.security.InvalidShortCodeException;
import codeOrchestra.colt.core.rpc.security.TooManyFailedCodeTypeAttemptsException;
import codeOrchestra.colt.core.session.LiveCodingSession;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractColtRemoteService<P extends Project> implements ColtRemoteService<P> {

    private final Object monitor = new Object();

    @Override
    public void createProject(String securityToken, final ColtRemoteProject<P> remoteProject)
            throws ColtRemoteTransferableException {
        executeSecurilyInUI(securityToken, new RemoteCommand<Void>() {
            @Override
            public String getName() {
                return "Create project under " + remoteProject.getPath();
            }

            @Override
            public Void execute() throws ColtRemoteException {
                LiveCodingLanguageHandler<P> handler;
                try {
                    handler = LiveCodingHandlerManager.getInstance().get(remoteProject.getType());
                } catch (LiveCodingHandlerLoadingException e) {
                    throw new ColtRemoteException("Can't load the handler for the project type " + remoteProject.getType(), e);
                }

                File projectFile = new File(remoteProject.getPath());
                P createdProject = handler.createProject(remoteProject.getName(), projectFile, false);
                createdProject.setPath(projectFile.getPath());

                remoteProject.copyToProject(createdProject);

                try {
                    ColtProjectManager.getInstance().save(createdProject);
                } catch (ColtException e) {
                    throw new ColtRemoteException(e);
                }

                try {
                    ColtProjectManager.getInstance().load(projectFile.getPath());
                } catch (ColtException e) {
                    throw new ColtRemoteException(e);
                }

                return null;
            }
        });
    }

    public void loadProject(String securityToken, final String path) throws ColtRemoteTransferableException {
        executeSecurilyInUI(securityToken, new RemoteCommand<Void>() {
            @Override
            public String getName() {
                return "Load project " + path;
            }

            @Override
            public Void execute() throws ColtRemoteException {
                try {
                    ColtProjectManager.getInstance().load(path);
                } catch (ColtException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
    }

    @Override
    public ColtState getState() {
        Project currentProject = ColtProjectManager.getInstance().getCurrentProject();

        if (currentProject != null) {
            List<ColtConnection> coltConnections = new ArrayList<>();
            List<LiveCodingSession> currentConnections = ServiceProvider.get(LiveCodingManager.class).getCurrentConnections();
            for (LiveCodingSession session : currentConnections) {
                if (!session.isDisposed()) {
                    coltConnections.add(new ColtConnection(session));
                }
            }

            return new ColtState(currentProject, coltConnections.toArray(new ColtConnection[coltConnections.size()]));
        }

        return new ColtState();
    }

    @Override
    public String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException {
        return ColtRemoteSecurityManager.getInstance().obtainAuthToken(shortCode);
    }

    @Override
    public void requestShortCode(final String requestor) throws ColtRemoteTransferableException {
        ColtRemoteSecurityManager.getInstance().createNewTokenAndGetShortCode(requestor);
    }

    @Override
    public void checkAuth(String securityToken) throws InvalidAuthTokenException {
        if (!ColtRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }
    }

    @Override
    public int ping() {
        return 0;
    }

    protected <T> T executeInDisplayAsyncAndWait(final RemoteAsyncCommand<T> command)
            throws ColtRemoteTransferableException {
        final Throwable[] exception = new Throwable[1];
        final Object[] result = new Object[1];

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                command.execute(new ColtControllerCallbackEx<T>() {
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

            if (exception[0] instanceof ColtRemoteTransferableException) {
                throw (ColtRemoteTransferableException) exception[0];
            }

            ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
            throw new ColtUnhandledException(exception[0]);
        }

        return (T) result[0];
    }

    protected <T> T executeInDisplayAndWait(final RemoteCommand<T> command) throws ColtRemoteTransferableException {
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

            if (exception[0] instanceof ColtRemoteTransferableException) {
                throw (ColtRemoteTransferableException) exception[0];
            }

            ErrorHandler.handle(exception[0], "Error while handling remote command: " + command.getName());
            throw new ColtUnhandledException(exception[0]);
        }

        return (T) result[0];
    }

    protected <T> T executeSecurily(String securityToken, final RemoteCommand<T> command)
            throws ColtRemoteTransferableException {
        if (!ColtRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }

        try {
            return command.execute();
        } catch (ColtRemoteException e) {
            e.printStackTrace();

            if (e instanceof ColtRemoteTransferableException) {
                throw (ColtRemoteTransferableException) e;
            }

            ErrorHandler.handle(e, "Error while handling remote command: " + command.getName());
            throw new ColtUnhandledException(e);
        }
    }

    protected <T> T executeSecurilyInUI(String securityToken, final RemoteCommand<T> command)
            throws ColtRemoteTransferableException {
        if (!ColtRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }

        return executeInDisplayAndWait(command);
    }

    protected <T> T executeSecurilyAsyncInUI(String securityToken, final RemoteAsyncCommand<T> command)
            throws ColtRemoteTransferableException {
        if (!ColtRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }

        return executeInDisplayAsyncAndWait(command);
    }

    @Override
    public void dispose() {
    }

}
