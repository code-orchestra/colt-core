package codeOrchestra.colt.core.rpc.security.ui

import codeOrchestra.colt.core.rpc.security.ColtRemoteSecurityListener
import codeOrchestra.colt.core.rpc.security.ColtRemoteSecurityManager
import javafx.event.ActionEvent
import javafx.scene.Node as FXNode
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import org.controlsfx.control.NotificationPane
import org.controlsfx.control.action.AbstractAction

/**
 * @author Dima Kruk
 */
class ShortCodeNotification {
    private static NotificationPane  notificationPane = new NotificationPane()

    static NotificationPane initNotification(FXNode content) {
        notificationPane.setContent(content)

        ColtRemoteSecurityManager.instance.addListener(new ColtRemoteSecurityListener() {
            @Override
            void onNewRequest(String requestor, String shortCode) {
                TextFlow textFlow = new TextFlow()
                textFlow.children.add(new Text("'" + requestor + "' has requested authorization to use COLT API.\nTo authorize, enter the following code into that tool: "))
                Text shortCodeText = new Text(shortCode)
                shortCodeText.setFill(Color.BLUE)
                textFlow.children.add(shortCodeText)
                notificationPane.setGraphic(textFlow)

                notificationPane.actions.clear()
                AbstractAction action = new AbstractAction("Copy") {
                    @Override
                    void execute(ActionEvent actionEvent) {
                        ClipboardContent clipboardContent = new ClipboardContent()
                        clipboardContent.putString(shortCode)
                        Clipboard.getSystemClipboard().setContent(clipboardContent)
                    }
                }
                notificationPane.actions.add(action)
                notificationPane.show()
            }

            @Override
            void onSuccessfulActivation(String shortCode) {
                if (notificationPane.isShowing()) {
                    notificationPane.hide()
                }
            }
        })

        return notificationPane
    }
}
