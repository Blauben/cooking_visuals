package recipe.processing;

import java.util.ArrayList;
import java.util.List;

public class InstructionRoot {
    private final List<Instruction> instructions;
    private final InstructionRoot mergeSuccessor;
    private final String name;

    public InstructionRoot(String name) {
        instructions = new ArrayList<>();
        mergeSuccessor = null;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public Instruction getInstructionReversed(int index) {
        //TODO: continue
        return new Instruction("");
    }

    @Override
    public String toString() {
        return name + ": " + instructions;
    }
}
