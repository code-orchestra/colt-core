package codeOrchestra.colt.core.storage

import codeOrchestra.util.FileUtils
import codeOrchestra.util.ProjectHelper
import codeOrchestra.util.StringUtils
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder

/**
 * @author Alexander Eliseyev
 */
class ProjectStorageManager {

    public static final String COLT_DIR_NAME = ".colt"

    static File getOrCreateColtDir() {
        File coltDir = new File(System.getProperty("user.home"), COLT_DIR_NAME)
        if (!coltDir.exists()) {
            coltDir.mkdir()
        }
        return coltDir
    }

    private static File getStorageDescriptorsFile() {
        new File(getOrCreateColtDir(), "storage.xml")
    }

    static List<ProjectStorageDescriptor> getStorageDescriptors() {
        File descriptorsFile = getStorageDescriptorsFile()
        if (!descriptorsFile.exists()) {
            return new ArrayList<>()
        }

        List<ProjectStorageDescriptor> result = new ArrayList<>()
        GPathResult gPathResult = new XmlSlurper().parseText(FileUtils.read(descriptorsFile))
        gPathResult.'storage'.each { result << new ProjectStorageDescriptor((String) it.@path, (String) it.@subDir) }

        return result
    }

    static void saveStorageDescriptors(List<ProjectStorageDescriptor> descriptors) {
        File descriptorsFile = getStorageDescriptorsFile()

        StringWriter writer = new StringWriter()
        new MarkupBuilder(writer).xml() {
            descriptors.each {
                storage(path : it.projectPath, subDir : it.storageSubDir)
            }
        }
        String content = writer.toString()

        FileUtils.write(descriptorsFile, content)
    }

    static File getOrCreateProjectStorageDir() {
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

    private static File getOrCreateProjectStorageDirBySubDir(String subDirName) {
        File dir = new File(getOrCreateColtDir(), "storage" + File.separator + subDirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

}
