package codeOrchestra.colt.core.loading.impl;

import codeOrchestra.colt.core.loading.LiveCodingHandlerLoader;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.descriptor.LiveCodingHandlerDescriptor;
import codeOrchestra.util.XMLUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Dev-only!
 *
 * @author Alexander Eliseyev
 */
public class IdeaDevLiveCodingHandlerLoader extends AbstractLiveCodingHandlerLoader implements LiveCodingHandlerLoader {

    @Override
    public HandlerWrapper getHandlerMetadata(String id) throws LiveCodingHandlerLoadingException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(COLT_HANDLER_XML);
        Document descriptorDocument;
        try {
            descriptorDocument = XMLUtils.streamToDocument(resourceAsStream);
        } catch (IOException e) {
            throw new LiveCodingHandlerLoadingException("Can't load live coding handler descriptor from local IDA handlers list", e);
        }

        LiveCodingHandlerDescriptor liveCodingHandlerDescriptor = getLiveCodingHandlerDescriptor(id, "local IDEA handlers list", descriptorDocument);

        File file = new File(liveCodingHandlerDescriptor.getClassPath());
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

        return new HandlerWrapper(descriptorDocument, classLoader, file);
    }

}
