package codeOrchestra.colt.core.execution;

import codeOrchestra.colt.core.logging.Level;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.util.process.ProcessAdapter;

/**
 * @author Alexander Eliseyev
 */
public class LoggingProcessListener extends ProcessAdapter {

    private Logger logger;
    private Level levelOverride;

    public LoggingProcessListener(String loggerName, Level levelOverride) {
        this.logger = Logger.getLogger(loggerName);
        this.levelOverride = levelOverride;
    }

    public LoggingProcessListener(String loggerName) {
        this(loggerName, null);
    }

    @Override
    public void onTextAvailable(ProcessEvent event, String outputType) {
        String text = event.getText().trim();

        if ("finished.with.exit.code.text.message".equals(text)) {
            logger.live("Finished with exit code " + event.getExitCode());
            return;
        }

        if (levelOverride != null) {
            logger.log(text, levelOverride);
        } else {
            switch (outputType) {
                case "SYSTEM":
                    logger.info(text);
                    break;
                case "STDERR":
                    logger.error(text);
                    break;
                default:
                    logger.info(text);
                    break;
            }
        }
    }

}
