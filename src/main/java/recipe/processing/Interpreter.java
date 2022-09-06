package recipe.processing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.List;

public class Interpreter {

    private String[] ingredientsTemp;

    public static void evaluateRecipe(String[] ingredients, String recipe) {
        InstructionRoot[] roots = new InstructionRoot[ingredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            roots[i] = new InstructionRoot(ingredients[i]);
        }
        StanfordCoreNLP pipeline = new StanfordCoreNLP("german");
        CoreDocument document = new CoreDocument(recipe);
        pipeline.annotate(document);

        for (CoreSentence sentence : document.sentences()) {
            SemanticGraph dependencyParse = sentence.dependencyParse();

        }
    }

    public static void main(String[] args) {
        evaluateRecipe(new String[]{}, "Die Tomaten vom Strunk befreien und in Spalten schneiden.");
    }

    public int[] getIngredientIndices(CoreSentence sentence, String[] ingredients) {
        ingredientsTemp = ingredients;
        List<CoreLabel> tokens = sentence.tokens();
        for (int i = 0; i < tokens.size(); i++) {
            CoreLabel current = tokens.get(i);
            if (current.tag().equals("NOUN") && isIngredient(current.toString())) {

            }
        }
        return new int[]{};
    }

    private boolean isIngredient(String noun) {
        for (String ingredient : ingredientsTemp) {
            //TODO:continue
        }
        return false;
    }
}
