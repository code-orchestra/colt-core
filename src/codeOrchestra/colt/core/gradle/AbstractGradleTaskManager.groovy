package codeOrchestra.colt.core.gradle

import codeOrchestra.colt.core.model.Project
import codeOrchestra.util.ProjectHelper

/**
 * @author Alexander Eliseyev
 */
abstract class AbstractGradleTaskManager<P extends Project> implements GradleTaskManager<P> {

    @Override
    List<String> appendTasks(StringBuilder script) {
        List<String> tasksApplied = []

        GradleTask<P>[] lastTask = new GradleTask<P>[1]
        tasks.each { task ->
            if (task.isApplicable(ProjectHelper.currentProject as P)) {
                task.append(script, lastTask[0])
                tasksApplied.addAll(task.getTaskNames())
                lastTask[0] = task
            }
        }
        return tasksApplied
    }

    abstract List<GradleTask<P>> getTasks()

}
