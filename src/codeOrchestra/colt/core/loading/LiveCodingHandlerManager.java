package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.loading.impl.IdeaDevLiveCodingHandlerLoader;

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
        return getLoader().load(id);
    }

    public void dispose() {
        currentHandler = null;
    }

    public LiveCodingLanguageHandler getCurrentHandler() {
        return currentHandler;
    }

}
