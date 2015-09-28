package codeOrchestra.colt.core;

/**
 * @author Alexander Eliseyev
 */
public class ColtException extends Exception {

    public ColtException(String message) {
        super(message);
    }

    public ColtException(String message, Throwable cause) {
        super(message, cause);
    }

}