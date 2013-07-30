package codeOrchestra.colt.core.model;

import codeOrchestra.colt.core.model.persistence.COLTProjectPersistedAspect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class COLTProject {

    private String name;
    private String handlerId;

    private transient String path;

    private boolean disposed;

    // Default aspects
    public abstract COLTProjectPaths getProjectPaths();
    public abstract COLTProjectLiveSettings getProjectLiveSettings();
    public abstract COLTProjectBuildSettings getProjectBuildSettings();

    protected abstract List<COLTProjectPersistedAspect> getLanguageSpecificAspects();

    /*
    private List<COLTProjectPersistedAspect> getDefaultAspects() {
        return new ArrayList<COLTProjectPersistedAspect>() {{
            add(getProjectPaths());
            add(getProjectLiveSettings());
            add(getProjectBuildSettings());
        }};
    }
    */

    public List<COLTProjectPersistedAspect> getAllPersistedAspects() {
        List<COLTProjectPersistedAspect> result = new ArrayList<COLTProjectPersistedAspect>();

//        result.addAll(getDefaultAspects());
        result.addAll(getLanguageSpecificAspects());

        return result;
    }

    public COLTProjectPersistedAspect getPersistedAspectByName(String name) {
        for (COLTProjectPersistedAspect coltProjectCOLTProjectPersistedAspect : getAllPersistedAspects()) {
            if (name.equals(coltProjectCOLTProjectPersistedAspect.getAspectName())) {
                return coltProjectCOLTProjectPersistedAspect;
            }
        }
        return null;
    }

    public File getBaseDir() {
        return new File(path).getParentFile();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }

}
