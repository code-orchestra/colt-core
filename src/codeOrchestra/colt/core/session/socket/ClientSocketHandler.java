package codeOrchestra.colt.core.session.socket;

/**
 * @author Alexander Eliseyev
 */
public interface ClientSocketHandler {

    void handle(String str);

    void dispose();

}
