package codeOrchestra.colt.core;

import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.model.COLTProjectHandlerIdParser;
import codeOrchestra.colt.core.model.listener.ProjectListener;
import codeOrchestra.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class COLTProjectManager {

    private static COLTProjectManager instance;

    public static synchronized COLTProjectManager getInstance() {
        if (instance == null) {
            instance = new COLTProjectManager();
        }
        return instance;
    }

    private COLTProject currentProject;

    private COLTProjectManager() {
    }

    public synchronized COLTProject getCurrentProject() {
        return currentProject;
    }

    private List<ProjectListener> projectListeners = new ArrayList<ProjectListener>();

    public synchronized void fireProjectLoaded() {
        for (ProjectListener projectListener : projectListeners) {
            projectListener.onProjectLoaded(getCurrentProject());
        }
    }

    public synchronized void fireProjectClosed() {
        for (ProjectListener projectListener : projectListeners) {
            projectListener.onProjectUnloaded(getCurrentProject());
        }
    }

    public synchronized void addProjectListener(ProjectListener projectListener) {
        projectListeners.add(projectListener);
    }

    public synchronized void removeProjectListener(ProjectListener projectListener) {
        projectListeners.remove(projectListener);
    }

    public synchronized void load(String path) throws COLTException {
        if (currentProject != null) {
            unload();
        }

        COLTProjectHandlerIdParser coltProjectHandlerIdParser = new COLTProjectHandlerIdParser(FileUtils.read(new File(path)));
        String handlerId = coltProjectHandlerIdParser.getHandlerId();
        if (handlerId == null) {
            throw new COLTException("Can't figure out the handler ID for the project path " + path);
        }

        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new COLTException("Can't load the handler for the project type " + handlerId);
        }

        // Parse the project
        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.parseProject(coltProjectHandlerIdParser.getNode(), path);

        fireProjectLoaded();
    }

    public synchronized void save() throws COLTException {
        File file = new File(currentProject.getPath());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(currentProject.toXmlString());
            fileWriter.close();
        } catch (IOException e) {
            throw new COLTException("Can't write COLT project file to " + file.getPath());
        }
    }

    public synchronized void create(String handlerId, String pName, File pFile) throws COLTException {
        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new COLTException("Can't load the handler for the project type " + handlerId);
        }

        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.createProject(pName, pFile);
        currentProject.setPath(pFile.getPath());

        save();

        fireProjectLoaded();
    }

    public synchronized void importProject(File file) throws COLTException {
        String handlerId = "AS";
        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new COLTException("Can't load the handler for the project type " + handlerId);
        }

        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.importProject(file);
        currentProject.setPath(file.getPath());

        fireProjectLoaded();
    }

    public synchronized void unload() throws COLTException {
        // TODO: implement
    }

}
