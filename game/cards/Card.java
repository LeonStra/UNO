package cards;

import java.awt.*;

public class Card{

    private int number;
    private Color color;

    public boolean suitable(Card c) {
        //Regel zum Legen
        if (color.equals(c.color) || c.color.equals(COLOR.MULTICOLORED) || number == c.number){
            return true;
        }
        return false;

    }

    public void action(){

    }
}
