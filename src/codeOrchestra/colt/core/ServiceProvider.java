package codeOrchestra.colt.core;

import codeOrchestra.colt.core.launch.LiveLauncher;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.session.sourcetracking.SourceFileFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class ServiceProvider {

    public static List<Class<? extends COLTService>> KNOWN_SERVICES = new ArrayList<Class<? extends COLTService>>() {{
        add(LiveLauncher.class);
        add(LiveCodingManager.class);
        add(SourceFileFactory.class);
        add(Logger.class);
    }};

    public static <T extends COLTService> T get(Class<T> clazz) {
        Class<? extends COLTService> existingService = null;
        for (Class<? extends COLTService> knownService : KNOWN_SERVICES) {
            if (clazz.getSimpleName().equals(knownService.getSimpleName())) {
                existingService = knownService;
            }
        }
        if (existingService == null) {
            throw new IllegalArgumentException("Unknown service: " + clazz.getCanonicalName());
        }

        LiveCodingLanguageHandler currentHandler = LiveCodingHandlerManager.getInstance().getCurrentHandler();
        for (Method method : currentHandler.getClass().getMethods()) {
            if (method.getName().startsWith("get") && method.getReturnType().equals(existingService)) {
                try {
                    return (T) method.invoke(currentHandler);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {

    }

}
