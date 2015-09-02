package codeOrchestra.colt.core.ui.testmode

import codeOrchestra.colt.core.execution.ProcessEvent
import codeOrchestra.util.process.ProcessAdapter

/**
 * @author Dima Kruk
 */
class GitProcessListener extends ProcessAdapter{

    @Override
    void onTextAvailable(ProcessEvent event, String outputType) {
        String text = event.getText().trim()

        if ("finished.with.exit.code.text.message".equals(text)) {
            text = "Finished with exit code " + event.getExitCode()
        }

        println text
    }
}
