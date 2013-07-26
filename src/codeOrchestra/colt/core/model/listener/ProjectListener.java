package codeOrchestra.colt.core.model.listener;

import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public interface ProjectListener {

    void onProjectLoaded(COLTProject project);

    void onProjectUnloaded(COLTProject project);

}
