package codeOrchestra.colt.core;

import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.model.Project;
import codeOrchestra.colt.core.model.ProjectHandlerIdParser;
import codeOrchestra.colt.core.model.listener.ProjectListener;
import codeOrchestra.colt.core.model.monitor.ChangingMonitor;
import codeOrchestra.util.FileUtils;
import codeOrchestra.util.ProjectHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Alexander Eliseyev
 */
public class ColtProjectManager {

    private static ColtProjectManager instance;

    public static synchronized ColtProjectManager getInstance() {
        if (instance == null) {
            instance = new ColtProjectManager();
        }
        return instance;
    }

    private List<ProjectListener> projectListeners = new ArrayList<>();

    private Project currentProject;

    private ColtProjectManager() {
        addProjectListener(new ProjectListener() {
            @Override
            public void onProjectLoaded(Project project) {
                RecentProjects.addRecentProject(project.getPath());
                Logger.getLogger(ColtProjectManager.class).info("Loaded " + project.getProjectType() + " project " + project.getName() + " on "
                        + DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.US).format(new Date()));
            }

            @Override
            public void onProjectUnloaded(Project project) {
            }
        });
    }

    public synchronized Project getCurrentProject() {
        return currentProject;
    }

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

    public synchronized void load(String path) throws ColtException {
        if (currentProject != null) {
            unload();
        }

        File projectFile = new File(path);
        String projectFileContents = FileUtils.read(projectFile);

        if (ProjectHelper.isLegacyProject(projectFileContents)) {
            importProject(projectFile);
            return;
        }

        ProjectHandlerIdParser coltProjectHandlerIdParser = new ProjectHandlerIdParser(projectFileContents);
        String handlerId = coltProjectHandlerIdParser.getHandlerId();
        if (handlerId == null) {
            throw new ColtException("Can't figure out the handler ID for the project path " + path);
        }

        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new ColtException("Can't load the handler for the project type " + handlerId);
        }

        // Parse the project
        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.parseProject(coltProjectHandlerIdParser.getNode(), path);

        ChangingMonitor.getInstance().reset();

        fireProjectLoaded();
    }

    public synchronized void save() throws ColtException {
        File file = new File(currentProject.getPath());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(currentProject.toXmlString());
            fileWriter.close();
            ChangingMonitor.getInstance().reset();
        } catch (IOException e) {
            throw new ColtException("Can't write COLT project file to " + file.getPath());
        }
    }

    public synchronized void create(String handlerId, String pName, File pFile) throws ColtException {
        if (currentProject != null) {
            unload();
        }

        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new ColtException("Can't load the handler for the project type " + handlerId);
        }

        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.createProject(pName, pFile);
        currentProject.setPath(pFile.getPath());
        currentProject.setNewProject(true);

        save();

        fireProjectLoaded();
    }

    private synchronized void importProject(File file) throws ColtException {
        if (currentProject != null) {
            unload();
        }

        String handlerId = "AS";
        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new ColtException("Can't load the handler for the project type " + handlerId);
        }

        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.importProject(file);
        currentProject.setPath(file.getPath());

        ChangingMonitor.getInstance().reset();

        fireProjectLoaded();
    }

    public synchronized void unload() throws ColtException {
        // TODO: implement
        fireProjectClosed();
    }

    public synchronized void dispose() {
        try {
            unload();
        } catch (ColtException e) {
            // ignore
        }

        projectListeners.clear();
        currentProject = null;
    }

}
