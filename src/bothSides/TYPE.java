package bothSides;

import java.util.Arrays;
import java.util.List;

public enum TYPE {
    UNKNOWN("-side"),ZERO("0"),ONE("1"),TWO("2"), THREE("3"),FOUR("4"),FIVE("5"),SIX("6"),SEVEN("7"), EIGHT("8"), NINE("9"), WILD("w"), WILDFOUR("w4"),DRAWTWO("+"),SKIP("s"),REVERSE("r");

    private static final TYPE[] excluded = {UNKNOWN,WILDFOUR,WILD};
    private static final TYPE[] multicolored = {WILDFOUR,WILD};
    private String shortcut;

    public static List<TYPE> getExcluded(){
        return Arrays.stream(TYPE.excluded).toList();
    }//asList??
    public static  List<TYPE> getMultiColored(){return Arrays.stream(TYPE.multicolored).toList();}
    TYPE(String shortcut){
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }
}
