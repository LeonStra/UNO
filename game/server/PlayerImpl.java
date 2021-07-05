package server;

import Exceptions.*;
import bothSides.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class PlayerImpl extends UnicastRemoteObject implements Player {
    private String name;
    private boolean myTurn;
    private boolean increased;
    private boolean cardDrawn;
    private Integer drawCount;
    private Hand hand;
    private LinkedList<Card> drawPile;
    private LinkedList<Card> playPile;
    private LinkedList<PlayerImpl> players;
    private View view;

    //Spieler initialisieren
    public PlayerImpl(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<PlayerImpl> players, Integer drawCount) throws RemoteException {
        this.myTurn = false;
        this.increased = false;
        this.drawCount = drawCount;
        this.hand = new Hand();
        this.drawPile = drawPile;
        this.playPile = playPile;
        this.players = players;
        this.players.add(this);
        this.name = "Ubekannt";
    }

    //Spiel verlassen
    @Override
    public void leaveGame(){
        players.remove(this); }

    //Karten hinzufügen
    private void giveCards(int amount) throws RemoteException {
        //Karten ziehen
        for(int i=0; i<amount ;i++) {
            hand.add(drawPile.getFirst());
            drawPile.remove(drawPile.getFirst());
        }
        view.refresh();
    }

    //--
    @Override
    public void takeDrawCount() throws RemoteException {
        if (myTurn) {
            giveCards(drawCount);
            drawCount = 0;
        }
    }

    //Am Zug
    //StartSpieler
    public void startPlayer(){
        players.removeFirst();
        players.addLast(this);
        myTurn = true;
    }

    //Ablschuss des Zuges
    public void next() throws RemoteException {
        if (!increased && drawCount > 0){
            takeDrawCount();
        }
        cardDrawn = false;
        increased = false;
        myTurn = false;
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
        everyoneRefresh();

        //Ziehen?
        if (drawCount > 0){
            if ((playPile.getFirst().equalType(TYPE.DRAWTWO) && hand.containsType(TYPE.DRAWTWO)) || playPile.getFirst().equalType(TYPE.WILDFOUR) && hand.containsType(TYPE.WILDFOUR)){
                view.drawOrCounter(drawCount);
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
                    System.out.println("2+");
                    drawCount += 2;
                    increased = true;
                    break;
                case REVERSE:
                    System.out.println("Reverse");
                    Collections.reverse(players);
                    players.removeFirst();
                    players.addLast(this);
                    break;
                case SKIP:
                    System.out.println("SKIP");
                    players.removeFirst();
                    players.addLast(this);
                    view.setNews("Aussetzen");
                    break;
                case WILD:
                    System.out.println("Wunschkarte");
                    view.selectColor();
                    wait = true;
                    break;
                case WILDFOUR:
                    System.out.println("4+");
                    drawCount += 4;
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
            view.refresh();
        } else {throw new NotSuitableException();}
    }

    //Kommunikation mit view
    //Von jedem Spieler die Ansicht erneuern
    private void everyoneRefresh() throws RemoteException {
        for (PlayerImpl p : players){
            p.refreshView();
        }
    }

    //Ansicht erneuern
    public void refreshView() throws RemoteException {
        view.refresh();
    }

    //gewünschte Farbe
    @Override
    public void wish(COLOR c) throws RemoteException {
        TYPE t = playPile.getFirst().getcType();
        playPile.removeFirst();
        playPile.addFirst(new Card(t,c));
        view.refresh();
        next();
        System.out.println(playPile.getFirst().getPath());
    }

    //Karte ziehen
    @Override
    public void drawCard() throws RemoteException{
        if (myTurn && !cardDrawn) {
            giveCards(1);
            cardDrawn = true;
        }
    }

    //Getter/Setter
    @Override
    public void setView(View view) throws RemoteException {
        if (this.view == null) {
            this.view = view;
            giveCards(7);
        }
    }
    @Override
    public void setName(String name){
        this.name = name;
    }
    @Override
    public ArrayList<Card> getHand() {
        return hand;
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