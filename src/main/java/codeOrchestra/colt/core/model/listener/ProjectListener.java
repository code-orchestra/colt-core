package codeOrchestra.colt.core.model.listener;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public interface ProjectListener<P extends Project> {

    void onProjectLoaded(P project);

    void onProjectUnloaded(P project);

}
