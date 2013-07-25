package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingLanguageHandler {

    String getId();

    String getName();

    COLTProject loadProject(String path);

}
