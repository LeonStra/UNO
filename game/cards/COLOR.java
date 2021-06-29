package cards;

import java.util.Arrays;
import java.util.List;

public enum COLOR {
    UNKNOWN("back"),BLUE("b"),RED("r"),YELLOW("y"),GREEN("g"), MULTICOLORED("");

    private static final COLOR[] excluded = {UNKNOWN,MULTICOLORED};
    private  String shortcut;

    public static List<COLOR> getExcluded(){
        return Arrays.stream(COLOR.excluded).toList();
    }

    COLOR(String shortcut){
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }
}
