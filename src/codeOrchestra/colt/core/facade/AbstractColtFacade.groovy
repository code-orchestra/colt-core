package codeOrchestra.colt.core.facade

import codeOrchestra.colt.core.ui.ApplicationGUI

/**
 * @author Dima Kruk
 */
abstract class AbstractColtFacade implements ColtFacade {
    protected ApplicationGUI applicationGUI

    AbstractColtFacade(ApplicationGUI value) {
        applicationGUI = value
    }

    @Override
    void runSession() {
        applicationGUI.runSession()
    }

    @Override
    void runProductionBuild() {
        applicationGUI.runBuild()
    }
}
