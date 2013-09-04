package codeOrchestra.colt.core.storage

/**
 * @author Alexander Eliseyev
 */
class ProjectStorageManager {

    public static final String COLT_DIR_NAME = ".colt"

    File getOrCreateColtDir() {
        File coltDir = new File(System.getProperty("user.home"), COLT_DIR_NAME)
        if (!coltDir.exists()) {
            coltDir.mkdir()
        }
        coltDir
    }

    private List<ProjectStorageDescriptor> getStorageDescriptors() {
        null; // TODO: implement
    }

    File getProjectStorageDir() {
        null; // TODO: implement
    }

}
