package codeOrchestra.colt.core.session.sourcetracking;

import java.io.File;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class SourcesTrackerThread extends Thread {

	private static final long SLEEP_INTERVAL = 50;

    private boolean shouldStop;

	private SourcesTracker sourcesTracker;

	private final SourcesTrackerCallback callback;
	
	public SourcesTrackerThread(SourcesTrackerCallback callback, List<File> sourceDirs) {
		super("Source files changes tracker thread");
		this.callback = callback;
		this.sourcesTracker = new SourcesTracker(sourceDirs);
	}
	
	public void stopRightThere() {
		shouldStop = true;
	}

	@Override
	public void run() {
		while (!shouldStop) {
			try {
				Thread.sleep(SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				// do nothing
			}
			
			for (SourceFile changedFile : sourcesTracker.getChangedFiles()) {
                try {
                    callback.sourceFileChanged(changedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
}
