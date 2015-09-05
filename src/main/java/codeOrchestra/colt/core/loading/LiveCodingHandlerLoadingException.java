package codeOrchestra.colt.core.loading;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingHandlerLoadingException extends Exception {

    public LiveCodingHandlerLoadingException() {
    }

    public LiveCodingHandlerLoadingException(String message) {
        super(message);
    }

    public LiveCodingHandlerLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiveCodingHandlerLoadingException(Throwable cause) {
        super(cause);
    }

    public LiveCodingHandlerLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
