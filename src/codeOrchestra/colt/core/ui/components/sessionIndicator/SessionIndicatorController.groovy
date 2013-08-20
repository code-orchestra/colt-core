package codeOrchestra.colt.core.ui.components.sessionIndicator

import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.session.LiveCodingSession
import codeOrchestra.colt.core.session.listener.LiveCodingListener
import javafx.scene.image.Image
import javafx.scene.image.ImageView

/**
 * @author Dima Kruk
 */
class SessionIndicatorController implements LiveCodingListener{

    private static SessionIndicatorController ourInstance = new SessionIndicatorController()

    public static SessionIndicatorController getInstance() {
        return ourInstance
    }

    ImageView indicator

    Image on
    Image off

    ArrayList<LiveCodingSession> sessions = new ArrayList<>()

    private SessionIndicatorController() {
        on = new Image(getClass().getResource("session-indicator-on.png").toString())
        off = new Image(getClass().getResource("session-indicator-off.png").toString())
    }

    void setIndicator(ImageView value) {
        indicator = value;
        indicator.setImage(off)
    }

    @Override
    void onSessionStart(LiveCodingSession session) {
        if(!sessions.contains(session)) {
            sessions.add(session)
        }
        indicator?.setImage(on)
    }

    @Override
    void onSessionEnd(LiveCodingSession session) {
        sessions.remove(session)
        if (sessions.size() == 0) {
            indicator?.setImage(off)
        }
    }

    @Override
    void onSessionPause() {
        //todo: implement
    }

    @Override
    void onSessionResume() {
        //todo: implement
    }

    @Override
    void onAutoPausedSessionResume() {
        //todo: implement
    }
}
