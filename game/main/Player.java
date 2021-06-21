package main;

import cards.*;
import view.BoardFrame;

import java.util.ArrayList;


public class Player{
    private ArrayList<Card> hand;
    private BoardFrame board;
    private Controller controller;

    public Player(Controller controller){
        this.hand = new ArrayList<>();
        this.board = new BoardFrame(this);
        this.controller = controller;
        hand.add(new Card());
    }

    public void drawCard(){
        //Karte der Hand hinzufügen
    }

    private boolean check(){
        //Überprüfung ob Spieler die Karte überhaupt hat
        return true;
    }

    /***/
    //Alle Kartenaktionen
    //Bitte nicht weiterarbeiten!!
    //Ich will erst rausfinden ob es eine bessere Methode als Überladen
    /***/
    //Normale Karten
    public void play(Card card){

    }

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

    public ArrayList<Card> getHand() {
        return hand;
    }
}