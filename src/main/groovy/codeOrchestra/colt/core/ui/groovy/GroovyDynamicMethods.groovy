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

        addSetter(javafx.scene.Node, "newStyleClass", { String it ->
            styleClass.add(it)
        })
        addSetter(Parent, "newChildren", {List<MenuItem> it ->
            children.addAll(it)
        })
        addSetter(Menu, "newItems", {List<MenuItem> it ->
            items.addAll(it)
        })
    }

    private static void addSetter(Class clazz, String methodName, Closure methodBody) {
        addMethod(clazz, "set" + methodName.capitalize(), methodBody)
    }

    private static void addMethod(Class clazz, String methodName, Closure methodBody) {
        ExpandoMetaClass exp = new ExpandoMetaClass(clazz, false)
        exp."$methodName" = methodBody
        exp.initialize()
        clazz.metaClass = exp
    }
}
