package codeOrchestra.colt.core.session.sourcetracking;

import java.io.File;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class SourcesTracker {

    private List<File> sourceDirs;

	private SourcesState state;
	
	public SourcesTracker(List<File> sourceDirs) {
		this.sourceDirs = sourceDirs;
		
		capture();
	}

	private void capture() {
		this.state = SourcesState.capture(sourceDirs);
	}
	
	public List<SourceFile> getChangedFiles() {
		SourcesState oldState = state;
		
		capture();
		
		return state.getChangedFiles(oldState);
	}
	
}
