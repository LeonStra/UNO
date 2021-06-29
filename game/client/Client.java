package client;

import java.io.*;
import java.net.Socket;

public class Client {
    private int port = 6666;
    private String serverLocation = "//localhost/Server";
    private String id;


    public static void main(String[] args) throws IOException {
        Client client = new Client();
    }

    public Client() throws IOException {
        connect();
    }

    public void connect() throws IOException {
        Socket socket = new Socket("127.0.0.1", 6780);

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        System.out.println(fromServer.readLine());
    }
}
