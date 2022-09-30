package recipe.control;

import recipe.extraction.Context;
import recipe.extraction.ExtractingPolicy;
import recipe.processing.InstructionRoot;
import recipe.processing.Interpreter;

import java.util.List;

public class Controller {

    private static final int INGREDIENTS = 0;
    private static final int INSTRUCTIONS = 1;

    private Controller(String[] args) {
        if (args.length == 0) {
            return;
        }
        run(args[0]);
    }

    public static void main(String[] args) {
        new Controller(args);
    }

    private void run(String url) {
        String[] recipe;
        try {
            recipe = extractRecipe(url);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }
        String[] ingredients = formatIngredients(recipe[INGREDIENTS]);
        List<InstructionRoot> baseNodes = parseRecipe(ingredients, recipe[INSTRUCTIONS]);
        String output = generateJSONOutput(baseNodes);
        System.out.println(output);
    }

    private String[] extractRecipe(String url) throws IllegalArgumentException {
        Context context = new Context();
        context.setAlgorithm(ExtractingPolicy.chooseAlgorithm(url));
        return context.extractRecipe(url);
    }

    private List<InstructionRoot> parseRecipe(String[] ingredients, String instructions) {
        Interpreter interpreter = new Interpreter(ingredients, instructions);
        return interpreter.retrieveResult();
    }

    private String generateJSONOutput(List<InstructionRoot> nodes) {
        if (nodes.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(nodes.get(0).toString());
        for (int i = 1; i < nodes.size(); i++) {
            builder.append("\n");
            builder.append(nodes.get(i).toString());
        }
        return builder.toString();
    }
}
