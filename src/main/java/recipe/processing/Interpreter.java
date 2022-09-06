package recipe.processing;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {

    //TODO: remove main
    public static void main(String[] args) {
        Interpreter.startRecipeParser(new String[]{"Tomate"}, "Die Tomaten vom Strunk befreien und in Spalten schneiden.");
    }

    public static void startRecipeParser(String[] ingredients, String recipe) {
        if (ingredients.length == 0) {
            return;
        }

        List<InstructionRoot> roots = new ArrayList<>(ingredients.length);
        for (String ingredient : ingredients) {
            roots.add(new InstructionRoot(cleanIngredient(ingredient)));
        }
        processDocument(roots, recipe);
    }

    private static String cleanIngredient(String rawIngredient) {
        return rawIngredient.replaceAll("[(),]", "").split(" ")[0];
    }

    private static void processDocument(List<InstructionRoot> roots, String recipe) {
        StanfordCoreNLP pipeline = new StanfordCoreNLP("german");
        CoreDocument document = new CoreDocument(recipe);
        pipeline.annotate(document);

        for (CoreSentence sentence : document.sentences()) {
            processSentence(sentence, roots);
        }
    }

    private static void processSentence(CoreSentence sentence, List<InstructionRoot> roots) {
        RootList root = new RootList(sentence, roots);
        SemanticGraph dependencyParse = sentence.dependencyParse();
        while (root.hasNext()) {
            int sentenceIndex = root.next().sentenceIndex();
            IndexedWord current = dependencyParse.getNodeByIndexSafe(sentenceIndex);
            // dependencyParse.
            //TODO: continue
        }
    }
}
