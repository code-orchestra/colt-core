package codeOrchestra.colt.core.logging;

import java.util.List;

/**
 * @author Alexander Elisetev
 */
public interface LoggerService {

    void log(String source, String message, List<String> scopeIds, long timestamp, Level level, String stackTrace);

    void clear(Level level);

}
