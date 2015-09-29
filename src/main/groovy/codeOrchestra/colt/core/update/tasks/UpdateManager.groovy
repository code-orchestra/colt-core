package codeOrchestra.colt.core.update.tasks

import codeOrchestra.colt.core.net.ProxyModel
import codeOrchestra.util.PathUtils
import codeOrchestra.util.SystemInfo
/**
 * @author Dima Kruk
 */
class UpdateManager {

    private static String UPDATE_URL = "https://github.com/code-orchestra/colt-build/raw/master/updates"

    public static ArrayList<UpdateTask> checkForUpdate() {
        File baseDir = PathUtils.applicationBaseDir
        File jarDir

        if (SystemInfo.isMac) {
            jarDir = new File(baseDir, "Contents/Java")
        } else if (SystemInfo.isWindows){
            jarDir = baseDir;
        }

        ArrayList<UpdateTask> result = new ArrayList<>()

        try {
            if (checkJar("colt-core.jar", jarDir)) {
                result.add(new UpdateTask("${UPDATE_URL}/colt-core.jar", jarDir.path, false, true))
            }
            if (checkJar("colt-as.jar", jarDir)) {
                result.add(new UpdateTask("$UPDATE_URL/colt-as.jar", jarDir.path, false, true))
            }
            if (checkJar("colt-updater.jar", jarDir)) {
                result.add(new UpdateTask("${UPDATE_URL}/colt-updater.jar", jarDir.path, false, true))
            }
        } catch (Exception ignored) {
            return null
        }

        return result
    }

    private static boolean checkJar(String fileName, File baseDir) {
        File curJar = new File(baseDir, fileName)
        if (!curJar.exists()) {
            return false
        }
        URL url = new URL("$UPDATE_URL/$fileName")
        ProxyModel proxyModel = ProxyModel.instance
        HttpURLConnection httpConn
        if (proxyModel.usingProxy()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyModel.host, proxyModel.port as int))
            httpConn = (HttpURLConnection) url.openConnection(proxy)
        } else {
            httpConn = (HttpURLConnection) url.openConnection()
        }
        int responseCode = httpConn.getResponseCode()
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String lastModified = httpConn.getHeaderField("Last-Modified")
            if (lastModified == null) lastModified = httpConn.getHeaderField("Date")
            return new Date(lastModified).after(new Date(curJar.lastModified()))
        }
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return false
        }
        throw new Exception()
    }
}