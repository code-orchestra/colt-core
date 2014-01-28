package codeOrchestra.colt.core.net

import codeOrchestra.groovyfx.FXBindable
import groovy.transform.Canonical

import java.util.prefs.Preferences

/**
 * @author Dima Kruk
 */
@Canonical
@FXBindable
class ProxyModel {
    private static ProxyModel ourInstance = new ProxyModel()

    public static ProxyModel getInstance() {
        return ourInstance
    }

    Preferences preferences

    boolean useProxy = false
    String host, port, username, password

    ProxyModel() {
        preferences = Preferences.userNodeForPackage(ProxyModel.class)
        useProxy = preferences.getBoolean("proxy.use", false)
        host = preferences.get("proxy.host", "")
        port = preferences.getInt("proxy.port", 8080)
        username = preferences.get("proxy.name", "")
        password = preferences.get("proxy.pass", "")
    }

    public void save() {
        preferences.putBoolean("proxy.use", useProxy)
        preferences.put("proxy.host", host)
        preferences.putInt("proxy.port", port as int)
        preferences.put("proxy.name", username)
        preferences.put("proxy.pass", password)
        preferences.sync()
    }

    public boolean usingProxy() {
        return useProxy && !host.isEmpty()
    }


}
