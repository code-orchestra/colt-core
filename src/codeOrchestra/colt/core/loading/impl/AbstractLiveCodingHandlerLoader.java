package codeOrchestra.colt.core.loading.impl;

import codeOrchestra.colt.core.LiveCodingLanguageHandler;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoader;
import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.descriptor.LiveCodingHandlerDescriptor;
import org.w3c.dom.Document;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingHandlerLoader implements LiveCodingHandlerLoader {

    protected abstract HandlerWrapper getHandlerMetadata(String id) throws LiveCodingHandlerLoadingException;

    @Override
    public final LiveCodingLanguageHandler load(String id) throws LiveCodingHandlerLoadingException {
        HandlerWrapper handlerMetadata = getHandlerMetadata(id);
        File handlerLocation = handlerMetadata.getHandlerLocation();

        LiveCodingHandlerDescriptor liveCodingHandlerDescriptor = null;
        for (LiveCodingHandlerDescriptor descriptorFromXML : LiveCodingHandlerDescriptor.fromXML(handlerMetadata.getDescriptorDocument())) {
            if (id.equals(descriptorFromXML.getId())) {
                liveCodingHandlerDescriptor = descriptorFromXML;
                break;
            }
        }
        if (liveCodingHandlerDescriptor == null) {
            throw new LiveCodingHandlerLoadingException("Can't locate live coding handler " + id + " from " + handlerLocation.getPath());
        }

        Class<LiveCodingLanguageHandler> handlerClass;
        try {
            handlerClass = (Class<LiveCodingLanguageHandler>) handlerMetadata.getHandlerClassLoader().loadClass(liveCodingHandlerDescriptor.getHandlerClassName());
        } catch (ClassNotFoundException e) {
            throw new LiveCodingHandlerLoadingException("Can't load live coding handler " + id + " from " + handlerLocation.getPath(), e);
        }

        try {
            return handlerClass.newInstance();
        } catch (InstantiationException e) {
            throw new LiveCodingHandlerLoadingException("Can't load live coding handler " + id + " from " + handlerLocation.getPath(), e);
        } catch (IllegalAccessException e) {
            throw new LiveCodingHandlerLoadingException("Can't load live coding handler " + id + " from " + handlerLocation.getPath(), e);
        }
    }

    public static class HandlerWrapper {
        private Document descriptorDocument;
        private ClassLoader handlerClassLoader;
        private File handlerLocation;

        public HandlerWrapper(Document descriptorDocument, ClassLoader handlerClassLoader, File handlerLocation) {
            this.descriptorDocument = descriptorDocument;
            this.handlerClassLoader = handlerClassLoader;
            this.handlerLocation = handlerLocation;
        }

        private Document getDescriptorDocument() {
            return descriptorDocument;
        }

        private ClassLoader getHandlerClassLoader() {
            return handlerClassLoader;
        }

        private File getHandlerLocation() {
            return handlerLocation;
        }
    }

}
