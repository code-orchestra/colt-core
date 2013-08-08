package codeOrchestra.colt.core.logging;

import codeOrchestra.colt.core.COLTService;
import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.util.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class Logger implements COLTService {

    org.slf4j.Logger slf4jlogger = LoggerFactory.getLogger(Logger.class);

    private static Logger HEADLESS_LOGGER = new Logger() {
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
            if (stackTrace != null) {
                sb.append(" ");
                sb.append(stackTrace);
            }

            String fullMessage = sb.toString();

            switch (level) {
                case DEBUG:
                    slf4jlogger.debug(fullMessage);
                    break;
                case ERROR:
                case FATAL:
                    slf4jlogger.error(fullMessage);
                    break;
                case TRACE:
                case INFO:
                case ALL:
                    slf4jlogger.info(fullMessage);
                    break;
                case WARN:
                    slf4jlogger.warn(fullMessage);
                    break;
                default:
                    break;
            }
        }
    };

    private static final List<String> DEFAULT_SCOPES = new ArrayList<String>() {{
        add("0");
    }};

    public static synchronized Logger getLogger(String source) {
        LiveCodingLanguageHandler currentHandler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        if (currentHandler == null) {
            return HEADLESS_LOGGER;
        }
        return new DefaultLogger(currentHandler, source);
    }

    private static class DefaultLogger extends Logger {

        private final LiveCodingLanguageHandler currentHandler;
        private String source;

        private DefaultLogger(LiveCodingLanguageHandler currentHandler, String source) {
            this.currentHandler = currentHandler;
            this.source = source;
        }

        private LoggerService getLoggerService() {
            return this.currentHandler.getLoggerService();
        }

        @Override
        public void log(String message, List<String> scopeIds, long timestamp, Level level, String stackTrace) {
            LoggerService loggerService = getLoggerService();

            if (loggerService == null) {
                HEADLESS_LOGGER.log(message, scopeIds, timestamp, level);
            } else {
                loggerService.log(source, message, scopeIds, timestamp, level, stackTrace);
            }
        }
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

    public void error(String message, Throwable t) {
        if (t.getMessage() != null) {
            log(message + ": " + t.getClass().getSimpleName() + " - " + t.getMessage(),  DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
        } else {
            log(message + ": " + t.getClass().getSimpleName(),  DEFAULT_SCOPES, System.currentTimeMillis(), Level.ERROR);
        }
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
