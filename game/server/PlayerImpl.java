package server;

import bothSides.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class PlayerImpl extends UnicastRemoteObject implements Player {
    private boolean myTurn;
    private Integer drawCount;
    private ArrayList<Card> hand;
    //private BoardFrame board;
    private LinkedList<Card> drawPile;
    private LinkedList<Card> playPile;
    private LinkedList<PlayerImpl> players;

    //Konstruktor
    public PlayerImpl(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<PlayerImpl> players, Integer drawCount) throws RemoteException {
        this.myTurn = false;
        this.drawCount = drawCount;
        this.hand = new ArrayList<>();
        //this.board = new BoardFrame(this);
        this.drawPile = drawPile;
        this.playPile = playPile;
        this.players = players;
        this.players.add(this);
        //board.refresh();
        giveCards(7);
        for (Card i : hand){
            System.out.println(i.getPath());
        }
    }

    //Spiel verlassen
    public void leaveGame(){
        players.remove(this); }

    //Karte ziehen
    public void drawCard() throws InterruptedException, RemoteException{
        //Überprüfen ob Spieler am Zug ist
        if (myTurn) {
            //Karte der Hand hinzufügen
        giveCards(1);}
    }

    //Karte geben
    private void giveCards(int amount){
        //Karten ziehen
        for(int i=0; i<amount ;i++) {
            hand.add(drawPile.getFirst());
            drawPile.remove(drawPile.getFirst());
        }
    }

    public void itsMyTurn(){
        players.removeFirst();
        players.addLast(this);
    }

    //Karte spielen
    //Normale Karten
    public void play(Card card){
        boolean draw2 = true;
        boolean draw4 = true;

        switch (card.getcType()){
            case DRAWTWO:
                drawCount += 2;
                draw2 = false;
                break;
            case REVERSE:
                Collections.reverse(players);
                break;
            case WILDFOUR:
                drawCount += 4;
                draw4 = false;
                break;
                //board.drawFrame(); //Farbe wünschen
            case SKIP:
                players.removeFirst();
                players.addLast(this);
                break;
            case WILD:
                //board.drawFrame();
                break;
        }
        System.out.println("Guguck");
        hand.remove(card);
        if (draw2 || draw4){
            giveCards(drawCount);
            drawCount = 0;
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
    public Card getTop(){
        try {
            return playPile.getFirst();
        }
        catch (NoSuchElementException e){
            return new Card(TYPE.UNKNOWN,COLOR.UNKNOWN);
        }
    }
}