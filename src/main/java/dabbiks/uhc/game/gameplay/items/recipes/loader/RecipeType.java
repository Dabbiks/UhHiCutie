package dabbiks.uhc.game.gameplay.items.recipes.loader;

public enum RecipeType {
    ARMOR("pancerze"),
    WEAPON("bronie"),
    TOOL("narzędzia"),
    USABLE("przedmioty specjalne"),
    CONSUMABLE("jedzenie i dodatki");

    private String name;

    RecipeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
