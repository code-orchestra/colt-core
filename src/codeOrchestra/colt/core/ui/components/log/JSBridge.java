package codeOrchestra.colt.core.ui.components.log;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;

import java.util.HashMap;

/**
 * @author Eugene Potapenko
 */
public class JSBridge {
    private WebEngine engine;

    public JSBridge(WebEngine engine) {
        this.engine = engine;
        JSObject win = (JSObject) engine.executeScript("window");
        win.setMember("app", this);
    }

    public static JSBridge create(WebEngine engine) {
        return new JSBridge(engine);
    }

    public void resize(int height){
        System.out.println("resize: " + height);
    }

    public void setClipboard(String str){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        HashMap<DataFormat, Object> content = new HashMap<>();
        content.put(DataFormat.PLAIN_TEXT, str);
        clipboard.setContent(content);
    }

    public String getClipboard(){
        return Clipboard.getSystemClipboard().getString();
    }
}
