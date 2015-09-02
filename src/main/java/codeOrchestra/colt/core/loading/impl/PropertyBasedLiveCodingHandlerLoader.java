package codeOrchestra.colt.core.loading.impl;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoader;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class PropertyBasedLiveCodingHandlerLoader implements LiveCodingHandlerLoader {

    private Map<String, Class> idToHandler = new HashMap<>();

    private boolean hasBeenInitialized;

    private void init() throws LiveCodingHandlerLoadingException {
        String handlersProperty = System.getProperty("colt.handlers");
        if (StringUtils.isNotEmpty(handlersProperty)) {
            String[] handlersSplit = handlersProperty.split("\\,");
            if (handlersSplit != null && handlersSplit.length > 0) {
                for (String handlerPart : handlersSplit) {
                    String[] handlerPartSplit = handlerPart.split("\\:");

                    String id = handlerPartSplit[0];
                    String className = handlerPartSplit[1];

                    try {
                        Class<?> handlerClass = Class.forName(className);
                        idToHandler.put(id, handlerClass);
                    } catch (ClassNotFoundException e) {
                        throw new LiveCodingHandlerLoadingException(e);
                    }
                }
            }
        }

        hasBeenInitialized = true;
    }

    @Override
    public LiveCodingLanguageHandler load(String id) throws LiveCodingHandlerLoadingException {
        if (!hasBeenInitialized) {
            init();
        }

        Class handlerClass = idToHandler.get(id);
        if (handlerClass == null) {
            throw new LiveCodingHandlerLoadingException("Unknown live coding handler ID: " + id);
        }

        try {
            return (LiveCodingLanguageHandler) handlerClass.newInstance();
        } catch (InstantiationException e) {
            throw new LiveCodingHandlerLoadingException(e);
        } catch (IllegalAccessException e) {
            throw new LiveCodingHandlerLoadingException(e);
        }
    }

}
