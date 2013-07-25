package codeOrchestra.colt.core.model.persistence;

import codeOrchestra.colt.core.model.COLTProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Alexander Eliseyev
 */
public interface COLTProjectPersistedAspect<P extends COLTProject> {

    String getAspectName();

    void persist(Document document, Element aspectElement, P coltProject) throws COLTProjectPersistException;

    void load(Element aspectElement, P coltProject) throws COLTProjectPersistException;

}
