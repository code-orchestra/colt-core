package codeOrchestra.colt.core.http;

import codeOrchestra.util.SocketUtil;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class CodeOrchestraResourcesHttpServer {

    public static final int PORT = SocketUtil.findAvailablePortStartingFrom(9091);

    private static CodeOrchestraResourcesHttpServer instance = new CodeOrchestraResourcesHttpServer();

    public static CodeOrchestraResourcesHttpServer getInstance() {
        return instance;
    }

    private Server server;
    private HandlerList activeHandlers;

    private Map<String, Handler> handlersMap = new HashMap<>();

    private boolean mustStopReloadThread;

    private boolean mustReload;
    private long lastReloadRequest;
    private Object reloadMonitor = new Object();

    public void init() {
        server = new Server(PORT);

        activeHandlers = new HandlerList();
        server.setHandler(activeHandlers);

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Can't start jetty server", e);
        }

        new ServerReloadThread().start();
    }

    public void addAlias(File baseDir, String alias) {
        Handler handler = getContextHandler(alias, getResourceHandler(baseDir.getPath() + "/"));
        addHandler(handler, alias);
    }

    private void addHandler(Handler handler, String alias) {
        Handler existingHandler = handlersMap.get(alias);
        if (existingHandler != null) {
            activeHandlers.removeHandler(existingHandler);
        }

        activeHandlers.addHandler(handler);
        handlersMap.put(alias, handler);

        reloadServer();
    }

    private void reloadServer() {
        synchronized (reloadMonitor) {
            mustReload = true;
            lastReloadRequest = System.currentTimeMillis();
        }
    }

    public void dispose() {
        mustStopReloadThread = true;
        try {
            server.stop();
        } catch (Exception e) {
            // ignore
        }
    }

    private static ContextHandler getContextHandler(String contextPath, Handler handler) {
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath(contextPath);
        contextHandler.addHandler(handler);
        return contextHandler;
    }

    private static ResourceHandler getResourceHandler(String resourceBase) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(resourceBase);
        resourceHandler.setCacheControl("max-age=0, no-cache, no-store, must-revalidate");
        return resourceHandler;
    }

    private class ServerReloadThread extends Thread {
        private ServerReloadThread() {
            super("HTTP Server Reload Thread");
        }

        private void doReload() {
            try {
                System.out.println("Reloading Resources HTTP server");

                server.stop();
                server.start();
            } catch (Exception e) {
                throw new RuntimeException("Can't reload jetty server", e);
            } finally {
                mustReload = false;
            }
        }

        @Override
        public void run() {
            while (!mustStopReloadThread) {
                synchronized (reloadMonitor) {
                    if (mustReload && (System.currentTimeMillis() - lastReloadRequest) > 1500) {
                        doReload();
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }


}
