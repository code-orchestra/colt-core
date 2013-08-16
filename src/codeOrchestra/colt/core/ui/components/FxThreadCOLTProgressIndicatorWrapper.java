package codeOrchestra.colt.core.ui.components;

import javafx.application.Platform;

/**
 * @author Alexander Eliseyev
 */
public class FxThreadCOLTProgressIndicatorWrapper implements ICOLTProgressIndicator {

    private ICOLTProgressIndicator progressIndicator;

    public FxThreadCOLTProgressIndicatorWrapper(ICOLTProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    @Override
    public void start() {
        Platform.runLater(progressIndicator::start);
    }

    @Override
    public void stop() {
        Platform.runLater(progressIndicator::stop);
    }

    @Override
    public void setProgress(int percents) {
        Platform.runLater(() -> {
            progressIndicator.setProgress(percents);
        });
    }

    @Override
    public void setText(String text) {
        Platform.runLater(() -> {
            progressIndicator.setText(text);
        });
    }
}
