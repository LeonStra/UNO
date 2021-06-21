package cards;

import main.Controller;
import main.Player;

import java.awt.*;

public class Card{
    private final String path = "media/cards/";
    private final String type = ".jpg";
    private TYPE cType;
    private COLOR color;

    //Regel zum Legen
    public boolean suitable(Card c) {
        return color.equals(c.color) || c.color.equals(COLOR.MULTICOLORED) || type.equals(c.cType)?true:false;
    }

    //Aktion der Karte
    public String getPath(){
        return path+ color.getShortcut()+cType.getShortcut()+type;
    }
}
