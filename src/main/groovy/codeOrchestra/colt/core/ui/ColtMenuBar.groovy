package codeOrchestra.colt.core.ui
import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.ServiceProvider
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.facade.ColtFacade
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.model.listener.ProjectListener
import codeOrchestra.colt.core.net.ProxyDialog
import codeOrchestra.colt.core.ui.dialog.ProjectDialogs
import codeOrchestra.colt.core.ui.groovy.GroovyDynamicMethods
import codeOrchestra.util.ApplicationUtil
import codeOrchestra.util.SystemInfo
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

        MenuItem newAs
        MenuItem save
        MenuItem saveAs

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
                        newAs = new MenuItem(
                                text: "New Project",
                                id: "new-as",
                                onAction: { t ->
                                    ProjectDialogs.newAsProjectDialog(scene, false)
                                } as EventHandler<ActionEvent>),
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
                                    ProjectDialogs.closeProjectDialog()
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
                                new SeparatorMenuItem(),
                                new MenuItem(
                                        text: "Pause",
                                        onAction: { t ->
                                            coltFacade.pauseSession()
                                        } as EventHandler<ActionEvent>
                                ),
                                new MenuItem(
                                        text: "Resume",
                                        onAction: { t ->
                                            coltFacade.resumeSession()
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
                        new MenuItem(
                                text: "Check for updates",
                                onAction: {
                                   ColtApplication.get().checkForUpdate(true)
                                } as EventHandler
                        ),
                        new MenuItem(
                                text: "Proxy settings",
                                onAction: {
                                    new ProxyDialog(ColtApplication.get().primaryStage).show()
                                } as EventHandler
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

        if (SystemInfo.isMac) {
            popupMenuItems.addAll(newAs, save, saveAs)
        } else {

            popupMenuItems.addAll(
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
                                    new SeparatorMenuItem(),
                                    new MenuItem(
                                            text: "Pause",
                                            onAction: { t ->
                                                coltFacade.pauseSession()
                                            } as EventHandler<ActionEvent>
                                    ),
                                    new MenuItem(
                                            text: "Resume",
                                            onAction: { t ->
                                                coltFacade.resumeSession()
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
                            new MenuItem(
                                    text: "Check for updates",
                                    onAction: {
                                        ColtApplication.get().checkForUpdate(true)
                                    } as EventHandler
                            ),
                            new MenuItem(
                                    text: "Proxy settings",
                                    onAction: {
                                        new ProxyDialog(ColtApplication.get().primaryStage).show()
                                    } as EventHandler
                            )
                    ]),

                    new SeparatorMenuItem(),
                    // root actions

                    new MenuItem(
                            text: "New Window",
                            onAction: { t ->
                                RecentProjects.setMustOpenRecentProject(false)
                                ApplicationUtil.startAnotherColtInstance()
                            } as EventHandler<ActionEvent>,
                            accelerator: new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN)
                    ),
                    new MenuItem(
                            text: "New Project",
                            id: "new-as",
                            onAction: { t ->
                               ProjectDialogs.newAsProjectDialog(scene, false)
                            } as EventHandler<ActionEvent>
                    ),
                    new SeparatorMenuItem(),
                    new MenuItem(
                            text: "Open Project",
                            onAction: { t ->
                                ProjectDialogs.openProjectDialog(scene, false)
                            } as EventHandler<ActionEvent>,
                            accelerator: new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN)
                    ),
                    recentProjectsSubMenu,
                    new SeparatorMenuItem(),
                    new MenuItem(
                            text: "Save Project",
                            id: "save",
                            onAction: { t ->
                                ProjectDialogs.saveProjectDialog()
                            } as EventHandler<ActionEvent>,
                            accelerator: new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                            disable: true
                    ),
                    new MenuItem(
                            text: "Save As...",
                            onAction: { t ->
                                ProjectDialogs.saveAsProjectDialog(scene)
                            } as EventHandler<ActionEvent>,
                            accelerator: new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
                    ),
                    new MenuItem(
                            text: "Close Project",
                            onAction: { t ->
                                ProjectDialogs.closeProjectDialog()
                            } as EventHandler<ActionEvent>,
                            accelerator: new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                    ),
                    new SeparatorMenuItem(),
                    new MenuItem(
                            text: "Exit",
                            onAction: { t ->
                                ApplicationUtil.exitColt()
                            } as EventHandler<ActionEvent>
                    )

            )
        }

        if (SystemInfo.isMac) {
            setUseSystemMenuBar(true)
        } else {
            setVisible(false)
            setManaged(false)
        }
    }

    private static Menu cloneMenu(Menu menu) {
        Menu result = new Menu()
        result.text = menu.text
        result.items.addAll(menu.items.collect {
            return cloneMenuItem(it)
        } as Collection<? extends MenuItem>)
        return result
    }

    private static MenuItem cloneMenuItem(MenuItem item) {
        if (item instanceof SeparatorMenuItem) {
            return new SeparatorMenuItem()
        } else if(item instanceof Menu) {
            return cloneMenu(item)
        } else {
            MenuItem result = new MenuItem()
            result.text = item.text
            result.onAction = item.onAction
            result.accelerator = item.accelerator
            result.id = item.id
            return result
        }
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
