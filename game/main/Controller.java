package main;

import Exceptions.NotSuitableException;
import test.Card;

import java.util.ArrayList;
import java.util.LinkedList;

public class Controller {
    private LinkedList<Card> drawPile;
    private ArrayList<Card> playPile;
    private LinkedList<Player> players;

    //Spieler tritt dem Spiel bei
    public void join(Player player1){
        //Übergebener Spieler zur SpielerListe hinzufügen
        //Überprüfen ob Spieler schon in der Liste ist (ansonsten ist er womöglich immer mehrfach dran)
    }

    //Spiel starten
    public void start(){
        /*1.Kartendeck erstellen
        * 2. Deck mischen
        * 3. an Spieler austeilen
        * */
    }
    //Karte ziehen
    public Card draw(){
        //erste Karte vom Stapel entfernen und zurückgeben
        //Oberste => drawPile.getFirst();
        return null;
    }

    //Karte spielen
    public void play() throws NotSuitableException{
        //Überprüft ob Karte gelegt werden darf
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
