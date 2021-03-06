package server;

import bothSides.Exceptions.*;
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
    protected boolean sorting;
    protected boolean alreadyPlayed;
    protected Hand hand;
    protected LinkedList<ChatMessage> chatHistory;
    protected Counter drawCount;
    protected Pile drawPile;
    protected Pile playPile;
    protected LinkedList<PlayerImpl> players;
    protected View view;

    //Spieler initialisieren
    public PlayerImpl(Pile drawPile, Pile playPile, LinkedList<PlayerImpl> players, Counter drawCount, LinkedList<ChatMessage> chatHistory) throws RemoteException {
        this.name = "Unbekannt";
        this.myTurn = false;
        this.increased = false;
        this.cardDrawn = false;
        this.saidUno = false;
        this.sorting = false;
        this.alreadyPlayed = false;
        this.hand = new Hand();
        this.chatHistory = chatHistory;
        this.drawCount = drawCount;
        this.drawPile = drawPile;
        this.playPile = playPile;
        this.players = players;
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
        System.out.println("Next Sup");
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
        alreadyPlayed = false;
        view.changeDrawPass(true);
        if (hand.size() == 0) {
            leaveGame();
            setNews( getName() + "won the Game");
        }
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
    public void play(Card card) throws RemoteException, NotSuitableException, TurnException {
        boolean wait = false;

        if (playPile.getFirst().suitable(card) && myTurn && !alreadyPlayed){
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
            alreadyPlayed = true;


            if (!wait) {
                next();
            }
            refreshView();
        } else {if (!myTurn){throw new TurnException();}else {throw new NotSuitableException();}}
    }

    //Kommunikation mit view
    //Ansicht erneuern
    public void refreshView(){
        if (sorting){Collections.sort(hand);}
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

    //Uno-Button gedrückt
    @Override
    public void sayUno(){
        saidUno = true;
        players.forEach((p)->p.setNews(name+" hat UNO gerufen"));
    }

    //Senden-Button
    @Override
    public void sendMessage(String message) throws RemoteException {
        chatHistory.addLast(new ChatMessage(this.getName(), message));
        players.forEach((p)->p.refreshChat());
    }

    //Karte ziehen-Button
    @Override
    public void drawCard() throws RemoteException{
        if (myTurn && !cardDrawn && !alreadyPlayed) {
            giveCards(1);
            cardDrawn = true;
            view.changeDrawPass(false);
        }else if(myTurn && !alreadyPlayed){
            next();
        }
    }

    //Farbe nach Wunschkarte gewählt
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

    //Getter/Setter
    @Override
    public void setView(View view) throws RemoteException {
        if (this.view == null) {
            this.view = view;
            giveCards(7);
        }
        hand.add(new Card(TYPE.THREE,COLOR.YELLOW));
        hand.add(new Card(TYPE.FOUR,COLOR.YELLOW));
        hand.add(new Card(TYPE.WILD,COLOR.MULTICOLORED));
    }
    @Override
    public void setName(String name){
        this.name = name;
    }
    @Override
    public void setSorting(boolean b) throws RemoteException {
        sorting = b;
        refreshView();
    }

    @Override
    public ArrayList<Card> getHand() {
        return hand;
    }
    @Override
    public boolean getSorting() throws RemoteException {
        return sorting;
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
    @Override
    public String getName() {
        return name;
    }
}