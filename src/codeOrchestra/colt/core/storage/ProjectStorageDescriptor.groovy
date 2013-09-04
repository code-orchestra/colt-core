package codeOrchestra.colt.core.storage

/**
 * @author Alexander Eliseyev
 */
class ProjectStorageDescriptor {

    String projectPath;
    String storageSubDir;

    ProjectStorageDescriptor(String projectPath, String storageSubDir) {
        this.projectPath = projectPath
        this.storageSubDir = storageSubDir
    }

}
