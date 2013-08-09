package codeOrchestra.colt.core.ui.components.log;

import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;

/**
 * @author Eugene Potapenko
 */
public abstract class JSBridge {
    private WebEngine engine;

    protected JSBridge(WebEngine engine) {
        this.engine = engine;
        JSObject win = (JSObject) engine.executeScript("window");
        win.setMember("app", this);
    }

    abstract void resize(int height);
}
