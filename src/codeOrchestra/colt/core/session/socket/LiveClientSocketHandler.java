package codeOrchestra.colt.core.session.socket;

/**
 * @author Alexander Eliseyev
 */
public interface LiveClientSocketHandler {

    void handle(String str);

    void dispose();

}
