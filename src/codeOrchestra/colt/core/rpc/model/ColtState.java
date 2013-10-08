package codeOrchestra.colt.core.rpc.model;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public class ColtState {

    private boolean projectLoaded;
    private String projectName;
    private String handlerId;

    private ColtConnection[] activeConnections;

    public ColtState() {
    }

    public ColtState(Project currentProject, ColtConnection[] activeConnections) {
        projectLoaded = currentProject != null;

        if (projectLoaded) {
            handlerId = currentProject.getProjectType();
            projectName = currentProject.getName();
        }

        this.activeConnections = activeConnections;
    }

    public boolean isProjectLoaded() {
        return projectLoaded;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public ColtConnection[] getActiveConnections() {
        return activeConnections;
    }
}
