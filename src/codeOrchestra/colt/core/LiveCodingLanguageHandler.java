package codeOrchestra.colt.core;

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

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingLanguageHandler<P extends COLTProject> {

    String getId();

    String getName();

    P parseProject (GPathResult gPathResult);

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

    COLTRemoteService createRPCService();

    LiveLauncher<P> createLauncher();

    LiveCodingManager<P> createLiveCodingManager();

    SourceFileFactory createSourceFileFactory();


}
