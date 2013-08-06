package codeOrchestra.colt.core.logging;

import codeOrchestra.colt.core.COLTService;
import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class Logger implements COLTService {

    private static Logger DEFAULT_LOGGER = new Logger() {
        @Override
        public void log(String message, List<String> scopeIds, long timestamp, Level level, String stackTrace) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(new Date(timestamp)).append("]");
            sb.append(" ");
            if (scopeIds != null && !scopeIds.isEmpty()) {
                sb.append(StringUtils.join(scopeIds, ", "));
            }
            sb.append(" ");
            sb.append(message);
        }
    };

    private static final List<String> DEFAULT_SCOPES = new ArrayList<String>() {{
        add("0");
    }};

    public static synchronized Logger getLogger(String source) {
        LiveCodingLanguageHandler currentHandler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        if (currentHandler == null) {
            return DEFAULT_LOGGER;
        }
        return currentHandler.getLogger(source);
    }

    public static synchronized Logger getLogger(Class clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public void info(String message, List<String> scopeIds, long timestamp) {
        log(message, scopeIds, timestamp, Level.INFO);
    }

    public void warning(String message, List<String> scopeIds, long timestamp) {
        log(message, scopeIds, timestamp, Level.WARN);
    }

    public void error(String message, List<String> scopeIds, long timestamp, String stackTrace) {
        log(message, scopeIds, timestamp, Level.ERROR, stackTrace);
    }

    public void error(String message) {
        log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
    }

    public void error(Throwable t) {
        log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
    }

    public void info(String message) {
        log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.INFO);
    }

    public void info(Throwable t) {
        log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.INFO);
    }

    public void warning(String message) {
        log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.WARN);
    }

    public void debug(Throwable t) {
        log(t.getMessage(), DEFAULT_SCOPES, System.currentTimeMillis(), Level.DEBUG);
    }

    public void debug(String message) {
        log(message, DEFAULT_SCOPES, System.currentTimeMillis(), Level.DEBUG);
    }

    private void log(String message, List<String> scopeIds, long timestamp, Level level) {
        log(message, scopeIds, timestamp, level, null);
    }

    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            error(message);
        }
    }

    public abstract void log(String message, List<String> scopeIds, long timestamp, Level level, String stackTrace);

}
