package bothSides;

import java.util.Arrays;
import java.util.List;

public enum TYPE {
    UNKNOWN("-side",20),ZERO("0",0),ONE("1",1),TWO("2",2), THREE("3",3),FOUR("4",4),FIVE("5",5),SIX("6",6),SEVEN("7",7), EIGHT("8",8), NINE("9",9),SKIP("s",20),REVERSE("r",20),DRAWTWO("+",20), WILD("w",50), WILDFOUR("w4",50);

    private static final TYPE[] excluded = {UNKNOWN,WILDFOUR,WILD};
    private static final TYPE[] multicolored = {WILDFOUR,WILD};
    private String shortcut;
    private int value;

    public static List<TYPE> getExcluded(){
        return Arrays.asList(TYPE.excluded);}
    public static  List<TYPE> getMultiColored(){return Arrays.asList(TYPE.multicolored);}
    TYPE(String shortcut, int value){
        this.shortcut = shortcut;
        this.value = value;
    }

    public String getShortcut() {
        return shortcut;
    }
    public int getValue() {
        return value;
    }
}
