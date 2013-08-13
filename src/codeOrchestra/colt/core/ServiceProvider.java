package codeOrchestra.colt.core;

import codeOrchestra.colt.core.controller.COLTController;
import codeOrchestra.colt.core.launch.LiveLauncher;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.logging.LoggerService;
import codeOrchestra.colt.core.rpc.COLTRemoteService;
import codeOrchestra.colt.core.session.sourcetracking.SourceFileFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class ServiceProvider {

    private static Map<String, COLTService> cache = new HashMap<String, COLTService>();

    public static synchronized void dispose() {
        for (COLTService coltService : cache.values()) {
            coltService.dispose();
        }

        cache.clear();
    }

    public static List<Class<? extends COLTService>> KNOWN_SERVICES = new ArrayList<Class<? extends COLTService>>() {{
        add(LiveLauncher.class);
        add(LiveCodingManager.class);
        add(SourceFileFactory.class);
        add(COLTRemoteService.class);
        add(COLTController.class);
    }};

    public static synchronized <T extends COLTService> T get(Class<T> clazz) {
        if (cache.containsKey(clazz.getCanonicalName())) {
            return (T) cache.get(clazz.getCanonicalName());
        }

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
        if (currentHandler == null) {
            return null;
        }

        for (Method method : currentHandler.getClass().getMethods()) {
            if (method.getName().startsWith("create") && method.getReturnType().equals(existingService)) {
                try {
                    T service = (T) method.invoke(currentHandler);
                    cache.put(existingService.getCanonicalName(), service);
                    return service;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }


}
