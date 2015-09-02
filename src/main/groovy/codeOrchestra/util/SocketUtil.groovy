package codeOrchestra.util

/**
 * @author Alexander Eliseyev
 */
class SocketUtil {

    static final int MAX_ITERATIONS_COUNT = 50

    static int findAvailablePortStartingFrom(int port) {
        for (int i = 0; i < MAX_ITERATIONS_COUNT; i++) {
            if (isPortAvailable(port + i)) {
                return port + i;
            }
        }

        throw new RuntimeException("Can't obtain a network port in the range of " + port + "-" + (port + MAX_ITERATIONS_COUNT))
    }

    static boolean isPortAvailable(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            ds?.close();
            try {
                ss?.close();
            } catch (IOException ignored) {
            }
        }

        return false;
    }

}
