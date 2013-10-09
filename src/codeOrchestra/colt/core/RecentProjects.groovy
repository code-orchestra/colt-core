package codeOrchestra.colt.core

import codeOrchestra.colt.core.storage.ProjectStorageManager
import codeOrchestra.util.FileUtils
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder

/**
 * @author Alexander Eliseyev
 */
class RecentProjects {

    static void addRecentProject(String path) {
        List<String> paths = getRecentProjectsPaths()

        if (paths.contains(path)) {
            paths.removeAll(Collections.singletonList(path))
        }

        paths.add(0, path)
        save(paths, true)
    }

    private static File getWorkingSetFile() {
        new File(ProjectStorageManager.getOrCreateColtDir(), "workingset.xml")
    }

    static void setMustOpenRecentProject(boolean value) {
        save(getRecentProjectsPaths(), value)
    }

    static void clear(String onlyPathToLeave) {
        save(onlyPathToLeave != null ? Collections.singletonList(onlyPathToLeave) : Collections.emptyList(), mustOpenRecentProject());
    }

    private static void save(List<String> paths, boolean openRecentValue) {
        StringWriter writer = new StringWriter()
        new MarkupBuilder(writer).workingset(openRecent:openRecentValue) {
            paths.each {
                project(path : it)
            }
        }
        String content = writer.toString()

        FileUtils.write(workingSetFile, content)
    }

    static boolean mustOpenRecentProject() {
        if (!workingSetFile.exists()) {
            return false
        }

        GPathResult xml = new XmlSlurper().parseText(FileUtils.read(workingSetFile))
        return Boolean.parseBoolean((String) xml.@openRecent)
    }

    static List<String> getRecentProjectsPaths() {
        List<String> result = new ArrayList<String>()

        File workingSetFile = getWorkingSetFile()
        if (!workingSetFile.exists()) {
            return result
        }

        GPathResult xml = new XmlSlurper().parseText(FileUtils.read(workingSetFile))
        xml.'project'.each { result << (String) it.@path }

        return result
    }

}
