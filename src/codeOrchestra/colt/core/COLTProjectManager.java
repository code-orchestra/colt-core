package codeOrchestra.colt.core;

import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.model.COLTProjectHandlerIdParser;
import codeOrchestra.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        currentProject = handler.parseProject(coltProjectHandlerIdParser.getNode());
        currentProject.setPath(path);

        handler.fireProjectLoaded();
    }

    public synchronized void create(String handlerId, String pName, File pFile) throws COLTException {
        try {
            LiveCodingHandlerManager.getInstance().load(handlerId);
        } catch (LiveCodingHandlerLoadingException e) {
            throw new COLTException("Can't load the handler for the project type " + handlerId);
        }

        LiveCodingLanguageHandler handler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        currentProject = handler.createProject(pName);
        currentProject.setPath(pFile.getPath());

        String xml = currentProject.toXmlString();

        try {
            FileWriter fileWriter = new FileWriter(pFile);
            fileWriter.write(xml);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.fireProjectLoaded();
    }

    public synchronized void unload() throws COLTException {
        // TODO: implement
    }

}
