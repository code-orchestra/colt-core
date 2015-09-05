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
        List<LiveCodingHandlerDescriptor> result = new ArrayList<>();

        NodeList coldHandlersList = document.getDocumentElement().getElementsByTagName("coltHandler");
        for (int i = 0; i < coldHandlersList.getLength(); i++) {
            Element coltHandlerElement = (Element) coldHandlersList.item(i);

            if (coltHandlerElement.hasAttribute("classpath")) {
                result.add(new LiveCodingHandlerDescriptor(
                        coltHandlerElement.getAttribute("id"),
                        coltHandlerElement.getAttribute("class"),
                        coltHandlerElement.getAttribute("classpath")
                ));
            } else {
                result.add(new LiveCodingHandlerDescriptor(
                        coltHandlerElement.getAttribute("id"),
                        coltHandlerElement.getAttribute("class")
                ));
            }
        }

        return result;
    }

    public LiveCodingHandlerDescriptor(String id, String handlerClassName) {
        this(id, handlerClassName, null);
    }

    public LiveCodingHandlerDescriptor(String id, String handlerClassName, String classPath) {
        this.id = id;
        this.handlerClassName = handlerClassName;
        this.classPath = classPath;
    }

    private String id;
    private String handlerClassName;
    private String classPath;

    public String getClassPath() {
        return classPath;
    }

    public String getId() {
        return id;
    }

    public String getHandlerClassName() {
        return handlerClassName;
    }
}
