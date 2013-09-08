package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.ServiceProvider
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.facade.ColtFacade
import codeOrchestra.colt.core.license.CodeOrchestraLicenseManager
import codeOrchestra.colt.core.license.ExpirationHelper
import codeOrchestra.colt.core.license.LicenseListener
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.ui.dialog.ProjectDialogs
import codeOrchestra.colt.core.ui.groovy.GroovyDynamicMethods
import codeOrchestra.util.ApplicationUtil
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

import static codeOrchestra.colt.core.RecentProjects.getRecentProjectsPaths

class ColtMenuBar extends MenuBar {

    private Menu recentProjectsSubMenu
    private MenuItem clearRecentProjects

    ArrayList<MenuItem> popupMenuItems = new ArrayList<>()

    ColtMenuBar() {
        GroovyDynamicMethods.init()

        MenuItem newJs
        MenuItem newAs
        MenuItem save
        MenuItem saveAs
        MenuItem serial

        menus.addAll(
                new Menu(text: "File", newItems: [
                        new MenuItem(
                                text: "New Window",
                                onAction: { t ->
                                    RecentProjects.setMustOpenRecentProject(false)
                                    ApplicationUtil.startAnotherColtInstance()
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN)
                        ),
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
                        new SeparatorMenuItem(),
                        new MenuItem(
                                text: "Open Project",
                                onAction: { t ->
                                    ProjectDialogs.openProjectDialog(scene, false)
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
                        new SeparatorMenuItem(),
                        save = new MenuItem(
                                text: "Save Project",
                                id: "save",
                                onAction: { t ->
                                    ProjectDialogs.saveProjectDialog()
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                                disable: true
                        ),
                        saveAs = new MenuItem(
                                text: "Save As...",
                                onAction: { t ->
                                    ProjectDialogs.saveAsProjectDialog(scene)
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
                        ),
                        new MenuItem(
                                text: "Close Project",
                                onAction: { t ->
                                    RecentProjects.mustOpenRecentProject = true
                                    ApplicationUtil.restartColt()
                                } as EventHandler<ActionEvent>,
                                accelerator: new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                        ),
                        new SeparatorMenuItem(),
                        new MenuItem(
                                text: "Exit",
                                onAction: { t ->
                                    ApplicationUtil.exitColt()
                                } as EventHandler<ActionEvent>
                        ),
                ]),
                new Menu(text: "Run", newItems: [
                        new Menu(text: "Livecoding Session", newItems: [
                                new MenuItem(
                                        text: "Start",
                                        onAction: { t ->
                                            coltFacade.runSession()
                                        } as EventHandler<ActionEvent>
                                ),
                                new MenuItem(
                                        text: "Stop",
                                        onAction: { t ->
                                            coltFacade.stopSession()
                                        } as EventHandler<ActionEvent>
                                ),
                                new MenuItem(
                                        text: "Pause",
                                        onAction: { t ->
                                            coltFacade.pauseSession()
                                        } as EventHandler<ActionEvent>
                                ),
                                new SeparatorMenuItem(),
                                new MenuItem(
                                        text: "Restart",
                                        onAction: { t ->
                                            coltFacade.restartSession()
                                        } as EventHandler<ActionEvent>
                                )
                        ]),
                        new SeparatorMenuItem(),
                        new MenuItem(
                                text: "Open New Connection",
                                onAction: { t ->
                                    coltFacade.openNewConnection()
                                } as EventHandler<ActionEvent>
                        ),
                        new MenuItem(
                                text: "Close All Connections",
                                onAction: { t ->
                                    coltFacade.closeAllConnections()
                                } as EventHandler<ActionEvent>
                        ),
                        new SeparatorMenuItem(),
                        new MenuItem(
                                text: "Production Build",
                                onAction: { t ->
                                    coltFacade.runProductionBuild()
                                } as EventHandler<ActionEvent>
                        )
                ]),
                new Menu(text: "Help", newItems: [
                        new MenuItem(
                                text: "Open Demo Projects Directory",
                                onAction: { t ->
                                    ProjectDialogs.openDemoProjectDialog(scene)
                                } as EventHandler<ActionEvent>
                        ),
                        new MenuItem(
                                text: "Open Welcome Screen",
                                onAction: { t ->
                                    ProjectDialogs.openWelcomeScreen(scene)
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
                    saveAs.disable = false
                },
                onProjectUnloaded: { Project project ->
                    save.disable = true
                    saveAs.disable = true
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

        popupMenuItems.addAll(newAs, newJs, save, saveAs)

        CodeOrchestraLicenseManager.addListener({
            serial.disable = false
        } as LicenseListener)

        setUseSystemMenuBar(true)
    }

    private static ColtFacade getColtFacade() {
        ServiceProvider.get(ColtFacade.class)
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
                    RecentProjects.addRecentProject(it)
                    RecentProjects.mustOpenRecentProject = true
                    ApplicationUtil.restartColt()
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
