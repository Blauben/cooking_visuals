package recipe.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InstructionRoot {
    private final Optional<String> name;
    private List<Instruction> instructions;
    private String[] mergeWith;

    public InstructionRoot(String name) {
        this.name = Optional.of(name);
        setup();
    }

    public InstructionRoot() {
        this.name = Optional.empty();
        setup();
    }

    private void setup() {
        instructions = new ArrayList<>();
        mergeWith = new String[]{};
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void setMergeWith(String[] merge) {
        mergeWith = merge;
    }

    @Override
    public String toString() {
        return name.orElse("merge");
    }
}
