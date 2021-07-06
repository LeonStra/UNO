package server;

import bothSides.Card;
import bothSides.TYPE;

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

    public boolean contains(Card card){
        return this.stream().anyMatch(c -> c.equals(card));
    }

    public boolean containsType(TYPE type){
        return this.stream().anyMatch(c -> c.equalType(type));
    }
}
