package codeOrchestra.colt.core.rpc.model;

import codeOrchestra.colt.core.session.LiveCodingSession;

/**
 * @author Alexander Eliseyev
 */
public class ColtConnection {

    private String clientId;

    public ColtConnection(LiveCodingSession session) {
        this.clientId = session.getClientId();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}