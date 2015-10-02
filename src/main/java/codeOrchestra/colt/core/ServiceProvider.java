package codeOrchestra.colt.core;

import codeOrchestra.colt.core.controller.ColtController;
import codeOrchestra.colt.core.facade.ColtFacade;
import codeOrchestra.colt.core.launch.LiveLauncher;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.rpc.ColtRemoteService;
import codeOrchestra.colt.core.session.sourcetracking.SourceFileFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Alexander Eliseyev
 */
public class ServiceProvider {

    private static Map<String, ColtService> cache = new HashMap<>();

    public static synchronized void dispose() {
        cache.values().forEach(ColtService::dispose);
        cache.clear();
    }

    public static List<Class<? extends ColtService>> KNOWN_SERVICES = new ArrayList<Class<? extends ColtService>>() {{
        add(LiveLauncher.class);
        add(LiveCodingManager.class);
        add(SourceFileFactory.class);
        add(ColtRemoteService.class);
        add(ColtController.class);
        add(ColtFacade.class);
    }};

    public static synchronized <T extends ColtService> T get(Class<T> clazz) {
        if (cache.containsKey(clazz.getCanonicalName())) {
            return (T) cache.get(clazz.getCanonicalName());
        }

        Class<? extends ColtService> existingService = null;
        for (Class<? extends ColtService> knownService : KNOWN_SERVICES) {
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

                    if (service != null) {
                        cache.put(existingService.getCanonicalName(), service);
                    }

                    return service;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}