package codeOrchestra.colt.core.facade;

import codeOrchestra.colt.core.ColtService;

/**
 * @author Alexander Eliseyev
 */
public interface ColtFacade extends ColtService {

    // Session

    void runSession();

    void stopSession();

    void pauseSession();

    void resumeSession();

    void restartSession();

    // Connections

    void openNewConnection();

    void closeAllConnections();

    // Production

    void runProductionBuild();

}
