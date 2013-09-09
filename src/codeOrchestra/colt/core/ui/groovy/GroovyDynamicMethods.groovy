package codeOrchestra.colt.core.ui.groovy

import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

/**
 * @author Eugene Potapenko
 */
class GroovyDynamicMethods {

    private static inited = false

    static void init() {
        if(inited)return
        inited = true

        ExpandoMetaClass menuExpando = new ExpandoMetaClass(Menu, false)
        menuExpando.setNewItems = {List<MenuItem> it ->
            items.addAll(it)
        }
        menuExpando.initialize()
        Menu.metaClass = menuExpando


        ExpandoMetaClass parentExpando = new ExpandoMetaClass(Parent, false)
        parentExpando.setNewChildren = {List<Node> it ->
            children.addAll(it)
        }
        parentExpando.initialize()
        Parent.metaClass = parentExpando


        ExpandoMetaClass nodeExpando = new ExpandoMetaClass(Node, false)
        nodeExpando.setNewStyleClass = {String it ->
            styleClass.add(it)
        }
        nodeExpando.initialize()
        Node.metaClass = nodeExpando
    }
}
