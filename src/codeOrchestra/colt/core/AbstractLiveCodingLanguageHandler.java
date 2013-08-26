package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingLanguageHandler<P extends Project> implements LiveCodingLanguageHandler<P> {

    @Override
    public P getCurrentProject() {
        return (P) ColtProjectManager.getInstance().getCurrentProject();
    }
}
