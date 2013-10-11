package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.jmdns.JmDNSFacade;
import codeOrchestra.colt.core.loading.impl.PropertyBasedLiveCodingHandlerLoader;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceServlet;
import codeOrchestra.colt.core.ui.ColtApplication;

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

        JmDNSFacade.getInstance().dispose();
        ServiceProvider.dispose();
        Logger.dispose();
    }

    public LiveCodingLanguageHandler getCurrentHandler() {
        return currentHandler;
    }

}
