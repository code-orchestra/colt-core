package codeOrchestra.colt.core;

import codeOrchestra.colt.core.controller.COLTController;
import codeOrchestra.colt.core.launch.LiveLauncher;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.logging.LoggerService;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.model.listener.ProjectListener;
import codeOrchestra.colt.core.rpc.COLTRemoteService;
import codeOrchestra.colt.core.session.sourcetracking.SourceFileFactory;
import groovy.util.slurpersupport.GPathResult;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingLanguageHandler<P extends COLTProject> {

    String getId();

    String getName();

    P parseProject (GPathResult gPathResult, String projectPath);

    P createProject(String pName, File pFile);

    P importProject(File file);

    P getCurrentProject();

    void initHandler();

    void disposeHandler();

    // Listeners

    void fireProjectLoaded();

    void fireProjectClosed();

    void addProjectListener(ProjectListener<P> projectListener);

    void removeProjectListener(ProjectListener<P> projectListener);

    // Logger

    LoggerService getLoggerService();

    // UI

    Node getPane() throws Exception;

    // Services

    COLTController<P> createCOLTController();

    COLTRemoteService<P> createRPCService();

    LiveLauncher<P> createLauncher();

    LiveCodingManager<P> createLiveCodingManager();

    SourceFileFactory createSourceFileFactory();


}
