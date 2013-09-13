package codeOrchestra.colt.core;

import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.model.Project;
import codeOrchestra.colt.core.model.ProjectHandlerIdParser;
import codeOrchestra.colt.core.model.listener.ProjectListener;
import codeOrchestra.colt.core.model.monitor.ChangingMonitor;
import codeOrchestra.colt.core.storage.ProjectStorageManager;
import codeOrchestra.util.DateUtils;
import codeOrchestra.util.FileUtils;
import codeOrchestra.util.ProjectHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class ColtProjectManager {

    public static final Logger getLogger() {
        return Logger.getLogger(ColtProjectManager.class);
    }

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
                ProjectStorageManager.getOrCreateProjectStorageDir();
                CodeOrchestraResourcesHttpServer.getInstance().addAlias(project.getOutputDir(), "/output");

                getLogger().info("Loaded " + project.getProjectType() + " project " + project.getName() + " on " + DateUtils.getCurrentDate());
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

        loadHandler(handlerId);

        // Parse the project
        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.parseProject(coltProjectHandlerIdParser.getNode(), path);

        ChangingMonitor.getInstance().reset();

        fireProjectLoaded();
    }

    public synchronized void save() throws ColtException {
        save(currentProject);
    }

    private void save(Project project) throws ColtException {
        File file = new File(project.getPath());
        FileWriter fileWriter;
        try {
            String projectXml = project.toXmlString();
            fileWriter = new FileWriter(file);
            fileWriter.write(projectXml);
            fileWriter.close();
            ChangingMonitor.getInstance().reset();
        } catch (IOException e) {
            throw new ColtException("Can't write COLT project file to " + file.getPath());
        }
    }

    public synchronized void create(String handlerId, String pName, File pFile, boolean load) throws ColtException {
        if (load && currentProject != null) {
            unload();
        }

        LiveCodingLanguageHandler handler = load ? loadHandler(handlerId) : getHandler(handlerId);
        Project createdProject = handler.createProject(pName, pFile, load);
        createdProject.setPath(pFile.getPath());

        if (load) {
            currentProject = createdProject;
            currentProject.setNewProject(true);
        }
        save(createdProject);

        if (load) {
            fireProjectLoaded();
        }
    }

    private synchronized void importProject(File file) throws ColtException {
        if (currentProject != null) {
            unload();
        }

        String handlerId = "AS";
        loadHandler(handlerId);

        LiveCodingLanguageHandler handler = loadHandler(handlerId);
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

    private LiveCodingLanguageHandler getHandler(String handlerId) throws ColtException {
        try {
            return LiveCodingHandlerManager.getInstance().get(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new ColtException("Can't load the handler for the project type " + handlerId, e);
        }
    }

    private LiveCodingLanguageHandler loadHandler(String handlerId) throws ColtException {
        try {
            return LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new ColtException("Can't load the handler for the project type " + handlerId, e);
        }
    }

}
