package codeOrchestra.colt.core.ui.components

import javafx.event.EventHandler
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Tooltip

/**
 * @author Dima Kruk
 */
class COLTProgressIndicatorController implements ICOLTProgressIndicator {

    private static COLTProgressIndicatorController ourInstance = new COLTProgressIndicatorController()

    public static COLTProgressIndicatorController getInstance() {
        return ourInstance
    }

    private Tooltip tooltip
    private ProgressBar progressBar

    private COLTProgressIndicatorController() {

        progressBar = new ProgressBar()

        tooltip = new Tooltip("Label")
        tooltip.contentDisplay = ContentDisplay.BOTTOM
        tooltip.graphic = progressBar
        tooltip.onShowing = {
            tooltip.x -= 60
            tooltip.y -= 60
        } as EventHandler
    }

    private ProgressIndicator progressIndicator

    void setProgressIndicator(ProgressIndicator indicator) {
        progressIndicator = indicator
        progressIndicator.progress = -1
        indicator.tooltip = tooltip
        stop()
    }

    @Override
    void start() {
        progressIndicator?.visible = true
    }

    @Override
    void stop() {
        progressIndicator?.visible = false
    }

    @Override
    void setProgress(int percents) {
        progressBar.progress = percents * 0.01
    }

    @Override
    void setText(String text) {
        tooltip.text = text
    }
}
