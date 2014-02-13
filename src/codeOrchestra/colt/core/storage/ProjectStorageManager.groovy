package codeOrchestra.colt.core.storage

import codeOrchestra.util.FileUtils
import codeOrchestra.util.ProjectHelper
import codeOrchestra.util.StringUtils
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

    private static List<ProjectStorageDescriptor> _storageDescriptors;

    static List<ProjectStorageDescriptor> getStorageDescriptors() {
        if(_storageDescriptors)return _storageDescriptors;

        File descriptorsFile = getStorageDescriptorsFile()
        if (!descriptorsFile.exists()) {
            return new ArrayList<>()
        }

        List<ProjectStorageDescriptor> result = new ArrayList<>()
        new XmlSlurper().parseText(descriptorsFile.text).'storage'.each {
            result << new ProjectStorageDescriptor("" + it.@path, "" + it.@subDir)
        }

        _storageDescriptors = result;

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
        for (ProjectStorageDescriptor it : descriptors) {
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
