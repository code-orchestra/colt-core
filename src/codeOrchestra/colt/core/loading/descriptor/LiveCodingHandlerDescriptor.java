package codeOrchestra.colt.core.loading.descriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingHandlerDescriptor {

    public static LiveCodingHandlerDescriptor fromXML(Document document) {
        Element classElement = (Element) document.getDocumentElement().getElementsByTagName("class").item(0);
        return new LiveCodingHandlerDescriptor(classElement.getTextContent());
    }

    public LiveCodingHandlerDescriptor(String handlerClassName) {
        this.handlerClassName = handlerClassName;
    }

    private String handlerClassName;

    public String getHandlerClassName() {
        return handlerClassName;
    }
}
