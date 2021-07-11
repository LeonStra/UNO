package server;

import Exceptions.*;
import bothSides.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class PlayerImpl extends UnicastRemoteObject implements Player {
    protected String name;
    protected boolean myTurn;
    protected boolean increased;
    protected boolean cardDrawn;
    protected boolean saidUno;
    protected Hand hand;
    protected LinkedList<ChatMessage> chatHistory;
    protected Counter drawCount;
    protected LinkedList<Card> drawPile;
    protected LinkedList<Card> playPile;
    protected LinkedList<PlayerImpl> players;
    protected View view;

    //Spieler initialisieren
    public PlayerImpl(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<PlayerImpl> players, Counter drawCount, LinkedList<ChatMessage> chatHistory) throws RemoteException {
        this.name = "Unbekannt";
        this.myTurn = false;
        this.increased = false;
        this.cardDrawn = false;
        this.hand = new Hand();
        this.chatHistory = chatHistory;
        this.drawCount = drawCount;
        this.drawPile = drawPile;
        this.playPile = playPile;
        this.players = players;
        this.players.add(this);
    }

    //Spiel verlassen
    @Override
    public void leaveGame(){
        players.remove(this);
        setNews("Spiel verlassen");
    }

    //Karten hinzufügen
    protected void giveCards(int amount) throws RemoteException {
        //Karten ziehen
        for(int i=0; i<amount ;i++) {
            hand.add(drawPile.getFirst());
            drawPile.remove(drawPile.getFirst());
        }
        refreshView();
    }

    //--
    @Override
    public void takeDrawCount() throws RemoteException {
        if (myTurn) {
            giveCards(drawCount.getCounter());
            drawCount.setCounter(0);
        }
    }

    //Am Zug
    //StartSpieler
    public void startPlayer(){
        players.removeFirst();
        players.addLast(this);
        myTurn = true;
    }

    //Abschluss des Zuges
    public void next() throws RemoteException {
        if (!increased && drawCount.getCounter() > 0){
            takeDrawCount();
        }
        if (!saidUno && hand.size() == 1){
            giveCards(2);
        }
        cardDrawn = false;
        increased = false;
        myTurn = false;
        saidUno = false;
        view.changeDrawPass(true);
        try {
            players.getFirst().itsMyTurn();
        } catch (TurnException e) {
            e.printStackTrace();
        }
    }

    //Beginn des Zuges
    public void itsMyTurn() throws RemoteException, TurnException {
        if (!players.getFirst().equals(this)){throw new TurnException();}
        players.removeFirst();
        players.addLast(this);
        myTurn = true;
        players.forEach((p) ->{ p.refreshView();});

        //Ziehen?
        if (drawCount.getCounter() > 0){
            if ((playPile.getFirst().equalType(TYPE.DRAWTWO) && hand.containsType(TYPE.DRAWTWO)) || playPile.getFirst().equalType(TYPE.WILDFOUR) && hand.containsType(TYPE.WILDFOUR)){
                view.drawOrCounter(drawCount.getCounter());
            }else {
                takeDrawCount();
            }
        }
    }

    //Karte spielen
    @Override
    public void play(Card card) throws RemoteException, NotSuitableException {
        boolean wait = false;

        if (playPile.getFirst().suitable(card) && myTurn){
            switch (card.getcType()){
                case DRAWTWO:
                    drawCount.addCounter(2);
                    increased = true;
                    break;
                case REVERSE:
                    if (players.size() == 2){
                        players.addLast(players.getFirst());
                        players.removeFirst();
                    }else {
                        Collections.reverse(players);
                        players.removeFirst();
                        players.addLast(this);
                    }
                    break;
                case SKIP:
                    players.addLast(players.getFirst());
                    players.removeFirst();
                    players.forEach((p)->p.setNews("Aussetzen"));
                    break;
                case WILD:
                    view.selectColor();
                    wait = true;
                    break;
                case WILDFOUR:
                    drawCount.addCounter(4);
                    increased = true;
                    wait = true;
                    view.selectColor();
                    break;
            }
            hand.remove(card);
            playPile.addFirst(card);
            if (!wait) {
                next();
            }
            refreshView();
        } else {throw new NotSuitableException();}
    }

    //Kommunikation mit view
    //Ansicht erneuern
    public void refreshView(){
        Collections.sort(hand);
        try {
            view.refresh();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setNews(String txt){
        try {
            view.setNews(txt);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void refreshChat(){
        try {
            view.refreshChat(chatHistory);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //gewünschte Farbe

    @Override
    public void sayUno(){
        saidUno = true;
        players.forEach((p)->p.setNews(name+" hat UNO gerufen"));
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        chatHistory.addLast(new ChatMessage(this.getName(), message));
        players.forEach((p)->p.refreshChat());
    }


    @Override
    public void wish(COLOR c) throws RemoteException {
        if (myTurn && playPile.getFirst().getColor() == COLOR.MULTICOLORED){
            TYPE t = playPile.getFirst().getcType();
            playPile.removeFirst();
            playPile.addFirst(new Card(t,c));
            refreshView();
            next();
        }
    }

    //Karte ziehen
    @Override
    public void drawCard() throws RemoteException{
        if (myTurn && !cardDrawn) {
            giveCards(1);
            cardDrawn = true;
            view.changeDrawPass(false);
        }else if(myTurn){
            next();
        }
    }

    //Getter/Setter
    @Override
    public void setView(View view) throws RemoteException {
        if (this.view == null) {
            this.view = view;
            giveCards(7);
            hand.add(new Card(TYPE.DRAWTWO,COLOR.GREEN));
        }
    }
    @Override
    public void setName(String name){
        this.name = name;
    }

    @Override
    public void setSorting(boolean b) throws RemoteException {

    }

    @Override
    public ArrayList<Card> getHand() {
        return hand;
    }

    @Override
    public boolean getSorting() throws RemoteException {
        return false;
    }

    @Override
    public Card getTop(){
        try {
            return playPile.getFirst();
        }
        catch (NoSuchElementException e){
            return new Card(TYPE.UNKNOWN,COLOR.UNKNOWN);
        }
    }
    @Override
    public LinkedList<String> getNameList() throws RemoteException {
        LinkedList<String> names = new LinkedList<>();
        for (PlayerImpl p : players){
            names.add(p.getName());
        }
        names.addFirst(names.getLast());
        names.removeLast();
        return names;
    }
    public String getName() {
        return name;
    }
}