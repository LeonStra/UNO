package main;

import cards.*;
import view.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;


public class Player{
    private boolean myTurn;
    private Integer drawCount;
    private ArrayList<Card> hand;
    private BoardFrame board;
    private LinkedList<Card> drawPile;
    private LinkedList<Card> playPile;
    private LinkedList<Player> players;

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
    }

    //Spiel verlassen
    public void leaveGame(){

    }

    //Karte ziehen
    public void drawCard(){
        //Überprüfen ob Spieler am Zug ist
        //Karte der Hand hinzufügen
    }

    //Karte geben
    private void giveCard(int amount){
        //Karten ziehen
    }

    /* Sollte unnötig sein
    private boolean check(){
        //Überprüfung ob Spieler die Karte überhaupt hat und legen kann
        return true;
    }*/

    //Karte spielen
    //Normale Karten
    public void play(Card card){

    }

    /*
    //Zwei ziehen
    public void play(DrawTwo card){

    }

    //Seitenwechsel
    public void play(Reverse card){

    }

    //Aussetzen
    public void play(Skip card){

    }

    //Wunschkarte
    public void play(Wild card){

    }

    //4+
    public void play(WildDrawFour card){

    }
    */

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