import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Simplifies some UDP send and receive operations
 */
public abstract class Host {
    private static int MAX_BUFFER_SIZE = 100;

    /**
     * Send a given payload to the specified address and port
     * @param socket The socket to send on
     * @param data The data to send
     * @param addr The address for the packet
     * @param port The port for the packet
     */
    public void send(DatagramSocket socket, byte[] data, InetAddress addr, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listen for a packet
     * @param socket The socket to listen on
     * @return The packet that was received
     */
    public DatagramPacket receive(DatagramSocket socket) {
        byte[] buf = new byte[Host.MAX_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet;
    }

    /**
     * Invoke an RPC call to the given address and port. This method blocks until a response is received
     * @param socket The socket to send/receive
     * @param data The payload to send
     * @param addr The address of the call
     * @param port The port for the call
     * @return The response
     */
    public DatagramPacket rpcCall(DatagramSocket socket, byte[] data, InetAddress addr, int port) {
        this.send(socket, data, addr, port);
        return this.receive(socket);
    }
}
