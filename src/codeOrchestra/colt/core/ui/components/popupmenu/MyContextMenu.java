package codeOrchestra.colt.core.ui.components.popupmenu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Skin;

/**
 * @author Dima Kruk
 */
public class MyContextMenu extends ContextMenu {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MyContextMenuSkin(this);
    }
}
