package main;

import Exceptions.NotSuitableException;
import test.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Game {
    private LinkedList<Card> drawPile;
    private ArrayList<Card> playPile;
    private LinkedList<Player> players;

    //Spieler tritt dem Spiel bei
    public void join(Player player1){
        //Übergebener Spieler zur SpielerListe hinzufügen
        //Überprüfen ob spiel schon gestartet hat
        //Überprüfen ob Spieler schon in der Liste ist (ansonsten ist er womöglich immer mehrfach dran)
        /*if(LinkedList<Player> players != player1){
            LinkedList<Player> players = new LinkedList <Player> ();
            players.add (player1);
        }*/
    }

    //Spiel starten
    public void start(){
        //1.Kartendeck erstellen
        LinkedList<Card> drawPile = new LinkedList <Card>();
        //drawPile.add ();
        // 2. Deck mischen
        Collections.shuffle(drawPile);
        // 3. an Spieler austeilen

    }
    //Karte ziehen
    public Card draw(){
        //erste Karte vom Stapel entfernen und zurückgeben drawPile.getFirst());

        //Oberste => drawPile.getFirst();
        return null;
    }

    //Karte spielen
    public void play(Card a) throws NotSuitableException{
        //Überprüft ob Karte gelegt werden darf
        /*if (Card.suitable(a) == false){
            throw new NotSuitableException("Nicht spielbar");
        }*/
        //Wenn nicht wird ein Fehler aufgerufen mit "throw NotSuitableException;"
        //diese Ausnahme muss dann beim Spieler behoben werden
    }

    //Richtungswechsel
    public void reverse(){
        //Liste umdrehen

    }

    //Nächster Spieler ist am Zug
    public void next(){
        //erster Spieler in der Liste wird an die letzte Stelle der Liste gesetzt
    }
}
