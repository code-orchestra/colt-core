package codeOrchestra.colt.core;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.model.listener.ProjectListener;
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

    void initHandler();

    void disposeHandler();

    void addProjectListener(ProjectListener projectListener);

    void removeProjectListener(ProjectListener projectListener);

    Logger getLogger(String source);

}
