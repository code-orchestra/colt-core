package codeOrchestra.colt.core.loading;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingHandlerLoader {
    LiveCodingLanguageHandler load(String id) throws LiveCodingHandlerLoadingException;
}