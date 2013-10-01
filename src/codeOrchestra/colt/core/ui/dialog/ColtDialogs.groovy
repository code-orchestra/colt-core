package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.util.ThreadUtils
import javafx.event.Event
import javafx.scene.image.Image
import javafx.scene.media.AudioClip
import javafx.stage.Window

/**
 * @author Dima Kruk
 */
class ColtDialogs {
    static boolean isShowing = false

    public static void showCloseProjectDialog(Window owner, Event event) {
        SaveDialog dialog = new SaveDialog(owner)
        dialog.show(event)
    }

    static void showException(Window owner, Throwable exception, String massage = null) {
        ExceptionDialog dialog = new ExceptionDialog(owner)
        dialog.initException(exception, massage)

        dialog.show()
    }

    static void showError(Window owner, String title, String massage, String comment = "") {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")
        dialog.title = title
        dialog.message = massage
        dialog.comment = comment

        dialog.show()
    }

    static void showDemoModeError(Window owner, String title, String massage, String comment = "") {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")
        dialog.title = title
        dialog.message = massage
        dialog.comment = comment

        AudioClip clip = new AudioClip(Dialog.class.getResource("sounds/cash_register.wav").toString())
        clip.play()

        showOneDialog(dialog)
    }

    static void showWarning(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/warning-48x48.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }

    static void showInfo(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/info-48x48.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }

    static void showApplicationMessage(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/ico-colt.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }

    static private void showOneDialog(Dialog dialog) {
        if (isShowing) {
            return
        }

        isShowing = true
        dialog.showWithClosure({afterShow()})
    }

    protected static void afterShow() {
        new Thread() {
            @Override
            void run() {
                ThreadUtils.sleep(100)
                isShowing = false
            }
        }.start()
    }
}
