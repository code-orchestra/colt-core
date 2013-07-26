package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.model.listener.ProjectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingLanguageHandler<P extends COLTProject> implements LiveCodingLanguageHandler<P> {

    private List<ProjectListener> projectListeners = new ArrayList<ProjectListener>();

    private P currentProject;

    protected AbstractLiveCodingLanguageHandler() {
        addProjectListener(new ProjectListener<P>() {
            @Override
            public void onProjectLoaded(P project) {
                currentProject = project;
            }

            @Override
            public void onProjectUnloaded(P project) {
                currentProject = null;
            }
        });
    }

    @Override
    public synchronized void addProjectListener(ProjectListener<P> projectListener) {
        projectListeners.add(projectListener);
    }

    @Override
    public synchronized void removeProjectListener(ProjectListener<P> projectListener) {
        projectListeners.remove(projectListener);
    }

    @Override
    public P getCurrentProject() {
        return currentProject;
    }
}
