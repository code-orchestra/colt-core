package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.loading.impl.IdeaDevLiveCodingHandlerLoader;
import codeOrchestra.colt.core.rpc.COLTRemoteService;
import codeOrchestra.colt.core.rpc.COLTRemoteServiceServlet;
import codeOrchestra.colt.core.ui.COLTApplication;

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
        COLTRemoteServiceServlet.getInstance().refreshService();

        currentHandler.initHandler();

        try {
            COLTApplication.get().setPluginPane(currentHandler.getPane());
        } catch (Exception e) {
            throw new LiveCodingHandlerLoadingException("Couldn't init the live coding handler UI", e);
        }

        return getCurrentHandler();
    }

    public void dispose() {
        currentHandler.disposeHandler();
        currentHandler = null;
    }

    public LiveCodingLanguageHandler getCurrentHandler() {
        return currentHandler;
    }

}
