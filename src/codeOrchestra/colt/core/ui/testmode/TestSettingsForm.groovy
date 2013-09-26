package codeOrchestra.colt.core.ui.testmode

import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.session.LiveCodingSession
import codeOrchestra.colt.core.session.listener.LiveCodingAdapter
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.util.PathUtils
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ListView
import org.controlsfx.control.ButtonBar

/**
 * @author Dima Kruk
 */
abstract class TestSettingsForm extends SettingsScrollPane {

    protected TestModeState state

    private Project project

    protected GitHelper gitHelper

    protected @Service LiveCodingManager liveCodingManager

    private Button initButton
    private Button startButton
    private Button recordButton
    protected ChoiceBox<String> choiceBox
    protected ListView<String> listView

    protected ArrayList<String> commits = new ArrayList<>()
    protected javafx.collections.ObservableList<String> tests
    private ButtonBar buttonBar

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

        buttonBar = new ButtonBar()
        buttonBar.buttons.addAll(recordButton, startButton)
        mainContainer.children.add(buttonBar)

        choiceBox = new ChoiceBox()
        tests = choiceBox.items
        choiceBox.valueProperty().addListener({ ObservableValue observableValue, String t, String newValue ->
            if (newValue) {
                gitHelper.checkoutBranch(newValue)
                commits = gitHelper.commits
                listView.items.clear()
                listView.items.addAll(commits)
            }
        } as ChangeListener)
        mainContainer.children.add(choiceBox)

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

    protected void initProject(Project value) {
        project = value

        gitHelper = new GitHelper(project.baseDir)

        if (new File(project.baseDir, ".git").exists()) {
            initButton.disable = true
            choiceBox.items.addAll(gitHelper.getBranches())
            choiceBox.value = choiceBox.items.first()
        } else {
            buttonBar.disable = true
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
                if (state == TestModeState.RECORD) {
                    choiceBox.value = choiceBox.items.last()
                }
                state = TestModeState.NONE
            }

            @Override
            void onCodeUpdate() {
                if (state == TestModeState.RECORD) {
                    gitHelper.makeCommit()
                }
            }
        })
    }

    protected void init() {
        gitHelper.init()
        initButton.disable = true
        buttonBar.disable = false
    }

    protected void startRecord() {
        state = TestModeState.RECORD
        listView.items.clear()
        gitHelper.resetCommitCount()
        String testName = "test" + (tests.size() + 1)
        tests.add(testName)
        gitHelper.createBranch(testName, "master")
    }

    protected void startTest() {
        state = TestModeState.TEST
        gitHelper.checkoutCommit(commits.first().split(":").first())
    }

    protected void runTest() {
        new Thread() {
            @Override
            void run() {
                commits.each {
                    gitHelper.checkoutCommit(it.split(":").first())
                    sleep(2000)
                }
            }
        }.start()
    }

    protected void addDirectories(List<String> paths) {
        gitHelper.addDirectories(paths.collect{
            PathUtils.makeRelative(it, project).replace("\${project}" + File.separator, "")
        })
    }
}
