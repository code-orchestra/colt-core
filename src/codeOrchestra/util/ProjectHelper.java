package codeOrchestra.util;

import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public final class ProjectHelper {

    public static boolean isLegacyProject(String projectFileContent) {
        String trimmedContent = projectFileContent.trim();
        if (trimmedContent.startsWith("<?xml") || trimmedContent.startsWith("<coltProject") || trimmedContent.startsWith("<xml")) {
            return false;
        }
        return true;
    }

    public static <P extends COLTProject> P getCurrentProject() {
        return (P) LiveCodingHandlerManager.getInstance().getCurrentHandler().getCurrentProject();
    }

}
