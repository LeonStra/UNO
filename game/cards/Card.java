package cards;

public class Card{
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
        return color.equals(c.color) || c.color.equals(COLOR.MULTICOLORED) || type.equals(c.cType)?true:false;
    }

    //Pfad der Karte
    public String getPath(){
        return path+ color.getShortcut()+cType.getShortcut()+type;
    }
}
