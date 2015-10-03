package codeOrchestra.colt.core.rpc.discovery

import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.storage.ProjectStorageManager
import codeOrchestra.util.FileUtils
import codeOrchestra.util.ProjectHelper
import codeOrchestra.util.ThreadUtils

/**
 * @author Alexander Eliseyev
 */
class RPCServicePublisher extends Thread {

    private boolean mustStop;

    public void stopRightThere() {
        this.mustStop = true;
    }

    @Override
    public synchronized void start() {
        mustStop = false;
        super.start();
    }

    @Override
    public void run() {
        while (!mustStop) {
            ThreadUtils.sleep(1000);
            Project project = ProjectHelper.currentProject
            if (project == null) {
                return
            }
            File serviceInfoFile = new File(ProjectStorageManager.getOrCreateProjectStorageDir(), "rpc.info");
            FileUtils.write(serviceInfoFile, "localhost:" + CodeOrchestraRPCHttpServer.PORT);
        }
    }
}