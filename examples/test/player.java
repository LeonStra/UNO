package test;

import java.util.ArrayList;

public class player {
    private ArrayList<Card> pile;

    public player(ArrayList<Card> p){
        pile = p;
    }

    public void add(Card c){
        pile.add(c);
    }

    public ArrayList<Card> get(){
        return pile;
    }
}
