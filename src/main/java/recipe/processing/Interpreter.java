package recipe.processing;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Interpreter {

    private static List<InstructionRoot> roots = new ArrayList<>();

    public static void main(String[] args) {
        Interpreter.startRecipeParser(new String[]{"Tomaten"}, "Die Tomaten von der Pflanze befreien und in Spalten schneiden.");
        System.out.println(roots);
    }

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
        if (rootContainer.size() == 0) {
        } else if (rootContainer.size() == 1) {
            parseElementaryInstruction(rootContainer.next(), sentence);
        } else {
            parseMergeInstruction(rootContainer, sentence);
        }
    }

    private static void parseElementaryInstruction(RootElement rootElement, CoreSentence sentence) {
        SemanticGraph dependencyParse = sentence.dependencyParse();
        IndexedWord coreWord = dependencyParse.getNodeByIndexSafe(rootElement.sentenceIndex() + 1);
        List<IndexedWord> actions = identifyActions(coreWord, dependencyParse);
        InstructionRoot node = rootElement.instructionRoot();

        for (IndexedWord action : actions) {
            Instruction instr = generateInstructionWithDetails(action, sentence);
            node.addInstruction(instr);
        }
    }

    private static Instruction generateInstructionWithDetails(IndexedWord verb, CoreSentence sentence) {
        Set<Constituent> constituents = sentence.constituencyParse().constituents(new LabeledScoredConstituentFactory());
        Instruction instr = new Instruction(verb.originalText());
        Optional<Constituent> verbPhrase = identifyVerbPhrase(constituents, verb.index());
        Optional<Constituent> predicatePhrase = identifyPredicatePhrase(constituents, verbPhrase);
        if (predicatePhrase.isPresent()) {
            int[] range = new int[]{predicatePhrase.get().start(), predicatePhrase.get().end()};
            String detail = String.join(" ", sentence.tokensAsStrings().subList(range[0], range[1] + 1));
            instr.addDetail(detail);
        }
        return instr;
    }

    private static Optional<Constituent> identifyVerbPhrase(Set<Constituent> constituents, int verbIndex) {
        for (Constituent con : constituents) {
            if (con.label().toString().equals("VP") && con.start() <= verbIndex && con.end() <= verbIndex) {
                return Optional.of(con);
            }
        }
        return Optional.empty();
    }

    private static Optional<Constituent> identifyPredicatePhrase(Set<Constituent> constituents, Optional<Constituent> verbPhrase) {
        if (verbPhrase.isEmpty()) {
            return Optional.empty();
        }
        int[] range = new int[]{verbPhrase.get().start(), verbPhrase.get().end()};
        for (Constituent con : constituents) {
            if (con.label().toString().equals("PP") && range[0] <= con.start() && con.end() <= range[1]) {
                return Optional.of(con);
            }
        }
        return Optional.empty();
    }

    private static void parseMergeInstruction(RootList root, CoreSentence sentence) {
        while (root.hasNext()) {
            RootElement current = root.next();
            IndexedWord coreWord = sentence.dependencyParse().getNodeByIndexSafe(current.sentenceIndex() + 1);
        }
    }

    private static List<IndexedWord> identifyActions(IndexedWord coreWord, SemanticGraph dependencyParse) {
        List<IndexedWord> incoming = dependencyParse.getIncomingEdgesSorted(coreWord).stream().map(edge -> edge.getSource()).collect(Collectors.toList());
        filterVerbs(incoming);
        final List<IndexedWord> verbOutgoing = new ArrayList<>();
        for (IndexedWord verb : incoming) {
            verbOutgoing.addAll(dependencyParse.getOutEdgesSorted(verb).stream().map(edge -> edge.getTarget()).collect(Collectors.toList()));
        }
        filterVerbs(verbOutgoing);
        incoming.addAll(verbOutgoing);
        return incoming;
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
