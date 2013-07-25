package codeOrchestra.colt.core.loading.descriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class LiveCodingHandlerDescriptor {

    public static List<LiveCodingHandlerDescriptor> fromXML(Document document) {
        List<LiveCodingHandlerDescriptor> result = new ArrayList<LiveCodingHandlerDescriptor>();

        NodeList coldHandlersList = document.getDocumentElement().getElementsByTagName("coldHandler");
        for (int i = 0; i < coldHandlersList.getLength(); i++) {
            Element coltHandlerElement = (Element) coldHandlersList.item(i);
            result.add(new LiveCodingHandlerDescriptor(
                    coltHandlerElement.getAttribute("id"),
                    coltHandlerElement.getAttribute("class")
            ));
        }

        return result;
    }

    private LiveCodingHandlerDescriptor(String id, String handlerClassName) {
        this.id = id;
        this.handlerClassName = handlerClassName;
    }

    private String id;
    private String handlerClassName;

    public String getId() {
        return id;
    }

    public String getHandlerClassName() {
        return handlerClassName;
    }
}
