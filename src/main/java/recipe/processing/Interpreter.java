package recipe.processing;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Interpreter {

    private static List<InstructionRoot> roots = new ArrayList<>();

    public static void startRecipeParser(String[] ingredients, String recipe) {
        if (ingredients.length == 0) {
            return;
        }

        roots = new ArrayList<>(ingredients.length);
        for (String ingredient : ingredients) {
            roots.add(new Ingredient(cleanIngredient(ingredient)));
        }
        processDocument(recipe);
    }

    private static String cleanIngredient(String rawIngredient) {
        return rawIngredient.replaceAll("[(),]", "").split(" ")[0];
    }

    private static void processDocument(String recipe) {
        StanfordCoreNLP pipeline = new StanfordCoreNLP("german");
        CoreDocument document = new CoreDocument(recipe);
        pipeline.annotate(document);

        for (CoreSentence sentence : document.sentences()) {
            processSentence(sentence);
        }
    }

    private static void processSentence(CoreSentence sentence) {
        RootList rootContainer = new RootList(sentence, roots);
        SemanticGraph dependencyParse = sentence.dependencyParse();
        if (rootContainer.size() == 0) {
        } else if (rootContainer.size() == 1) {
            parseElementaryInstruction(rootContainer.next(), dependencyParse, sentence);
        } else {
            parseMergeInstruction(rootContainer, dependencyParse, sentence);
        }
    }

    private static void parseElementaryInstruction(RootElement rootElement, SemanticGraph dependencyParse, CoreSentence sentence) {
        IndexedWord coreWord = dependencyParse.getNodeByIndexSafe(rootElement.sentenceIndex() + 1);
        Map<String, Integer> actions = identifyActions(coreWord, dependencyParse);
        InstructionRoot node = rootElement.instructionRoot();

        int startIndex = rootElement.sentenceIndex();
        for (Map.Entry<String, Integer> action : actions.entrySet()) {
            Instruction instr = generateInstructionWithDetails(action, startIndex + 1, sentence);
            startIndex = action.getValue();
            node.addInstruction(instr);
        }
    }

    private static Instruction generateInstructionWithDetails(Map.Entry<String, Integer> action, int rootIndex, CoreSentence sentence) {
        Instruction instr = new Instruction(action.getKey());
        if (rootIndex < action.getValue()) {
            List<String> detailTokens = sentence.tokensAsStrings().subList(rootIndex, action.getValue() - 1);
            String detail = detailTokens.stream().reduce("", (str1, str2) -> !str2.equals("und") ? str1 + " " + str2 : str1).trim();
            instr.addDetail(detail);
        }
        return instr;
    }

    private static void parseMergeInstruction(RootList root, SemanticGraph dependencyParse, CoreSentence sentence) {
        while (root.hasNext()) {
            RootElement current = root.next();
            IndexedWord coreWord = dependencyParse.getNodeByIndexSafe(current.sentenceIndex() + 1);
        }
    }

    private static Map<String, Integer> identifyActions(IndexedWord coreWord, SemanticGraph dependencyParse) {
        List<IndexedWord> incoming = dependencyParse.getIncomingEdgesSorted(coreWord).stream().map(edge -> edge.getSource()).collect(Collectors.toList());
        filterVerbs(incoming);
        final List<IndexedWord> verbOutgoing = new ArrayList<>();
        for (IndexedWord verb : incoming) {
            verbOutgoing.addAll(dependencyParse.getOutEdgesSorted(verb).stream().map(edge -> edge.getTarget()).collect(Collectors.toList()));
        }
        filterVerbs(verbOutgoing);
        incoming.addAll(verbOutgoing);
        Map<String, Integer> verbTable = new LinkedHashMap<>(incoming.size());
        incoming.stream().forEach(word -> verbTable.put(word.originalText(), word.index()));
        return verbTable;
    }

    private static void filterVerbs(List<IndexedWord> words) {
        for (int i = 0; i < words.size(); i++) {
            if (!words.get(i).tag().equals("VERB")) {
                words.remove(i--);
            }
        }
    }

    public static List<InstructionRoot> getRoots() {
        return roots;
    }

}
