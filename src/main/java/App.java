import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class App {
    static void main() throws IOException {
        InetAddress addr = InetAddress.getByName("228.5.6.7");
        InetSocketAddress group = new InetSocketAddress(addr, 0);

        InetAddress anyIpv4 = Inet4Address.getByName("0.0.0.0");
        try(final MulticastSocket socket = new MulticastSocket(new InetSocketAddress(anyIpv4, 6789))) {
            socket.setSoTimeout(5_000);

            // DatagramSocketAdaptor#anyNetworkInterface
            final InetAddress[] addrs = new InetAddress[1];
            addrs[0] = anyIpv4;
            NetworkInterface ni = NetworkInterfaces.newNetworkInterface(addrs[0].getHostName(), 0, addrs);

            socket.joinGroup(group, ni);

            // Receive packets

            socket.leaveGroup(group, ni);
        }
    }

    private static class NetworkInterfaces
    {
        static final MethodHandle CONSTRUCTOR;

        static
        {
            try
            {
                final MethodHandles.Lookup l =
                        MethodHandles.privateLookupIn(NetworkInterface.class, MethodHandles.lookup());
                final MethodType methodType =
                        MethodType.methodType(void.class, String.class, int.class, InetAddress[].class);
                CONSTRUCTOR = l.findConstructor(NetworkInterface.class, methodType);
            }
            catch(final Exception e)
            {
                throw new ExceptionInInitializerError(e);
            }
        }

        /**
         * Creates a NetworkInterface with the given name index and addresses.
         */
        static NetworkInterface newNetworkInterface(final String name, final int index, final InetAddress[] addrs)
        {
            try
            {
                return (NetworkInterface)CONSTRUCTOR.invoke(name, index, addrs);
            }
            catch(final Throwable e)
            {
                throw new InternalError(e);
            }
        }
    }
}
