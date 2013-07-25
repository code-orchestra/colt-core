package codeOrchestra.colt.core.model;

/**
 * @author Alexander Eliseyev
 */
public abstract class COLTProject {

    private String name;
    private String handlerId;

    private transient String path;

    private boolean disposed;

    public abstract COLTProjectPaths getProjectPaths();
    public abstract COLTProjectLiveSettings getProjectLiveSettings();
    public abstract COLTProjectBuildSettings getProjectBuildSettings();

    public String getName() {
        return name;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public String getPath() {
        return path;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }
}
