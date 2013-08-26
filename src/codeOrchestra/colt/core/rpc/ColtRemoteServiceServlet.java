package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.logging.Logger;
import com.googlecode.jsonrpc4j.JsonRpcServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteServiceServlet extends HttpServlet {

    private final static ColtRemoteServiceServlet instance = new ColtRemoteServiceServlet();

    public static ColtRemoteServiceServlet getInstance() {
        return instance;
    }

    private static final Logger LOG = Logger.getLogger(ColtRemoteServiceServlet.class);

    private ColtRemoteServiceServlet() {
    }

    private ColtRemoteService coltRemoteService;
    private JsonRpcServer jsonRpcServer;

    public void refreshService() {
        this.coltRemoteService = ServiceProvider.get(ColtRemoteService.class);
        this.jsonRpcServer = new JsonRpcServer(this.coltRemoteService, ColtRemoteService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (jsonRpcServer != null) {
                jsonRpcServer.handle(req, resp);
            } else {
                resp.setStatus(500);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

}
