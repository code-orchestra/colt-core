package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.loading.impl.PropertyBasedLiveCodingHandlerLoader;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceServlet;
import codeOrchestra.colt.core.ui.ColtApplication;
import codeOrchestra.colt.core.ui.components.log.JSBridge;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Alexander Eliseyev
 */
public final class LiveCodingHandlerManager implements LiveCodingHandlerLoader {

    private static LiveCodingHandlerManager instance;

    public static synchronized LiveCodingHandlerManager getInstance() {
        if (instance == null) {
            instance = new LiveCodingHandlerManager();
        }
        return instance;
    }

    private LiveCodingHandlerManager() {
        try {
            jmDNS = JmDNS.create(InetAddress.getLocalHost());
        } catch (IOException e) {
            ErrorHandler.handle(e, "Can't register COLT external API service via jmdns");
        }
    }

    private JmDNS jmDNS;
    private ServiceInfo serviceInfo;

    private final LiveCodingHandlerLoader ideaDevLiveCodingHandlerLoader = new PropertyBasedLiveCodingHandlerLoader();

    private LiveCodingLanguageHandler currentHandler;

    private LiveCodingHandlerLoader getLoader() {
        return ideaDevLiveCodingHandlerLoader;
    }

    public LiveCodingLanguageHandler get(String id) throws LiveCodingHandlerLoadingException {
        return getLoader().load(id);
    }

    @Override
    public LiveCodingLanguageHandler load(String id) throws LiveCodingHandlerLoadingException {
        if (currentHandler != null) {
            if (currentHandler.getId().equals(id)) {
                return currentHandler;
            }

            dispose();
        }

        currentHandler = get(id);

        // Start the RPC service
        ColtRemoteServiceServlet.getInstance().refreshService();

        // Publish RPC service in jmdns
        if (jmDNS != null) {
            serviceInfo = ServiceInfo.create("_http._tcp.local.", "ColtRPC", CodeOrchestraRPCHttpServer.PORT, "ColtRPC " + id);
            try {
                jmDNS.registerService(serviceInfo);
            } catch (IOException e) {
                ErrorHandler.handle(e, "Can't register COLT external API service via jmdns");
            }
        }

        // Init
        currentHandler.initHandler();

        // Load UI
        try {
            ColtApplication.get().setPluginPane(currentHandler.getPane());
        } catch (Exception e) {
            throw new LiveCodingHandlerLoadingException("Couldn't init the live coding handler UI", e);
        }

        return currentHandler;
    }

    public void dispose() {
        if (currentHandler != null) {
            currentHandler.disposeHandler();
            currentHandler = null;
        }

        if (jmDNS != null) {
            jmDNS.unregisterService(serviceInfo);
        }

        ServiceProvider.dispose();
        Logger.dispose();
    }

    public LiveCodingLanguageHandler getCurrentHandler() {
        return currentHandler;
    }

}
