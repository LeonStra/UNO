package cards;

public enum COLOR {
    UNKNOWN("back"),BLUE("b"),RED("r"),YELLOW("y"),GREEN("g"), MULTICOLORED("");

    private final String shortcut;

    COLOR(String shortcut){
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }
}
