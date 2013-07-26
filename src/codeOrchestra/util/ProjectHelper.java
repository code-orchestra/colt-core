package codeOrchestra.util;

import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public final class ProjectHelper {

    public static <P extends COLTProject> P getCurrentProject() {
        return (P) LiveCodingHandlerManager.getInstance().getCurrentHandler().getCurrentProject();
    }

}
