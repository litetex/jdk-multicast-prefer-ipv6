import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class App {
    static void main() throws IOException {
        InetAddress addr = InetAddress.getByName("228.5.6.7");
        InetSocketAddress group = new InetSocketAddress(addr, 0);

        try(final MulticastSocket socket = new MulticastSocket(new InetSocketAddress(Inet4Address.getByName("0.0.0.0"), 6789))) {
            socket.setSoTimeout(5_000);
            socket.joinGroup(group, null);

            // Receive packets

            socket.leaveGroup(group, null);
        }
    }
}
