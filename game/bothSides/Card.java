package bothSides;

import java.io.Serializable;

public class Card implements Serializable {
    private final static String path = "media/cards/";
    private final static String type = ".jpg";
    private TYPE cType;
    private COLOR color;

    protected Card(){}

    public Card(TYPE t, COLOR c){
        this.cType = t;
        this.color = c;
    }

    //Regel zum Legen
    public boolean suitable(Card c) {
        return equalType(c.cType) || equalColor(c.color) || c.color == COLOR.MULTICOLORED || this.color == COLOR.MULTICOLORED?true:false;
    }

    public boolean equalType(TYPE t){
        return this.cType == t;
    }

    public boolean equalColor(COLOR c){
        return this.color == c;
    }
    public boolean equals(Card c){
        return color.equals(c.color) && cType.equals(c.cType);
    }

    //Pfad der Karte
    public String getPath(){
        return path+ color.getShortcut()+cType.getShortcut()+type;
    }
    public TYPE getcType(){return cType;}
}
