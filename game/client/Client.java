package client;

import bothSides.Player;

import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class Client {
    private final int port = 1099;
    private String ip = "127.0.0.1";
    private String id;
    private Player player;


    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        Client client = new Client();
    }

    public Client() throws IOException, NotBoundException, InterruptedException {
        connect();
        BoardFrame boardFrame = new BoardFrame();
    }

    public void connect() throws IOException, NotBoundException {
        Socket socket = new Socket(ip, 6780);

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        id = fromServer.readLine();
        System.out.println("rmi://"+ip+"/player/"+id);
        player = (Player) Naming.lookup("rmi://"+ip+"/player/"+id);
    }
}
