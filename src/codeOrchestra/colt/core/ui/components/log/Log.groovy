package codeOrchestra.colt.core.ui.components.log

import codeOrchestra.colt.core.logging.Level
import codeOrchestra.colt.core.logging.LoggerService

/**
 * @author Dima Kruk
 */
class Log implements LoggerService {

    @Lazy static Log instance = new Log()
    @Lazy LogWebView logWebView = new LogWebView()

    private Log() {
//        GAController.instance.registerPage(logWebView, "/as/asLog.html", "asLog")
    }

    @Override
    synchronized void log(String source, String message, List<String> scopeIds, long timestamp, Level level, String stackTrace) {
        logWebView.logMessages.add(new LogMessage(source, level, message, timestamp, stackTrace))
    }

    @Override
    synchronized void clear(Level level) {
        ArrayList<LogMessage> newMessages = logWebView.logMessages.asList().grep{LogMessage m ->  m.level == level }
        logWebView.logMessages.clear()
        logWebView.logMessages.addAll(newMessages)
    }
}
