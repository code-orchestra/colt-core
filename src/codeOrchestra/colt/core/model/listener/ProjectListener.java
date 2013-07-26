package codeOrchestra.colt.core.model.listener;

import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public interface ProjectListener<P extends COLTProject> {

    void onProjectLoaded(P project);

    void onProjectUnloaded(P project);

}
