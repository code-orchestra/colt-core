package codeOrchestra.colt.core.gradle

import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.ui.components.fileset.FilesetInput
import codeOrchestra.util.StringUtils

/**
 * @author Alexander Eliseyev
 */
abstract class AbstractGradleScriptGenerator<P extends Project> {

    protected P project

    protected List<String> tasksApplied

    AbstractGradleScriptGenerator(Project project) {
        this.project = project
    }

    abstract AbstractGradleTaskManager<P> getTaskManager()

    abstract File generate();

    abstract List<String> getExcludedFiles()

    abstract String getOutputPath()

    List<String> getSourceFilesListExcludeWise(String fileset) {
        List<String> files = FilesetInput.getFilesFromString(fileset).collect{it.path}

        // Exclude output dir
        Iterator<String> iterator = files.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().contains(getOutputPath())) {
                iterator.remove()
            }
        }

        // Project exclude
        files.removeAll(getExcludedFiles())

        return files
    }

    static String getSourceFilesList(List<String> files) {
        StringBuilder sb = new StringBuilder()
        Iterator<String> sourceIterator = files.iterator()
        while (sourceIterator.hasNext()) {
            sb.append("\"")
            sb.append(sourceIterator.next())
            sb.append("\"")

            if (sourceIterator.hasNext()) {
                sb.append(", ")
            }
        }

        return sb.toString()
    }

    String getTasksNames() {
        return StringUtils.join(tasksApplied, " ")
    }

}
