package server;

import bothSides.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server extends UnicastRemoteObject {
    //"Datenmodell"
    private static String serverLocation = "//localhost/player/";
    private static int port = 6666;
    private static int connPort = 6780; //ASCII für CP
    private ArrayList<String> idList;
    private boolean pending = true;
    private static LinkedList<bothSides.Card> drawPile = new LinkedList<Card>();
    private static LinkedList<bothSides.Card> playPile = new LinkedList<Card>();
    private static LinkedList<PlayerImpl> players= new LinkedList<PlayerImpl>();
    private static Integer drawCount = 0; //Adapterklasse, damit die Zahl global aktualisiert wird

    //Thread der die Verbindung zu einem Client herstellt
    private class ConnectionThread extends Thread{
        private Socket socket;

        public ConnectionThread(Socket socket){
            this.socket = socket;
        }

        public void run(){
            System.out.println("new Thread");
            BufferedWriter toClient = null;
            //ID erstellen
            String id;
            do {
                id = Integer.toString((int)(Math.random()*999999));
            }while (idList.contains(id));
            idList.add(id);

            //Spieler hinzufügen
            try {
                Player player = new PlayerImpl(drawPile,playPile,players,drawCount);
                System.out.println(serverLocation+id);
                Naming.rebind("//localhost/Player",player);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
                toClient.write(id);
                toClient.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Exceptions müssen noch behandelt werden
    public static void main(String[] args) throws IOException {
        //Server server = new Server();
        java.rmi.registry.LocateRegistry.createRegistry(1099);
        Player player = new PlayerImpl(drawPile,playPile,players,drawCount);
        Naming.rebind("//localhost/Player",player);
        System.out.println("Server started");
    }

    public Server() throws IOException {
        idList = new ArrayList<>();

        ServerSocket serversocket = new ServerSocket(connPort);
        java.rmi.registry.LocateRegistry.createRegistry(port);
        PlayerImpl player = new PlayerImpl(drawPile,playPile,players,drawCount);
        Naming.rebind("//localhost/Player",player);
        System.out.println("Server started");

        while (pending){
            Socket socket = serversocket.accept();
            Thread serviceThread = new ConnectionThread(socket);
            serviceThread.start();
        }
        System.out.println("fertig");
    }

    public void start(){
        pending = false;
    }

    public void createDeck(){
        //Typen vorbereiten
        List<TYPE> typeList = Arrays.stream(TYPE.values()).toList();
        typeList.removeAll(TYPE.getExcluded());

        //Farben vorbereiten
        List<COLOR> colorList = Arrays.stream(COLOR.values()).toList();
        colorList.removeAll(COLOR.getExcluded());

        //Deck bilden
        for (int i=0;i<2;i++){
            for(TYPE t : typeList) {
                for (COLOR c : colorList) {
                    if (t == TYPE.ZERO){continue;} //Nur eine Null pro Farbe
                    drawPile.add(new Card(t, c));
                }
            }
        }
        //Wunschkarten
        for (int i=0;i<4;i++){
            drawPile.add(new Card(TYPE.WILD,COLOR.MULTICOLORED));
            drawPile.add(new Card(TYPE.WILDFOUR,COLOR.MULTICOLORED));
        }
        Collections.shuffle(drawPile);
    }
}
