package codeOrchestra.colt.core.ui.testmode

import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.execution.OSProcessHandler
import codeOrchestra.colt.core.execution.ProcessHandler
import codeOrchestra.colt.core.execution.ProcessHandlerWrapper
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.session.LiveCodingSession
import codeOrchestra.colt.core.session.listener.LiveCodingAdapter
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.util.PathUtils
import codeOrchestra.util.ThreadUtils
import codeOrchestra.util.process.ProcessHandlerBuilder
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ListView
import org.controlsfx.control.ButtonBar

/**
 * @author Dima Kruk
 */
abstract class TestSettingsForm extends SettingsScrollPane {

    protected TestModeState state

    private Project project

    protected @Service LiveCodingManager liveCodingManager

    protected ArrayList<ProcessHandlerWrapper> wrappers = new ArrayList<>()

    protected int commitCount = 1
    private Button initButton
    private Button startButton
    private Button recordButton
    private ListView<String> listView

    protected ArrayList<String> commits = new ArrayList<>()

    TestSettingsForm() {
        state = TestModeState.NONE

        initButton = new Button("Init")
        initButton.onAction = {
            init()
        } as EventHandler
        mainContainer.children.add(initButton)

        recordButton = new Button("Record")
        recordButton.onAction = {
            startRecord()
        } as EventHandler

        startButton = new Button("Start Test")
        startButton.onAction = {
            startTest()
        } as EventHandler

        ButtonBar buttonBar = new ButtonBar()
        buttonBar.buttons.addAll(recordButton, startButton)
        mainContainer.children.add(buttonBar)

        listView = new ListView<>()
        mainContainer.children.add(listView)

        ColtProjectManager.instance.addProjectListener([
                onProjectLoaded: { Project project ->
                    initProject(project)
                },
                onProjectUnloaded: { Project project ->
                }
        ] as ProjectListener)
    }

    protected void startRecord() {
        state = TestModeState.RECORD
    }

    protected void startTest() {
        state = TestModeState.TEST
        wrappers.add(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "checkout", commits.first().split(":").first())
                        .build(project.baseDir),
                true))
        executeWrappers()
    }

    protected void runTest() {
        new Thread() {
            @Override
            void run() {
                commits.each {
                    wrappers.add(new ProcessHandlerWrapper(
                            new ProcessHandlerBuilder()
                                    .append("git", "checkout", it.split(":").first())
                                    .build(project.baseDir),
                            true))
                    executeWrappers()
                    sleep(2000)
                }
            }
        }.start()
    }

    protected void initProject(Project value) {
        project = value

        if (new File(project.baseDir, ".git").exists()) {
            initButton.disable = true
            Process process = new ProcessBuilder().command("git", "log", '--pretty=format:"%h:%s"').directory(project.baseDir).start()

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.inputStream))
            String line
            while ((line = reader.readLine()) != null) {
                commits.add(line)
            }
            reader.close()
            commits = commits.reverse()
            println "commits = $commits"
            listView.items.addAll(commits)
        }

        liveCodingManager.addListener(new LiveCodingAdapter(){
            @Override
            void onSessionStart(LiveCodingSession session) {
                if (state == TestModeState.TEST) {
                    runTest()
                }
            }

            @Override
            void onSessionEnd(LiveCodingSession session) {
                state = TestModeState.NONE
            }

            @Override
            void onCodeUpdate() {
                if (state == TestModeState.RECORD) {
                    makeCommit()
                }
            }
        })
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
    //git log --pretty=format:"%h:%s"

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
