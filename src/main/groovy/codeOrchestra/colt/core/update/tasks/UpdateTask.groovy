package codeOrchestra.colt.core.update.tasks
import codeOrchestra.colt.core.net.ProxyModel
import codeOrchestra.util.FileUtils
import codeOrchestra.util.PathUtils
import javafx.concurrent.Task
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
/**
 * @author Dima Kruk
 */
class UpdateTask extends Task<Void> {

    String url
    boolean with_md5
    String copyTo
    boolean needCopy

    UpdateTask(String url, String target, boolean needCopy = true, boolean withMD5 = false) {
        this.url = url
        with_md5 = withMD5
        copyTo = target
        this.needCopy = needCopy
    }

    @Override
    protected Void call() throws Exception {
        String updatesPath = "${PathUtils.applicationBaseDir.path}${File.separator}updates"
        String tmpFilePath = downloadFile(url, updatesPath)
        if (with_md5) {
            downloadFile("${url}.MD5", updatesPath)
        }
        if (tmpFilePath != null && !cancelled) {
            String ext = tmpFilePath.split("\\.").last()
            if (ext == "zip") {
                updateTitle("Extracting...")
                updateMessage("")
                ZipFile zipFile = new ZipFile(tmpFilePath)
                zipFile.runInThread = true
                zipFile.extractAll(copyTo)
                ProgressMonitor progressMonitor = zipFile.progressMonitor

                while (progressMonitor.state == ProgressMonitor.STATE_BUSY) {
                    updateProgress(progressMonitor.percentDone, 100)
                    updateMessage(progressMonitor.fileName)
                }

                if (progressMonitor.getResult() == ProgressMonitor.RESULT_ERROR) {
                    // Any exception can be retrieved as below:
                    if (progressMonitor.getException() != null) {
                        progressMonitor.getException().printStackTrace()
                        throw progressMonitor.getException()
                    } else {
                        updateMessage("An error occurred without any exception")
                    }
                }
            } else if (needCopy) {
                File tmpFile = new File(tmpFilePath)
                FileUtils.copyFile(tmpFile, new File(copyTo + File.separator + tmpFile.name))
            }
        }
        return null
    }

    protected String downloadFile(String fileURL, String saveDir) throws IOException {
        File sDir = new File(saveDir)
        if (!sDir.exists()) {
            sDir.mkdirs()
        }

        URL url = new URL(fileURL);
        ProxyModel proxyModel = ProxyModel.instance
        HttpURLConnection httpConn
        if (proxyModel.usingProxy()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyModel.host, proxyModel.port as int))
            httpConn = (HttpURLConnection) url.openConnection(proxy)
        } else {
            httpConn = (HttpURLConnection) url.openConnection()
        }
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + "filename=".length());
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
            }

            // opens an output stream to save into file
            String saveFilePath = saveDir + File.separator + fileName;
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveFilePath));

            updateTitle("Downloading...")

            long totalBytesRead = 0;
            // opens input stream from the HTTP connection
            InputStream inputStream = new BufferedInputStream(httpConn.inputStream)
            long of = contentLength/104857.6
            byte[] buffer = new byte[1024]
            int bytesRead
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead;
                updateProgress(totalBytesRead, contentLength)
                long cur = totalBytesRead/10485.76
                updateMessage("${cur * 0.01}MB of ${of * 0.1}MB")
                if (cancelled) {
                    saveFilePath = null
                    break
                }
            }

            outputStream.close();
            inputStream.close();
            httpConn.disconnect()
            println "File downloaded"
            return saveFilePath
        } else {
            println "No file to download. Server replied HTTP code: $responseCode"
        }
        httpConn.disconnect();
        return null
    }
}