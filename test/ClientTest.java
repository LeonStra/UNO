import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class ClientTest {
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        Remote t = Naming.lookup("rmi://"+"127.0.0.1"+"/player/"+"80664");
        System.out.println(t);
    }
}
