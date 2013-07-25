package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.persistence.COLTProjectPersistence;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingLanguageHandler {

    String getId();

    String getName();

    COLTProjectPersistence[] getAvailablePersistenceHandlers();

}
