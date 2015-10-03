package codeOrchestra.colt.core.socket;

import codeOrchestra.colt.core.session.SocketWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Alexander Eliseyev
 */
public abstract class ClientSocketHandler implements Runnable, SocketWriter {

    private static final byte BYTE_DELIMITER = (byte) 0;
    private static final String STRING_DELIMITER = "\u0000";
    private static final String CHARSET = "UTF-8";

    private Socket clientSocket;
    private PrintWriter socketOut;
    private boolean closeRequested;

    private boolean shouldStop;

    private final Object monitor = new Object();

    public ClientSocketHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try {
            this.socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException("Error while initializing client socket", e);
        }
    }

    @Override
    public void closeSocket() {
        try {
            close();
        } catch (IOException ignored) {
        }
    }

    protected abstract void handle(String str);

    @Override
    public void writeToSocket(String str) {
        socketWrite(str);
    }

    public void close() throws IOException {
        synchronized (monitor) {
            closeRequested = true;
            clientSocket.close();
        }
    }

    protected void socketWrite(String msg) {
        socketOut.println(msg + STRING_DELIMITER);
        socketOut.flush();
    }

    public void stopRightThere() {
        this.shouldStop = true;
    }

    public final void run() {
        try {
            clientSocket.setTcpNoDelay(true);

            InputStream in = clientSocket.getInputStream();
            StringBuilder soFar = new StringBuilder();
            byte[] buf = new byte[1024];

            while (!shouldStop) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }

                // collect all the bytes waiting on the input stream
                int avail = in.available();
                while (avail > 0) {
                    int amt = avail;

                    if (amt > buf.length) amt = buf.length;
                    amt = in.read(buf, 0, amt);

                    int marker = 0;
                    for (int i = 0; i < amt; i++) {
                        // scan for the zero-byte EOM delimiter
                        if (buf[i] == BYTE_DELIMITER) {
                            String tmp = new String(buf, marker, i - marker, CHARSET);
                            soFar.append(tmp);
                            handle(soFar.toString());
                            soFar.setLength(0);
                            marker = i + 1;
                        }
                    }

                    if (marker < amt) {
                        // save all so far, still waiting for the final EOM
                        soFar.append(new String(buf, marker, amt - marker, CHARSET));
                    }
                    avail = in.available();
                }
            }
        } catch (IOException e) {
            synchronized (monitor) {
                if (!closeRequested || !clientSocket.isClosed()) {
                    throw new RuntimeException("Error while handling client socket", e);
                }
            }
        }
    }
}
