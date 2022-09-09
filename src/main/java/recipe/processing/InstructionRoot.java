package recipe.processing;

import java.util.ArrayList;
import java.util.List;

public abstract class InstructionRoot {
    private final List<Instruction> instructions;

    public InstructionRoot() {
        instructions = new ArrayList<>();
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        return instructions.toString();
    }
}
