package server;

import Exceptions.NotSuitableException;
import Exceptions.TurnException;
import bothSides.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class PlayerImpl extends UnicastRemoteObject implements Player {
    private String name;
    private boolean myTurn;
    private boolean increased;
    private Integer drawCount;
    private Hand hand;
    private LinkedList<Card> drawPile;
    private LinkedList<Card> playPile;
    private LinkedList<PlayerImpl> players;
    private View view;

    //Konstruktor
    public PlayerImpl(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<PlayerImpl> players, Integer drawCount) throws RemoteException {
        this.myTurn = false;
        this.increased = false;
        this.drawCount = drawCount;
        this.hand = new Hand();
        this.drawPile = drawPile;
        this.playPile = playPile;
        this.players = players;
        this.players.add(this);
        this.name = "Hallo";
    }

    //Spiel verlassen
    @Override
    public void leaveGame(){
        players.remove(this); }

    //Karten
    @Override
    public void drawCard() throws RemoteException{
        if (myTurn) {
            giveCards(1);
        }
    }

    private void giveCards(int amount) throws RemoteException {
        //Karten ziehen
        for(int i=0; i<amount ;i++) {
            hand.add(drawPile.getFirst());
            drawPile.remove(drawPile.getFirst());
        }
        view.refresh();
    }

    @Override
    public void takeDrawCount() throws RemoteException {
        if (myTurn) {
            giveCards(drawCount);
            drawCount = 0;
        }
    }

    //Am Zug
    public void startPlayer(){
        players.removeFirst();
        players.addLast(this);
        myTurn = true;
    }
    public void next() throws RemoteException {
        if (!increased && drawCount > 0){
            takeDrawCount();
        }
        increased = false;
        myTurn = false;
        try {
            players.getFirst().itsMyTurn();
        } catch (TurnException e) {
            e.printStackTrace();
        }
    }

    public void itsMyTurn() throws RemoteException, TurnException {
        if (!players.getFirst().equals(this)){throw new TurnException();}
        players.removeFirst();
        players.addLast(this);
        myTurn = true;
        view.refresh();

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

    //Aktion Rückrufe
    @Override
    public void wish(COLOR c) throws RemoteException {
        TYPE t = playPile.getFirst().getcType();
        playPile.removeFirst();
        playPile.addFirst(new Card(t,c));
        view.refresh();
        next();
        System.out.println(playPile.getFirst().getPath());
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

    public String getName() {
        return name;
    }

    @Override
    public ArrayList<String> getNameList() throws RemoteException {
        ArrayList<String> names = new ArrayList<>();
        for (PlayerImpl p : players){
            names.add(p.getName());
        }
        return names;
    }
}