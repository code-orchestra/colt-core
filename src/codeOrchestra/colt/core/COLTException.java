package codeOrchestra.colt.core;

/**
 * @author Alexander Eliseyev
 */
public class COLTException extends Exception {

    public COLTException() {
    }

    public COLTException(String message) {
        super(message);
    }

    public COLTException(String message, Throwable cause) {
        super(message, cause);
    }

    public COLTException(Throwable cause) {
        super(cause);
    }

    public COLTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
