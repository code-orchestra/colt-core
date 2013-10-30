package codeOrchestra.colt.core.jmdns;

import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.model.Project;
import codeOrchestra.util.LocalhostUtil;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public class JmDNSFacade {

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
        try {
            jmDNSObject = JmDNS.create(LocalhostUtil.getLocalhostIp());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateServiceInfo(Project project) {
        final Map<String, String> values = new HashMap<>();

        values.put("name", project.getName());
        values.put("path", project.getPath());
        values.put("timestamp", String.valueOf(System.currentTimeMillis()));

        serviceInfo = ServiceInfo.create("_colt._tcp.local.", getServiceName(project), CodeOrchestraRPCHttpServer.PORT, 0, 0, values);
    }

    public void updateCurrentServiceInfo(Project project, Map<String, String> values) {
        values.put("name", project.getName());
        values.put("path", project.getPath());
        values.put("timestamp", String.valueOf(System.currentTimeMillis()));

        serviceInfo.setText(values);
    }

    private String getServiceName(Project project) {
        return "colt::" + System.currentTimeMillis() + "::" + project.getName();
    }

    public void init(final Project project) {
        new Thread(() -> {
            doInit(project);
        }).start();
    }

    private synchronized void doInit(Project project) {
        updateServiceInfo(project);
        try {
            jmDNSObject.registerService(serviceInfo);
        } catch (IOException e) {
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
