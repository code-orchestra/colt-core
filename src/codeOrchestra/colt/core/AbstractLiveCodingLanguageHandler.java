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

    @Override
    public synchronized void fireProjectLoaded() {
        for (ProjectListener projectListener : projectListeners) {
            projectListener.onProjectLoaded(getCurrentProject());
        }
    }

    @Override
    public synchronized void fireProjectClosed() {
        for (ProjectListener projectListener : projectListeners) {
            projectListener.onProjectUnloaded(getCurrentProject());
        }
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
        return (P) COLTProjectManager.getInstance().getCurrentProject();
    }
}
