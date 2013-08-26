package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public interface ColtService<P extends Project> {

    void dispose();

}
