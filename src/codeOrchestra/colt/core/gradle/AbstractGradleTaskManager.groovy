package codeOrchestra.colt.core.gradle

import codeOrchestra.colt.core.model.Project
import codeOrchestra.util.ProjectHelper

/**
 * @author Alexander Eliseyev
 */
abstract class AbstractGradleTaskManager<P extends Project> implements GradleTaskManager<P> {

    @Override
    List<String> appendTasks(StringBuilder script, String type) {
        List<String> tasksApplied = []

        GradleTask<P>[] lastTask = new GradleTask<P>[1]
        tasks.each { task ->
            if (type.equals(task.getType()) && task.isApplicable(ProjectHelper.currentProject as P)) {
                task.append(script, lastTask[0])
                tasksApplied.addAll(task.getTaskNames())
                lastTask[0] = task
            }
        }
        return tasksApplied
    }

    @Override
    List<GradleTask<P>> getTasks(String type) {
        getTasks().findAll { it.getType().equals(type) }
    }

    abstract List<GradleTask<P>> getTasks()

}
