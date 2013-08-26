package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.loading.impl.IdeaDevLiveCodingHandlerLoader;
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

    private LiveCodingHandlerManager() {
    }

    // TODO: change to JarLiveCodingHandlerLoader for production
    private final LiveCodingHandlerLoader ideaDevLiveCodingHandlerLoader = new IdeaDevLiveCodingHandlerLoader();

    private LiveCodingLanguageHandler currentHandler;

    private LiveCodingHandlerLoader getLoader() {
        return ideaDevLiveCodingHandlerLoader;
    }

    @Override
    public LiveCodingLanguageHandler load(String id) throws LiveCodingHandlerLoadingException {
        if (currentHandler != null) {
            if (currentHandler.getId().equals(id)) {
                return currentHandler;
            }

            dispose();
        }

        currentHandler = getLoader().load(id);

        // Start the RPC service
        ColtRemoteServiceServlet.getInstance().refreshService();

        currentHandler.initHandler();

        try {
            ColtApplication.get().setPluginPane(currentHandler.getPane());
        } catch (Exception e) {
            throw new LiveCodingHandlerLoadingException("Couldn't init the live coding handler UI", e);
        }

        return getCurrentHandler();
    }

    public void dispose() {
        if (currentHandler != null) {
            currentHandler.disposeHandler();
            currentHandler = null;
        }

        ServiceProvider.dispose();
        Logger.dispose();
    }

    public LiveCodingLanguageHandler getCurrentHandler() {
        return currentHandler;
    }

}
