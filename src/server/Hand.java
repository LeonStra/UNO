package server;

import bothSides.Card;
import bothSides.TYPE;

import java.util.ArrayList;
import java.util.List;

public class Hand extends ArrayList<Card>{

    public Hand(List<Card> subList) {
        for (Card c : subList){
            add(c);
        }
    }

    public Hand(){
        super();
    }

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
