package server;

import bothSides.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

public class Server{
    //"Datenmodell"
    private String serverLocation = "//localhost/player/";
    private final int connPort = 6780; //ASCII für CP
    private boolean pending = true;
    private ServerSocket serversocket;
    private ArrayList<Socket> sockets= new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private LinkedList<bothSides.Card> drawPile = new LinkedList<Card>();
    private LinkedList<bothSides.Card> playPile = new LinkedList<Card>();
    private LinkedList<PlayerImpl> players= new LinkedList<PlayerImpl>();
    private Integer drawCount = 0; //Adapterklasse, damit die Zahl global aktualisiert wird

    //Thread der die Verbindung zu einem Client herstellt
    private class ConnectionThread extends Thread{
        private Socket socket;

        public ConnectionThread(Socket socket){
            this.socket = socket;
        }

        public void run(){
            BufferedWriter toClient;
            String id;

            //ID erstellen
            do {
                id = Integer.toString((int)(Math.random()*999999));
            }while (idList.contains(id));
            idList.add(id);

            //Spieler hinzufügen
            try {
                Player player = new PlayerImpl(drawPile,playPile,players,drawCount);
                System.out.println(serverLocation+id);
                Naming.rebind(serverLocation+id,player);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //Client antworten
            try {
                toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
                toClient.write(id);
                toClient.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Exceptions müssen noch behandelt werden
    public static void main(String[] args) throws IOException{
        Server server = new Server();
        ServerFrame frame = new ServerFrame(server);
        server.startSockets();
    }

    private Server () throws IOException {
        createDeck();
    }

    private void startSockets() throws IOException {
        serversocket = new ServerSocket(connPort);
        java.rmi.registry.LocateRegistry.createRegistry(1099);
        while (pending){
            try {
                Socket socket = serversocket.accept();
                sockets.add(socket);
                Thread serviceThread = new ConnectionThread(socket);
                serviceThread.start();
            }catch (SocketException e){ }
        }
    }

    void start() throws IOException {
        pending = false;
        serversocket.close();
        for(Socket s : sockets){
            s.close();
        }
    }

    private void createDeck(){
        //Deck bilden
        for (int i=0;i<2;i++){
            for(TYPE t : TYPE.values()) {
                if (TYPE.getExcluded().contains(t)){continue;}
                for (COLOR c : COLOR.values()) {
                    Card card = new Card(t,c);
                    if ((t == TYPE.ZERO && i==1)||COLOR.getExcluded().contains(c)){continue;}
                    drawPile.add(card);
                }
            }
        }
        //Wunschkarten
        for (int i=0;i<4;i++){
            drawPile.add(new Card(TYPE.WILD,COLOR.MULTICOLORED));
            drawPile.add(new Card(TYPE.WILDFOUR,COLOR.MULTICOLORED));
        }
        Collections.shuffle(drawPile);
        playPile.addFirst(drawPile.getFirst());
        drawPile.removeFirst();
    }
}
