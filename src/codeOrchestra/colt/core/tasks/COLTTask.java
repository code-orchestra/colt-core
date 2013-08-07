package codeOrchestra.colt.core.tasks;

import javafx.scene.control.ProgressIndicator;

/**
 * @author Alexander Eliseyev
 */
public interface COLTTask<R> {

    R doTask(ProgressIndicator progressIndicator);

}
