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

    ColtMenuBar() {
        ExpandoMetaClass menuExpando = new ExpandoMetaClass(Menu, false)
        menuExpando.setNewItems = {List<MenuItem> it ->
            items.addAll(it)
        }
        menuExpando.initialize()

        Menu.metaClass = menuExpando

        MenuItem newJs
        MenuItem newAs
        MenuItem save
        MenuItem serial

        menus.addAll(
                new Menu(text: "File", newItems: [
                        new Menu(text: "New Project", newItems: [
                                newAs = new MenuItem(
                                        text: "New AS Project",
                                        id: "new-as",
                                        onAction: { t ->
                                            ProjectDialogs.newAsProjectDialog(scene)
                                        } as EventHandler<ActionEvent>
                                ),
                                newJs = new MenuItem(
                                        text: "New JS Project",
                                        id: "new-js",
                                        onAction: { t ->
                                            ProjectDialogs.newJsProjectDialog(scene)
                                        } as EventHandler<ActionEvent>
                                )
                        ]),
                        new MenuItem(
                                text: "Open Project",
                                onAction: { t ->
                                    ProjectDialogs.openProjectDialog(scene)
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN)
                        ),
                        recentProjectsSubMenu = new Menu(text: "Open Recent", newItems: [
                                clearRecentProjects = new MenuItem(
                                        text: "Clear List",
                                        onAction: { t ->
                                            RecentProjects.clear(ColtProjectManager.instance.currentProject?.path)
                                            refreshRecentProjectsMenu()
                                        } as EventHandler<ActionEvent>
                                ),
                        ]),
                        save = new MenuItem(
                                text: "Save Project",
                                id: "save",
                                onAction: { t ->
                                    ProjectDialogs.saveProjectDialog()
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                                disable: true
                        ),
                        save = new MenuItem(
                                text: "Close Project",
                                onAction: { t ->
                                    ProjectDialogs.closeProjectDialog()
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                                disable: true
                        ),
                        new SeparatorMenuItem(),
                        new MenuItem(
                                text: "Exit",
                                onAction: { t ->
                                    System.exit(0)
                                } as EventHandler<ActionEvent>
                        ),
                ]),
                new Menu(text: "Help", newItems: [
                        new MenuItem(
                                text: "Open Demo Projects Directory",
                                onAction: { t ->
                                    ProjectDialogs.openDemoProjectDialog(scene)
                                } as EventHandler<ActionEvent>
                        ),
                        new SeparatorMenuItem(),
                        serial = new MenuItem(
                                text: "Enter Serial Number",
                                id: "serial",
                                disable: ExpirationHelper.expirationStrategy.trialOnly || !CodeOrchestraLicenseManager.noSerialNumberPresent(),
                                onAction: { t ->
                                    ExpirationHelper.getExpirationStrategy().showSerialNumberDialog()
                                } as EventHandler<ActionEvent>
                        )
                ])
        )


        ColtProjectManager.instance.addProjectListener([
                onProjectLoaded: { Project project ->
                    save.disable = false
                },
                onProjectUnloaded: { Project project ->
                    save.disable = true
                }
        ] as ProjectListener)

        refreshRecentProjectsMenu()

        ColtProjectManager.instance.addProjectListener([
                onProjectLoaded: { Project project ->
                    refreshRecentProjectsMenu()
                },
                onProjectUnloaded: { Project project ->
                }
        ] as ProjectListener)

        popupMenuItems.addAll(newAs, newJs, save)

        CodeOrchestraLicenseManager.addListener({
            serial.disable = false
        } as LicenseListener)

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
}
