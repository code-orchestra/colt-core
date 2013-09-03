package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.license.CodeOrchestraLicenseManager
import codeOrchestra.colt.core.license.ExpirationHelper
import codeOrchestra.colt.core.license.LicenseListener
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.tasks.ColtTaskWithProgress
import codeOrchestra.colt.core.tasks.TasksManager
import codeOrchestra.colt.core.ui.components.IProgressIndicator
import codeOrchestra.colt.core.ui.dialog.ProjectDialogs
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.FileChooser

import static codeOrchestra.colt.core.RecentProjects.getRecentProjectsPaths

class ColtMenuBar extends MenuBar {

    private Menu recentProjectsSubMenu
    private MenuItem clearRecentProjects

    ArrayList<MenuItem> popupMenuItems = new ArrayList<>()

//    private Map<String, EventHandler<ActionEvent>> actions = [:]

    ColtMenuBar() {
        Menu fileMenu = new Menu("File")

        MenuItem openProjectMenuItem = new MenuItem("Open Project")
        openProjectMenuItem.accelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN)
        openProjectMenuItem.onAction = { t ->
            ProjectDialogs.openProjectDialog(scene)
        } as EventHandler<ActionEvent>

        MenuItem saveProjectMenuItem = new MenuItem("Save Project")
        saveProjectMenuItem.accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN)

        saveProjectMenuItem.onAction = { t ->
            ProjectDialogs.saveProjectDialog()
        } as EventHandler<ActionEvent>

        saveProjectMenuItem.disable = true

        ColtProjectManager.instance.addProjectListener([
                onProjectLoaded: { Project project ->
                    saveProjectMenuItem.disable = false
                },
                onProjectUnloaded: { Project project ->
                    saveProjectMenuItem.disable = true
                }
        ] as ProjectListener)

        MenuItem newAsProjectMenuItem = new MenuItem("New Project")

        newAsProjectMenuItem.onAction = { t ->
            ProjectDialogs.newAsProjectDialog(scene)
        } as EventHandler<ActionEvent>

        MenuItem newJSProjectMenuItem = new MenuItem("New JS Project")

        newJSProjectMenuItem.onAction = { t ->
            ProjectDialogs.newJsProjectDialog(scene)
        } as EventHandler<ActionEvent>

        MenuItem exitMenuItem = new MenuItem("Exit")

        exitMenuItem.onAction = { t ->
            System.exit(0)
        } as EventHandler<ActionEvent>

        recentProjectsSubMenu = new Menu("Open Recent")
        clearRecentProjects = new MenuItem("Clear List")

        clearRecentProjects.onAction = { t ->
            RecentProjects.clear(ColtProjectManager.instance.currentProject?.path)
            refreshRecentProjectsMenu()
        } as EventHandler<ActionEvent>

        refreshRecentProjectsMenu()

        ColtProjectManager.instance.addProjectListener([
                onProjectLoaded: { Project project ->
                    refreshRecentProjectsMenu()
                },
                onProjectUnloaded: { Project project ->
                }
        ] as ProjectListener)

        popupMenuItems.addAll(newAsProjectMenuItem, openProjectMenuItem, saveProjectMenuItem)
        fileMenu.items.addAll(newAsProjectMenuItem, newJSProjectMenuItem, new SeparatorMenuItem(), openProjectMenuItem, recentProjectsSubMenu, saveProjectMenuItem, new SeparatorMenuItem(), exitMenuItem)

        Menu helpMenu = new Menu("Help")
        final MenuItem enterSerialItem = new MenuItem("Enter Serial Number")

        enterSerialItem.onAction = { t ->
            ExpirationHelper.getExpirationStrategy().showSerialNumberDialog()
        } as EventHandler<ActionEvent>

        enterSerialItem.disable = ExpirationHelper.expirationStrategy.trialOnly || !CodeOrchestraLicenseManager.noSerialNumberPresent()

        CodeOrchestraLicenseManager.addListener({
            enterSerialItem.disable = false
        } as LicenseListener)

        helpMenu.items.add(enterSerialItem)
        menus.add(fileMenu)
        menus.add(helpMenu)
        setUseSystemMenuBar(true)
    }

    private void refreshRecentProjectsMenu() {
        recentProjectsSubMenu.items.clear()

        List<String> recentProjectsPaths = recentProjectsPaths
        String curProjectPath = ColtProjectManager.instance.currentProject?.path
        recentProjectsPaths.findAll {
            final File projectFile = new File(it)
            it != curProjectPath && projectFile.exists() && !projectFile.isDirectory()
        }.each {
            MenuItem openRecentProjectItem = new MenuItem(it)
            openRecentProjectItem.onAction = { actionEvent ->
                try {
                    ColtProjectManager.instance.load(it)
                } catch (ColtException e) {
                    ErrorHandler.handle(e, "Can't load a project " + it)
                }
            } as EventHandler<ActionEvent>
            recentProjectsSubMenu.items.add(openRecentProjectItem)
        }

        clearRecentProjects.disable = recentProjectsSubMenu.items.size() == 0
        recentProjectsSubMenu.items.add(new SeparatorMenuItem())
        recentProjectsSubMenu.items.add(clearRecentProjects)
    }

//    private EventHandler<ActionEvent> addAction(String name, EventHandler<ActionEvent> action){
//        action[name] = action
//        return action;
//    }
//
//    void executeAction(String name){
//        actions[name].handle(null)
//    }


}
