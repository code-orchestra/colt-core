package codeOrchestra.colt.core.tasks;

import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.ui.components.IProgressIndicator;

/**
 * @author Alexander Eliseyev
 */
public abstract class ColtTaskWithProgress<R> extends ColtTask<R> {

    @Override
    protected void onOK(R result) {
    }

    @Override
    protected void onFail() {
    }

    @Override
    protected final R call() throws Exception {
        IProgressIndicator progressIndicator = LiveCodingHandlerManager.getInstance().getCurrentHandler().getProgressIndicator();
        progressIndicator.start();
        try {
            return call(progressIndicator);
        } catch (Throwable t) {
            ErrorHandler.handle(t);
            return null;
        } finally {
            progressIndicator.stop();
        }
    }

    protected abstract R call(IProgressIndicator progressIndicator);

}
