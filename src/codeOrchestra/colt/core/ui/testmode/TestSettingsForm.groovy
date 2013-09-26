package codeOrchestra.colt.core.ui.testmode

import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.session.LiveCodingSession
import codeOrchestra.colt.core.session.listener.LiveCodingAdapter
import codeOrchestra.colt.core.ui.components.inputForms.RadioButtonInput
import codeOrchestra.colt.core.ui.components.inputForms.RadioButtonWithTextInput
import codeOrchestra.colt.core.ui.components.inputForms.group.FormGroup
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.groovyfx.FXBindable
import codeOrchestra.util.PathUtils
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ListView
import javafx.scene.control.ToggleGroup
import javafx.util.StringConverter

/**
 * @author Dima Kruk
 */
abstract class TestSettingsForm extends SettingsScrollPane {

    protected TestModeState state

    private Project project

    protected GitHelper gitHelper

    protected @Service LiveCodingManager liveCodingManager

    protected Button initButton
    protected Button startButton
    protected Button recordButton
    protected ChoiceBox<String> choiceBox
    protected ListView<String> listView

    protected ArrayList<String> commits = new ArrayList<>()
    protected javafx.collections.ObservableList<String> tests

    @FXBindable Integer interval = 1000
    protected ToggleGroup testToggleGroup
    protected RadioButtonInput manually


    TestSettingsForm() {
        if (System.getProperty("colt.runType") != "test") {
            return
        }

        state = TestModeState.NONE

        initButton = new Button("Init")
        initButton.onAction = {
            init()
        } as EventHandler
        mainContainer.children.add(initButton)

        recordButton = new Button("Record")
        recordButton.disable = true
        recordButton.onAction = {
            startRecord()
        } as EventHandler
        mainContainer.children.add(recordButton)

        FormGroup startGroup = new FormGroup(title: "Start test")
        testToggleGroup = new ToggleGroup()
        RadioButtonWithTextInput byTime
        startGroup.children.addAll(
                manually = new RadioButtonInput(title: "manually", toggleGroup: testToggleGroup, selected: true),
                byTime = new RadioButtonWithTextInput(title: "by time", numeric: true, toggleGroup: testToggleGroup)
        )
        byTime.text().bindBidirectional(interval(), new StringConverter<Number>() {
            @Override
            String toString(Number t) {
                return t.toString()
            }

            @Override
            Number fromString(String s) {
                return s as Integer
            }
        })

        startButton = new Button("Start Test")
        startButton.disable = true
        startButton.onAction = {
            startTest()
        } as EventHandler
        startGroup.children.add(startButton)
        mainContainer.children.add(startGroup)

        choiceBox = new ChoiceBox()
        tests = choiceBox.items
        choiceBox.valueProperty().addListener({ ObservableValue observableValue, String t, String newValue ->
            if (newValue) {
                startButton.disable = false
                gitHelper.checkoutBranch(newValue)
                commits = gitHelper.commits
                listView.items.clear()
                listView.items.addAll(commits)
            }
        } as ChangeListener)
        mainContainer.children.add(choiceBox)

        listView = new ListView<>()
        listView.selectionModel.selectedItemProperty().addListener({ ObservableValue<? extends String> observableValue, String t, String newValue ->
            if (newValue) {
                if(state == TestModeState.TEST) {
                    gitHelper.checkoutCommit(newValue.split(":").first())
                }
            }
        } as ChangeListener)
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
            recordButton.disable = false
            choiceBox.items.addAll(gitHelper.getBranches())
            if (choiceBox.items.size() > 0) {
                choiceBox.value = choiceBox.items.first()
            }
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
                    Platform.runLater({
                        choiceBox.value = choiceBox.items.last()
                    } as Runnable)
                }
                if (state == TestModeState.TEST) {
                    startButton.disable = false
                }
                state = TestModeState.NONE
                recordButton.disable = false
                choiceBox.disable = false
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
        recordButton.disable = false
    }

    protected void startRecord() {
        state = TestModeState.RECORD
        listView.items.clear()
        gitHelper.resetCommitCount()
        String testName = "test" + (tests.size() + 1)
        tests.add(testName)
        gitHelper.createBranch(testName, "master")

        startButton.disable = true
        recordButton.disable = true
        choiceBox.disable = true
    }

    protected void startTest() {
        state = TestModeState.TEST
        gitHelper.checkoutCommit(commits.first().split(":").first())

        startButton.disable = true
        recordButton.disable = true
        choiceBox.disable = true
    }

    protected void runTest() {
        if (testToggleGroup.selectedToggle != manually.radioButton) {
            new Thread() {
                @Override
                void run() {
                    commits.each {
                        gitHelper.checkoutCommit(it.split(":").first())
                        sleep(getInterval())
                    }
                }
            }.start()
        }
    }

    protected void addDirectories(List<String> paths) {
        gitHelper.addDirectories(paths.collect{
            PathUtils.makeRelative(it, project).replace("\${project}" + File.separator, "")
        })
    }

    protected void addFiles(List<String> paths) {
        gitHelper.addFiles(paths.collect{
            PathUtils.makeRelative(it, project).replace("\${project}" + File.separator, "")
        })
    }
}
