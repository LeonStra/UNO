package cards;

public enum COLOR {
    BLUE("b"),RED("r"),YELLOW("y"),GREEN("g"), MULTICOLORED("");

    private final String shortcut;

    COLOR(String shortcut){
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }
}
