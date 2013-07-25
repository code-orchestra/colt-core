package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;

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

    private LiveCodingLanguageHandler currentHandler;

    @Override
    public LiveCodingLanguageHandler load(String id) {
        // TODO: implement
        return null;
    }

    public void dispose() {
        // TODO: implement
    }

    public LiveCodingLanguageHandler getCurrentHandler() {
        return currentHandler;
    }

}
