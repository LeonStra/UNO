package bothSides;

import java.io.Serializable;

public class Card implements Serializable, Comparable<Card> {
    private final static String path = "media/cards/";
    private final static String type = ".png";
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
    public COLOR getColor(){return color;}

    @Override
    public int compareTo(Card card) {
        //1 = greater / -1 smaller / 0 equal
        if (card.getColor().ordinal() < this.getColor().ordinal()){
            return 1;
        }else if(card.getColor().ordinal() > this.getColor().ordinal()){
            return -1;
        }else if(card.getcType().ordinal() < this.getcType().ordinal()){
            return 1;
        }
        return -1;
    }
}
