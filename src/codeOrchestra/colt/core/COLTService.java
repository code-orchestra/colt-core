package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public interface COLTService<P extends COLTProject> {

    void dispose();

}
