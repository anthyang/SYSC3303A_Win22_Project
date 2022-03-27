import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Simplifies some UDP send and receive operations
 */
public abstract class Host {
    private static int MAX_BUFFER_SIZE = 250;
    private String hostName;
    private DatagramSocket socket;

    /**
     * Define the host's name, use any port for communication
     * @param hostName the host's name
     */
    public Host(String hostName) {
        this.hostName = hostName;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Define the host's name, specify the port used for communication
     * @param hostName the host's name
     * @param port the port used for communication
     */
    public Host(String hostName, int port) {
        this.hostName = hostName;

        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a given payload to the specified address and port
     * @param data The data to send
     * @param addr The address for the packet
     * @param port The port for the packet
     */
    public void send(byte[] data, InetAddress addr, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listen for a packet
     * @return The packet that was received
     */
    public DatagramPacket receive() {
        byte[] buf = new byte[Host.MAX_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            this.socket.receive(packet);
        } catch (IOException e) {
            // Socket timed out
            this.log("Receive timed out. Shutting down.");
            return null;
        }

        return packet;
    }

    /**
     * Invoke an RPC call to the given address and port. This method blocks until a response is received
     * @param data The payload to send
     * @param addr The address of the call
     * @param port The port for the call
     * @return The response
     */
    public DatagramPacket rpcCall(byte[] data, InetAddress addr, int port) {
        this.send(data, addr, port);
        return this.receive();
    }

    /**
     * Log a message to console with a given timestamp
     * @param str the message to print
     */
    public void log(String str) {
        System.out.println(getTimestamp() + " " + this.hostName + ": " + str);
    }

    /**
     * Get a string representation of the current time
     * @return a timestamp
     */
    private String getTimestamp() {
        return Timestamp.from(ZonedDateTime.now().toInstant().truncatedTo(ChronoUnit.SECONDS)).toString();
    }

    /**
     * Serialize an object
     * @param o the object to serialize
     * @return an array of bytes representing the object
     */
    public static byte[] serialize(Serializable o) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(byteOut);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    /**
     * Deserialize an object
     * @param serializedData an array of bytes representing the object
     * @param clazz the class of the object
     * @param <T> the type of the object
     * @return the deserialized object
     */
    public static <T> T deserialize(byte[] serializedData, Class<T> clazz) {
        T o;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);

        try {
            ObjectInputStream ois = new ObjectInputStream(byteIn);
            o = clazz.cast(ois.readObject());
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not deserialize object");
        }

        return o;
    }

    /**
     * Get the port used by the host's socket
     * @return the port used by the host's socket
     */
    public int getPort() {
        return this.socket.getLocalPort();
    }
}
