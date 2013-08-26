package codeOrchestra.colt.core;

/**
 * @author Alexander Eliseyev
 */
public class ColtException extends Exception {

    public ColtException() {
    }

    public ColtException(String message) {
        super(message);
    }

    public ColtException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColtException(Throwable cause) {
        super(cause);
    }

    public ColtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
