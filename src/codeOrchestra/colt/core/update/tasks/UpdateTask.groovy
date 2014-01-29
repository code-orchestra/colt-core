package codeOrchestra.colt.core.update.tasks

import codeOrchestra.colt.core.net.ProxyModel
import codeOrchestra.util.FileUtils
import codeOrchestra.util.ThreadUtils
import javafx.concurrent.Task
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor


/**
 * @author Dima Kruk
 */
class UpdateTask extends Task<Void> {

    String url
    String copyTo

    UpdateTask(String url, String target) {
        this.url = url
        copyTo = target
    }

    @Override
    protected Void call() throws Exception {
        String tmpFilePath = "/Users/dimakruk/IdeaProjects/colt/updates/flex_sdk_mac.zip"// downloadFile(url, PathUtils.applicationBaseDir.path + File.separator + "updates")
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
            } else {
                File tmpFile = new File(tmpFilePath)
                FileUtils.copyFile(tmpFile, new File(copyTo + File.separator + tmpFile.name))
            }
        }
        return null
    }

    protected String downloadFile(String fileURL, String saveDir) throws IOException {
        println "downloadFile"
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
            String lastModified = httpConn.getHeaderField("Last-Modified")
            Date lastModifiedDate = new Date(lastModified)
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            updateTitle("Downloading...")

            long totalBytesRead = 0;
            int bytesRead
            byte[] buffer = new byte[inputStream.available()]
            long cur = 0
            long of = contentLength/104857.6
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer)
                buffer = new byte[inputStream.available()]
                totalBytesRead += bytesRead;
                updateProgress(totalBytesRead, contentLength)
                cur = totalBytesRead/10485.76
                updateMessage(cur * 0.01 + "MB of " + of * 0.1 + "MB")
                ThreadUtils.sleep(100)
                if (cancelled) {
                    break
                }
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
            return saveFilePath
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        return null
    }
}
