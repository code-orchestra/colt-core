package codeOrchestra.colt.core.ui.testmode

import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.execution.OSProcessHandler
import codeOrchestra.colt.core.execution.ProcessHandler
import codeOrchestra.colt.core.execution.ProcessHandlerWrapper
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.util.PathUtils
import codeOrchestra.util.process.ProcessHandlerBuilder
import javafx.event.EventHandler
import javafx.scene.control.Button

/**
 * @author Dima Kruk
 */
abstract class TestSettingsForm extends SettingsScrollPane {
    private Project project

    private @Service LiveCodingManager liveCodingManager

    protected ArrayList<ProcessHandlerWrapper> wrappers = new ArrayList<>()

    protected int commitCount = 1
    private Button initButton

    TestSettingsForm() {
        initButton = new Button("Init")
        initButton.onAction = {
            init()
        } as EventHandler
        mainContainer.children.add(initButton)

        ColtProjectManager.instance.addProjectListener([
                onProjectLoaded: { Project project ->
                    initProject(project)
                },
                onProjectUnloaded: { Project project ->
                }
        ] as ProjectListener)
    }

    protected void initProject(Project value) {
        project = value
        if (new File(project.baseDir, ".git").exists()) {
            initButton.disable = true
        }
    }

    protected void init() {
        wrappers.add(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "init")
                        .build(project.baseDir),
                true))
    }

    protected void addDirectories(List<String> paths) {
        ProcessHandlerBuilder builder = new ProcessHandlerBuilder()
        builder.append("git", "add")
        paths.each {
            String folder = PathUtils.makeRelative(it, project).replace("\${project}" + File.separator, "")
            builder = builder.append(folder + "/*")
        }
        wrappers.add(new ProcessHandlerWrapper(
                builder.build(project.baseDir),
                true))

        wrappers.add(new ProcessHandlerWrapper(
                new OSProcessHandler(project.baseDir, "git", "commit", "-m", "Initial commit"),
                true))

        executeWrappers()
    }
    //git log --pretty=oneline

    protected void executeWrappers() {
        wrappers.each {
            ProcessHandler processHandler = it.getProcessHandler()
            processHandler.addProcessListener(new GitProcessListener())
            processHandler.startNotify()
            if (it.mustWaitForExecutionEnd()) {
                processHandler.waitFor();
            }
        }
        wrappers.clear()
        println "done"
    }

    protected void makeCommit() {
        wrappers.add(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "add", "-u")
                        .build(project.baseDir),
                true))
        wrappers.add(new ProcessHandlerWrapper(
                new OSProcessHandler(project.baseDir, "git", "commit", "-m", "commit " + commitCount),
                true))

        executeWrappers()

        commitCount++
    }
}
