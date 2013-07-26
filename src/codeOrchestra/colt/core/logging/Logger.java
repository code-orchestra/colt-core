package codeOrchestra.colt.core.logging;

import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class Logger {

    private static final List<String> DEFAULT_SCOPES = new ArrayList<String>() {{
        add("0");
    }};

    public static synchronized Logger getLogger(String source) {
        return LiveCodingHandlerManager.getInstance().getCurrentHandler().getLogger(source);
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

    public abstract void log(String message, List<String> scopeIds, long timestamp, Level level, String stackTrace);

}
