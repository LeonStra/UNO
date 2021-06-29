package main;

import cards.*;
import view.*;

import java.util.*;


public class Player{
    private boolean myTurn;
    private Integer drawCount;
    private ArrayList<Card> hand;
    private BoardFrame board;
    private LinkedList<Card> drawPile;
    private LinkedList<Card> playPile;
    private LinkedList<Player> players;

    //Konstruktor
    public Player(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<Player> players,Integer drawCount){
        this.myTurn = false;
        this.drawCount = drawCount;
        this.hand = new ArrayList<>();
        this.board = new BoardFrame(this);
        this.drawPile = drawPile;
        this.playPile = playPile;
        this.players = players;
        this.players.add(this);
        board.refresh();
        giveCards(7);
    }

    //Spiel verlassen
    public void leaveGame(){
        players.remove(this); }

    //Karte ziehen
    public void drawCard(){
        //Überprüfen ob Spieler am Zug ist
        if (myTurn) {
            //Karte der Hand hinzufügen
        giveCards(1);}
    }

    //Karte geben
    private void giveCards(int amount){
        //Karten ziehen
        for(int i=0; i == amount ;i++) {
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
            case REVERSE:
                Collections.reverse(players);
            case WILDFOUR:
                drawCount += 4;
                draw4 = false;
                board.drawFrame(); //Farbe wünschen
            case SKIP:
                players.removeFirst();
                players.addLast(this);
            case WILD:
                board.drawFrame();
            default:
               System.out.println("Guguck");
            if (draw2 || draw4){
                giveCards(drawCount);
                drawCount = 0;
            }

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