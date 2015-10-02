package codeOrchestra.colt.core.loading;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingHandlerLoadingException extends Exception {
    public LiveCodingHandlerLoadingException(String message) {
        super(message);
    }
    public LiveCodingHandlerLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
    public LiveCodingHandlerLoadingException(Throwable cause) {
        super(cause);
    }
}