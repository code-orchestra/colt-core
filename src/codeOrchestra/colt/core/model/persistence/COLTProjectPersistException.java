package codeOrchestra.colt.core.model.persistence;

/**
 * @author Alexander Eliseyev
 */
public class COLTProjectPersistException extends Exception {

    public COLTProjectPersistException() {
    }

    public COLTProjectPersistException(String message) {
        super(message);
    }

    public COLTProjectPersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public COLTProjectPersistException(Throwable cause) {
        super(cause);
    }

    public COLTProjectPersistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
