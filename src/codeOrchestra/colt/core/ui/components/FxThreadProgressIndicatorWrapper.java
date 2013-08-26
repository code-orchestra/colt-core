package codeOrchestra.colt.core.ui.components;

import javafx.application.Platform;

/**
 * @author Alexander Eliseyev
 */
public class FxThreadProgressIndicatorWrapper implements IProgressIndicator {

    private IProgressIndicator progressIndicator;

    public FxThreadProgressIndicatorWrapper(IProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    @Override
    public void start() {
        if (Platform.isFxApplicationThread()) {
            progressIndicator.start();
        } else {
            Platform.runLater(progressIndicator::start);
        }
    }

    @Override
    public void stop() {
        if (Platform.isFxApplicationThread()) {
            progressIndicator.stop();
        } else {
            Platform.runLater(progressIndicator::stop);
        }
    }

    @Override
    public void setProgress(int percents) {
        if (Platform.isFxApplicationThread()) {
            progressIndicator.setProgress(percents);
        } else {
            Platform.runLater(() -> {
                progressIndicator.setProgress(percents);
            });
        }
    }

    @Override
    public void setText(String text) {
        if (Platform.isFxApplicationThread()) {
            progressIndicator.setText(text);
        } else {
            Platform.runLater(() -> {
                progressIndicator.setText(text);
            });
        }
    }
}
