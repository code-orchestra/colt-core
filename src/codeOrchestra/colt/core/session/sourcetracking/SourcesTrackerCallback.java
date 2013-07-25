package codeOrchestra.colt.core.session.sourcetracking;

/**
 * @author Alexander Eliseyev
 */
public interface SourcesTrackerCallback {

	void sourceFileChanged(SourceFile sourceFile);
	
}
