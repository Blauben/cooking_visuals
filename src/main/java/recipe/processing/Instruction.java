package recipe.processing;

import java.util.ArrayList;
import java.util.List;

public class Instruction {
    private final String action;
    private final List<String> details;

    public Instruction(String action) {
        this.action = action;
        details = new ArrayList<>();
    }

    public void addDetail(String detail) {
        details.add(detail);
    }

    public void addDetail(List<String> details) {
        this.details.addAll(details);
    }
}
