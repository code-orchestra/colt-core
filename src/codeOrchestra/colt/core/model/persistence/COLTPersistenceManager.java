package codeOrchestra.colt.core.model.persistence;

import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public class COLTPersistenceManager {

    private static COLTPersistenceManager instance;

    public static synchronized COLTPersistenceManager getInstance() {
        if (instance == null) {
            instance = new COLTPersistenceManager();
        }
        return instance;
    }

    public void persist(COLTProject coltProject) throws COLTProjectPersistException {
        COLTProjectPersistence mostRecentPersistence = getMostRecentPersistence();
        mostRecentPersistence.persist(coltProject);
    }

    public COLTProject load(String path) throws COLTProjectPersistException {
        File projectFile = new File(path);
        if (!projectFile.exists() || projectFile.isDirectory()) {
            throw new COLTProjectPersistException("Can't a COLT project from " + path + " as path ");
        }

        Document projectDocument;
        try {
            projectDocument = XMLUtils.fileToDOM(projectFile);
        } catch (Throwable t) {
            throw new COLTProjectPersistException("Can't a COLT project from " + path, t);
        }

        Element projectElement = projectDocument.getDocumentElement();
        int desiredPersistenceVersion = Integer.valueOf(projectElement.getAttribute("persistenceVersion"));

        COLTProjectPersistence persistenceByVersion = getPersistenceByVersion(desiredPersistenceVersion);
        if (persistenceByVersion == null) {
            throw new COLTProjectPersistException("Can't load a COLT project from " + path + " due to unknown persistence version");
        }

        return persistenceByVersion.load(projectElement, path);
    }

    private COLTProjectPersistence getPersistenceByVersion(int version) {
        for (COLTProjectPersistence coltProjectPersistence : LiveCodingHandlerManager.getInstance().getCurrentHandler().getAvailablePersistenceHandlers()) {
            if (version == coltProjectPersistence.getVersion()) {
                return coltProjectPersistence;
            }
        }
        return null;
    }

    private COLTProjectPersistence getMostRecentPersistence() {
        COLTProjectPersistence result = null;
        for (COLTProjectPersistence coltProjectPersistence : LiveCodingHandlerManager.getInstance().getCurrentHandler().getAvailablePersistenceHandlers()) {
            if (result == null || coltProjectPersistence.getVersion() > result.getVersion()) {
                result = coltProjectPersistence;
            }
        }
        return result;
    }

}
