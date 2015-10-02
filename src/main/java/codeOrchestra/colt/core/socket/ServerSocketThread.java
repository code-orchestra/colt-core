package codeOrchestra.colt.core.socket;

import codeOrchestra.util.ExceptionUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public abstract class ServerSocketThread extends Thread {

    private int port;
    private ClientSocketHandlerFactory handlerFactory;
    private ServerSocket serverSocket;
    private boolean socketOpen;

    private List<ClientSocketHandler> clientSocketHandlers = new ArrayList<>();

    public ServerSocketThread(int port, ClientSocketHandlerFactory handlerFactory) {
        assert handlerFactory != null;
        this.handlerFactory = handlerFactory;
        this.port = port;
    }

    public final void run() {
        try {
            serverSocket = new ServerSocket(port);
            ClientSocketHandler lastHandler = null;

            socketOpen = true;

            while (!serverSocket.isClosed()) {
                // Wait to accept a new connection
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketException e) {
                    if (ExceptionUtils.isSocketClosed(e)) {
                        return;
                    }
                    // TODO: improve logging
                    // LOG.warning("Couldn't accept a socket", e);
                    continue;
                }

                // Close the previous socket if the multiple logging clients are disabled
                if (!allowMultipleConnections() && lastHandler != null) {
                    try {
                        lastHandler.close();
                    } catch (IOException e) {
                        // Ignore it
                    }
                }

                lastHandler = handlerFactory.createHandler(clientSocket);

                // Run the client socker handler thread
                clientSocketHandlers.add(lastHandler);
                new Thread(lastHandler).start();
            }
        } catch (IOException ignored) {
        }
    }

    protected abstract boolean allowMultipleConnections();

    public synchronized void openSocket() {
        if (socketOpen) {
            throw new IllegalStateException("Socket is open");
        }

        start();
    }

    public synchronized void closeSocket() {
        if (!socketOpen) {
            throw new IllegalStateException("Socket is closed");
        }

        socketOpen = false;

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // throw new RuntimeException("Error while attempting to close a server socket", e);
            }
        }

        clientSocketHandlers.forEach(ClientSocketHandler::stopRightThere);
        clientSocketHandlers.clear();
    }
}