package codeOrchestra.colt.core.storage

import codeOrchestra.util.FileUtils
import codeOrchestra.util.ProjectHelper
import codeOrchestra.util.StringUtils
import groovy.util.slurpersupport.GPathResult

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
        return coltDir
    }

    private File getStorageDescriptorsFile() {
        new File(getOrCreateColtDir(), "storage.xml")
    }

    List<ProjectStorageDescriptor> getStorageDescriptors() {
        File descriptorsFile = getStorageDescriptorsFile()
        if (!descriptorsFile.exists()) {
            return Collections.emptyList()
        }

        List<ProjectStorageDescriptor> result = new ArrayList<>()
        GPathResult gPathResult = new XmlSlurper().parseText(FileUtils.read(descriptorsFile))
        gPathResult.'storage'.each { result << new ProjectStorageDescriptor((String) it.@path, (String) it.@subDir) }

        return result
    }

    void saveStorageDescriptors(List<ProjectStorageDescriptor> descriptors) {
        // TODO: implement
    }

    File getOrCreateProjectStorageDir() {
        String currentPath = ProjectHelper.currentProject.path

        List<ProjectStorageDescriptor> descriptors = storageDescriptors

        descriptors.each {
            if (it.projectPath.equals(currentPath)) {
                return getOrCreateProjectStorageDirBySubDir(it.storageSubDir)
            }
        }

        ProjectStorageDescriptor projectStorageDescriptor = new ProjectStorageDescriptor(currentPath, StringUtils.generateId(8))
        descriptors.add(0, projectStorageDescriptor)
        saveStorageDescriptors(descriptors)

        return getOrCreateProjectStorageDirBySubDir(projectStorageDescriptor.storageSubDir)
    }

    private File getOrCreateProjectStorageDirBySubDir(String subDirName) {
        File dir = new File(getOrCreateColtDir(), "storage" + File.separator + subDirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    public static void main(String[] args) {
        new ProjectStorageManager().getStorageDescriptors().each { println(it.projectPath); println(it.storageSubDir) }
    }

}
