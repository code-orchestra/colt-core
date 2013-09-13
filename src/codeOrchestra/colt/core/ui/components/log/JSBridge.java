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
    public JSBridge(JSObject win) {
        win.setMember("app", this);
    }

    public static JSBridge create(JSObject win) {
        return new JSBridge(win);
    }

    public void resize(int height){
//        System.out.println("resize: " + height);
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
