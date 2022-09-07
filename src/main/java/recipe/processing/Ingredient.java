package recipe.processing;

public class Ingredient extends InstructionRoot {
    private final String name;

    public Ingredient(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + super.toString();
    }
}
