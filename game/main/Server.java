package main;

import Exceptions.NotSuitableException;
import cards.*;

import java.util.*;

public class Server {
    //"Datenmodell"
    private LinkedList<cards.Card> drawPile = new LinkedList<Card>();
    private LinkedList<cards.Card> playPile = new LinkedList<Card>();
    private LinkedList<Player> players= new LinkedList<Player>();
    private Integer drawCount = 0; //Adapterklasse, damit die Zahl global aktualisiert wird

    public void createDeck(){
        //Typen vorbereiten
        List<TYPE> typeList = Arrays.stream(TYPE.values()).toList();
        typeList.removeAll(TYPE.getExcluded());

        //Farben vorbereiten
        List<COLOR> colorList = Arrays.stream(COLOR.values()).toList();
        colorList.removeAll(COLOR.getExcluded());

        //Deck bilden
        for (int i=0;i<2;i++){
            for(TYPE t : typeList) {
                for (COLOR c : colorList) {
                    if (t == TYPE.ZERO){continue;} //Nur eine Null pro Farbe
                    drawPile.add(new Card(t, c));
                }
            }
        }
        //Wunschkarten
        for (int i=0;i<4;i++){
            drawPile.add(new Card(TYPE.WILD,COLOR.MULTICOLORED));
            drawPile.add(new Card(TYPE.WILDFOUR,COLOR.MULTICOLORED));
        }
        Collections.shuffle(drawPile);
    }
}
