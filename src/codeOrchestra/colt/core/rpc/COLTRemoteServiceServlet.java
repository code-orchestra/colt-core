package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.ServiceProvider;
import codeOrchestra.colt.core.logging.Logger;
import com.googlecode.jsonrpc4j.JsonRpcServer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteServiceServlet extends HttpServlet {
  
  private static final Logger LOG = Logger.getLogger(COLTRemoteServiceServlet.class);
  
  private COLTRemoteService coltRemoteService;
  private JsonRpcServer jsonRpcServer;
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPost(req, resp);
  }
  
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    try {
      jsonRpcServer.handle(req, resp);
    } catch (IOException e) {
      LOG.error(e);
    }
  }

  public void init(ServletConfig config) {
    this.coltRemoteService = ServiceProvider.get(COLTRemoteService.class);
    this.jsonRpcServer = new JsonRpcServer(this.coltRemoteService, COLTRemoteService.class);
  }

}
