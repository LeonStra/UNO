//Zum Testen des Programms

import cards.Card;
import main.*;

import java.util.LinkedList;

public class InfUnoTest {

    public static void main(String[] args) {
        System.out.println("Start"+1);

        LinkedList<Card> drawPile = new LinkedList<Card>();
        LinkedList<Card> playPile = new LinkedList<Card>();
        LinkedList<Player> players= new LinkedList<Player>();
        Integer drawCount = new Integer(0);

        //Spieler1
        Player player1 = new Player(drawPile,playPile,players,drawCount);
        Player player2 = new Player(drawPile,playPile,players,drawCount);
    }
}
