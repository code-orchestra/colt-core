package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.listener.ProjectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingLanguageHandler implements LiveCodingLanguageHandler {

    private List<ProjectListener> projectListeners = new ArrayList<ProjectListener>();

    @Override
    public synchronized void addProjectListener(ProjectListener projectListener) {
        projectListeners.add(projectListener);
    }

    @Override
    public synchronized void removeProjectListener(ProjectListener projectListener) {
        projectListeners.remove(projectListener);
    }

}
