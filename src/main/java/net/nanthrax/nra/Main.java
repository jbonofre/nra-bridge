package net.nanthrax.nra;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting NRA bridge ...");
        QueuedThreadPool threadPool = new QueuedThreadPool(200, 8, 60000);
        threadPool.setName("nra-bridge-http");

        Server server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server, -1, -1);
        connector.setPort(8080);
        connector.setHost("0.0.0.0");
        connector.setAcceptQueueSize(0);

        server.addConnector(connector);

        ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servlets.setContextPath("/");
        server.setHandler(servlets);

        servlets.addServlet(BridgeServlet.class, "/nra/*");

        server.start();

        System.out.println("NRA bridge started and listening on 0.0.0.0:8080/nra");
    }

}
