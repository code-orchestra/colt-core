package codeOrchestra.colt.core.gradle

import codeOrchestra.colt.core.model.Project
import codeOrchestra.util.ProjectHelper

/**
 * @author Alexander Eliseyev
 */
abstract class AbstractGradleTaskManager<P extends Project> implements GradleTaskManager<P> {

    private List<GradleTask<P>> tasks;

    AbstractGradleTaskManager(List<GradleTask<P>> tasks) {
        this.tasks = tasks
    }

    @Override
    List<String> appendTasks(StringBuilder script) {
        List<String> tasksApplied = []
        tasks.each { task ->
            if (task.isApplicable(ProjectHelper.currentProject as P)) {
                task.append(script)
                tasksApplied << task.name
            }
        }
        return tasksApplied
    }

    @Override
    void dispose() {
        tasks.clear();
    }
}
