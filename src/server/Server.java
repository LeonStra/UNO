package server;

import Exceptions.*;
import bothSides.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

public class Server{
    private final String serverLocation = "//localhost/player/";
    private final int connPort = 6969;

    private boolean extended = true;
    private boolean pending = true;
    private ServerSocket serversocket;
    private ArrayList<Socket> sockets;
    private ArrayList<String> idList;

    //"Datenmodell"
    private LinkedList<bothSides.Card> drawPile;
    private LinkedList<bothSides.Card> playPile;
    private LinkedList<PlayerImpl> players;
    private LinkedList<ChatMessage> chatHistory;
    private Counter drawCount; //Adapterklasse, damit die Zahl global aktualisiert wird

    public static void main(String[] args) throws IOException{
        new Server();
    }
    
    //Thread der die Verbindung zu einem Client herstellt
    private class ConnectionThread extends Thread{
        private Socket socket;

        public ConnectionThread(Socket socket){
            this.socket = socket;
        }

        public void run(){
            //ID erstellen
            String id;
            do {
                id = Integer.toString((int)(Math.random()*999999));
            }while (idList.contains(id));
            idList.add(id);

            //Spieler hinzufügen
            try {
                PlayerImpl player = extended?new ExtPlayerImpl(drawPile,playPile,players,drawCount,chatHistory) : new PlayerImpl(drawPile,playPile,players,drawCount,chatHistory);
                players.add(player);
                System.out.println(serverLocation+id);
                Naming.rebind(serverLocation+id,player);
            } catch (RemoteException | MalformedURLException e) {
                e.printStackTrace();
            }

            //Client antworten
            try {
                BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                toClient.write(id);
                toClient.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Server () throws IOException {
        this.sockets = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.drawPile = new LinkedList<>();
        this.playPile = new LinkedList<>();
        this.players = new LinkedList<>();
        this.chatHistory = new LinkedList<>();
        this.drawCount = new Counter();
        this.drawCount.setCounter(0);
        new ServerFrame(this);
        createDeck();
        startSockets();
    }

    private void startSockets() throws IOException {
        serversocket = new ServerSocket(connPort);
        java.rmi.registry.LocateRegistry.createRegistry(1099);
        while (pending){
            try {
                Socket socket = serversocket.accept();
                sockets.add(socket);
                new ConnectionThread(socket).start();
            }catch (SocketException e){
                //e.printStackTrace();
            }
        }
    }

    void start() throws IOException, TurnException {
        pending = false;
        serversocket.close();
        Collections.shuffle(players);
        players.getFirst().startPlayer();
        for(Socket s : sockets){
            s.close();
        }

    }

    //Deck bilden
    private void createDeck(){
        //Normale Karten
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