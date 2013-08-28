package codeOrchestra.colt.core.http;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * @author Alexander Eliseyev
 */
public class CodeOrchestraRPCHttpServer {

  public static final int PORT = 8092;
  
  private static CodeOrchestraRPCHttpServer instance = new CodeOrchestraRPCHttpServer();
  
  public static CodeOrchestraRPCHttpServer getInstance() {
    return instance;
  }

  private Server server;
  private HandlerList activeHandlers;
  
  private ServletHandler servletHandler = new ServletHandler();

  private Map<String, Handler> handlersMap = new HashMap<>();

  public void init() {
    server = new Server(PORT); // TODO: make configurable

    activeHandlers = new HandlerList();
    server.setHandler(activeHandlers);

    ContextHandler contextHandler = new ContextHandler();
    contextHandler.setContextPath("/rpc");
    contextHandler.setHandler(servletHandler);
    contextHandler.setServer(server);
    addHandler(contextHandler, "/rpc");    

    try {
      server.start();
    } catch (Exception e) {
      throw new RuntimeException("Can't start jetty server", e);
    }
  }

  public void addServlet(HttpServlet servlet, String alias) {
    servletHandler.addServletWithMapping(new ServletHolder(servlet), alias);
  }

  private void addHandler(Handler handler, String alias) {
    Handler existingHandler = handlersMap.get(alias);
    if (existingHandler != null) {
      activeHandlers.removeHandler(existingHandler);
    }

    activeHandlers.addHandler(handler);
    handlersMap.put(alias, handler);

    reloadServer();
  }

  private void reloadServer() {
    try {
      System.out.println("Reloading RCP HTTP server");
      
      server.stop();
      server.start();
    } catch (Exception e) {
      throw new RuntimeException("Can't reload jetty server", e);
    }
  }

  public void dispose() {
    try {
      server.stop();
    } catch (Exception e) {
      // ignore
    }
  }
  
}
