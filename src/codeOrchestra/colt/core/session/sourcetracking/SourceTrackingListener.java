package codeOrchestra.colt.core.session.sourcetracking;

/**
 * @author Alexander Eliseyev
 */
public interface SourceTrackingListener<S extends SourceFile> {

    void onFileChanged(S sourceFile);

}
