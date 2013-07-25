package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.persistence.COLTProjectPersistence;
import codeOrchestra.colt.core.session.sourcetracking.SourceFileFactory;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingLanguageHandler {

    String getId();

    String getName();

    COLTProjectPersistence[] getAvailablePersistenceHandlers();

    SourceFileFactory getSourceFileFactory();

}
