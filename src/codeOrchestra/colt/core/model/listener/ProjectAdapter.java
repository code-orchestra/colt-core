package codeOrchestra.colt.core.model.listener;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public abstract class ProjectAdapter<P extends Project> implements ProjectListener<P> {

    @Override
    public void onProjectLoaded(P project) {
    }

    @Override
    public void onProjectUnloaded(P project) {
    }

}
