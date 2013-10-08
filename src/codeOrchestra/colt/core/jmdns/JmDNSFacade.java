package codeOrchestra.colt.core.jmdns;

import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.util.LocalhostUtil;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public class JmDNSFacade {

    public static final String NONE_PROJECT_NAME = "NONE";

    private static JmDNSFacade instance;

    public synchronized static JmDNSFacade getInstance() {
        if (instance == null) {
            instance = new JmDNSFacade();
        }
        return instance;
    }

    private ServiceInfo serviceInfo;
    private JmDNS jmDNSObject;

    public JmDNSFacade() {
        updateServiceInfo(NONE_PROJECT_NAME);
        try {
            jmDNSObject = JmDNS.create(LocalhostUtil.getLocalhostIp());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateServiceInfo(String projectName) {
        serviceInfo = ServiceInfo.create("_colt._tcp.local.", getServiceName(projectName), CodeOrchestraRPCHttpServer.PORT, "COLT RPC");
    }

    private String getServiceName(String projectName) {
        return "colt::" + projectName + "::" + System.currentTimeMillis();
    }

    public void setProjectName(final String projectName) {
        new Thread(() -> {
            doSetProjectName(projectName);
        }).start();
    }

    private synchronized void doSetProjectName(String projectName) {
        jmDNSObject.unregisterService(serviceInfo);
        updateServiceInfo(projectName);
        try {
            jmDNSObject.registerService(serviceInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        new Thread(this::doInit).start();
    }

    private synchronized void doInit() {
        try {
            jmDNSObject.registerService(serviceInfo);
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        }
    }

    public void dispose() {
        new Thread(this::doDispose).start();
    }

    private synchronized void doDispose() {
        if (jmDNSObject != null) {
            jmDNSObject.unregisterService(serviceInfo);
            try {
                jmDNSObject.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }


}
