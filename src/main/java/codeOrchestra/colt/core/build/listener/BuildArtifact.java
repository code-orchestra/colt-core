package codeOrchestra.colt.core.build.listener;

/**
 * @author Alexander Eliseyev
 */
public interface BuildArtifact {

    boolean isIncremental();

    String getPath();
}
