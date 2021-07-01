package server;

import bothSides.Card;

import java.util.ArrayList;

public class Hand extends ArrayList<Card>{

    public boolean remove(Card card) {
        for (Card c : this){
            if (c.equals(card)){
                return super.remove(c);
            }
        }
        return false;
    }
}
