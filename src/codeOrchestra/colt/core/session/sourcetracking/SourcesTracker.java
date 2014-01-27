package codeOrchestra.colt.core.session.sourcetracking;

import java.io.File;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class SourcesTracker {
    private static final int CAPTURE_COUNTER = 100;

    private List<File> sourceDirs;

	private SourcesState state;

    private int counterForCapture = 0;
	
	public SourcesTracker(List<File> sourceDirs) {
		this.sourceDirs = sourceDirs;
        counterForCapture = CAPTURE_COUNTER;

		capture();
	}

	private void capture() {
		this.state = SourcesState.capture(sourceDirs);
	}
	
	public List<SourceFile> getChangedFiles() {
		SourcesState oldState = state;

        if (counterForCapture == 0) {
            counterForCapture = CAPTURE_COUNTER;
            SourcesState.resetFilesTree();
        }
        counterForCapture--;

        capture();
		
		return state.getChangedFiles(oldState);
	}
	
}
