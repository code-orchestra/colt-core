package codeOrchestra.colt.core.session.listener;

import codeOrchestra.colt.core.session.LiveCodingSession;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingListener {
    void onSessionStart(LiveCodingSession session);
    void onSessionEnd(LiveCodingSession session);
    void onSessionPause();
    void onSessionResume();
    void onCodeUpdate();
}