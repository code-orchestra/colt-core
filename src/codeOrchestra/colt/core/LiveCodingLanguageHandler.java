package codeOrchestra.colt.core;

import codeOrchestra.colt.core.controller.COLTController;
import codeOrchestra.colt.core.launch.LiveLauncher;
import codeOrchestra.colt.core.logging.LoggerService;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.rpc.COLTRemoteService;
import codeOrchestra.colt.core.session.sourcetracking.SourceFileFactory;
import codeOrchestra.colt.core.ui.components.ICOLTProgressIndicator;
import groovy.util.slurpersupport.GPathResult;
import javafx.scene.Node;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingLanguageHandler<P extends COLTProject> {

    String getId();

    String getName();

    P parseProject(GPathResult gPathResult, String projectPath);

    P createProject(String pName, File pFile);

    P importProject(File file);

    P getCurrentProject();

    void initHandler();

    void disposeHandler();

    // Logger

    LoggerService getLoggerService();

    // UI

    Node getPane() throws Exception;

    ICOLTProgressIndicator getProgressIndicator();

    // Services

    COLTController<P> createCOLTController();

    COLTRemoteService<P> createRPCService();

    LiveLauncher<P> createLauncher();

    LiveCodingManager<P, ?> createLiveCodingManager();

    SourceFileFactory createSourceFileFactory();


}
