package cards;

public enum TYPE {
    ONE("1"),TWO("2"), THREE("3"),FOUR("4"),FIVE("5"),SIX("6"),SEVEN("7"), EIGHT("8"), NINE("9"), WILD("w"), WILDFOUR("w4"),DRAWTWO("+"),SKIP("a"),REVERSE("w");

    private String shortcut;

    TYPE(String shortcut){
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }
}
