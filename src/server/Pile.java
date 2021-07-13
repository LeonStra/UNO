package server;

import bothSides.Card;

import java.util.LinkedList;

public class Pile extends LinkedList<Card> {

    public boolean contains(Card card) {
        return this.stream().anyMatch(c -> c.equals(card));
    }

    public boolean remove(Card card) {
        for (Card c : this){
            if (c.equals(card)){
                return super.remove(c);
            }
        }
        return false;
    }
}
