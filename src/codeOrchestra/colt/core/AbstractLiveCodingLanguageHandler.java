package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingLanguageHandler<P extends COLTProject> implements LiveCodingLanguageHandler<P> {

    @Override
    public P getCurrentProject() {
        return (P) COLTProjectManager.getInstance().getCurrentProject();
    }
}
