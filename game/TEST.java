import java.net.Inet4Address;

import java.net.UnknownHostException;

public class TEST {
    public static void main(String args[]) throws UnknownHostException {
        String ip = Inet4Address.getLocalHost().getHostAddress().toString();
        System.out.println(ip);
    }
}
