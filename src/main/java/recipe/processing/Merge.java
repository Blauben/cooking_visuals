package recipe.processing;

import java.util.List;

public class Merge extends InstructionRoot {

    private final List<InstructionRoot> parents;

    public Merge(List<InstructionRoot> parents) {
        super();
        this.parents = parents;
    }

    @Override
    public String toString() {
        return "[Merge]: " + super.toString();
    }
}
