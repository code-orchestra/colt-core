package codeOrchestra.colt.core.loading.impl;

import codeOrchestra.colt.core.loading.LiveCodingHandlerLoader;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.util.XMLUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Alexander Eliseyev
 */
public class JarLiveCodingHandlerLoader extends AbstractLiveCodingHandlerLoader implements LiveCodingHandlerLoader {

    // TODO: make dynamic
    private static String getJarHandlersLocation() {
        return "/Users/eliseyev/TMP/colt_handlers/";
    }

    @Override
    protected AbstractLiveCodingHandlerLoader.HandlerWrapper getHandlerMetadata(String id) throws LiveCodingHandlerLoadingException {
        File file  = new File(getJarHandlersLocation() + id + ".jar");
        if (!file.exists()) {
            throw new LiveCodingHandlerLoadingException("Can't reach the live coding handler at " + file.getPath());
        }

        URL url;
        try {
            url = file.toURL();
        } catch (MalformedURLException e) {
            throw new LiveCodingHandlerLoadingException("Can't reach the live coding handler at " + file.getPath(), e);
        }
        URL[] urls = new URL[] { url };
        ClassLoader classLoader = new URLClassLoader(urls);

        InputStream resourceAsStream = classLoader.getResourceAsStream(COLT_HANDLER_XML);
        Document descriptorDocument;
        try {
            descriptorDocument = XMLUtils.streamToDocument(resourceAsStream);
        } catch (IOException e) {
            throw new LiveCodingHandlerLoadingException("Can't load live coding handler descriptor from " + file.getPath(), e);
        }

        return new HandlerWrapper(descriptorDocument, classLoader, file);
    }

}
