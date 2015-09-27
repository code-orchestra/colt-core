package codeOrchestra.colt.core.build;

/**
 * @author Alexander Eliseyev
 */
public class BuildException extends Exception {

    public BuildException(String message) {
        super(message);
    }

    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildException(Throwable cause) {
        super(cause);
    }

}
