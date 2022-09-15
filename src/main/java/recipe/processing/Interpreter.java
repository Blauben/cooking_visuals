package recipe.processing;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.ConstituentFactory;
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory;
import edu.stanford.nlp.trees.SimpleConstituentFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {

    private static List<InstructionRoot> roots = new ArrayList<>();
    private static StanfordCoreNLP pipeline;

    public static void main(String[] args) {
        Interpreter.startRecipeParser(new String[]{"Äpfel"}, "Die Äpfel ungeschält achteln, entkernen und in Scheibchen schneiden.");
        System.out.println(roots);
        Interpreter.startRecipeParser(new String[]{"Chillischotenpflanze"}, "Chilli waschen und schneiden.");
        System.out.println(roots);
        Interpreter.startRecipeParser(new String[]{"Zwiebeln", "Knoblauch"}, "Zwiebeln und Knoblauch schneiden und mit Oel vermischen.");
        System.out.println(roots);
    }

    public static void startRecipeParser(String[] ingredients, String recipe) {
        if (ingredients.length == 0) {
            return;
        }

        roots = new ArrayList<>(ingredients.length);
        recipe = replaceUmlaute(recipe);
        for (String ingredient : ingredients) {
            ingredient = tailorIngredientToRecipe(cleanIngredient(ingredient), recipe);
            roots.add(new InstructionRoot(ingredient) {
            });
        }
        processDocument(recipe);
    }

    private static String tailorIngredientToRecipe(String ingredient, String recipe) {
        if (recipe.contains(ingredient)) {
            return ingredient;
        }
        String sub;
        for (int i = ingredient.length() - 1; i > 0; i--) {
            sub = ingredient.substring(0, i);
            if (recipe.contains(sub)) {
                return sub;
            }
        }
        for (int i = 1; i < ingredient.length() - 2; i++) {
            sub = ingredient.substring(i);
            if (recipe.contains(sub)) {
                return sub;
            }
        }
        return ingredient;
    }

    private static String replaceUmlaute(String output) {
        String[][] UMLAUT_REPLACEMENTS = {{"Ä", "Ae"}, {"Ü", "Ue"}, {"Ö", "Oe"}, {"ä", "ae"}, {"ü", "ue"}, {"ö", "oe"}, {"ß", "ss"}};
        String result = output;
        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
        }
        return result;
    }

    private static String cleanIngredient(String rawIngredient) {
        return replaceUmlaute(rawIngredient.replaceAll("[(),]", "").split(" ")[0]);
    }

    private static void processDocument(String recipe) {
        initPipeline();
        CoreDocument document = new CoreDocument(recipe);
        pipeline.annotate(document);

        for (CoreSentence sentence : document.sentences()) {
            processSentence(sentence);
        }
        mergeIngredientBranches();
    }

    private static void initPipeline() {
        if (pipeline == null) {
            pipeline = new StanfordCoreNLP("german");
        }
    }

    private static void mergeIngredientBranches() {
        InstructionRoot core = roots.get(0);
        for (int i = 1; i < roots.size(); i++) {
            if (roots.get(i).getInstructions().size() > core.getInstructions().size()) {
                core = roots.get(i);
            }
        }
    }

    private static void processSentence(CoreSentence sentence) {
        SentenceRootList rootContainer = new SentenceRootList(sentence, roots);
        if (rootContainer.size() == 0) {
            //TODO
        } else {
            while (rootContainer.hasNext()) {
                parseElementaryInstruction(rootContainer.next(), sentence);
            }
        }
    }

    private static void parseElementaryInstruction(RootElement rootElement, CoreSentence sentence) {
        SemanticGraph dependencyParse = sentence.dependencyParse();
        IndexedWord coreWord = dependencyParse.getNodeByIndexSafe(rootElement.sentenceIndex() + 1);
        List<IndexedWord> actions = identifyActions(coreWord, dependencyParse);
        InstructionRoot node = rootElement.instructionRoot();

        for (IndexedWord action : actions) {
            Instruction instr = generateInstructionWithDetails(action, sentence);
            smartAddInstruction(node, instr);
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

    private static Instruction generateInstructionWithDetails(IndexedWord verb, CoreSentence sentence) {
        Set<Constituent> constituents = sentence.constituencyParse().constituents(new LabeledScoredConstituentFactory());
        Instruction instr = new Instruction(verb.originalText());
        Constituent verbPhrase = identifyParentPhrase(constituents, verb.index() - 1);
        Optional<Constituent> predicatePhrase = identifyPredicatePhrase(constituents, verbPhrase, verb.index() - 1);
        if (predicatePhrase.isPresent()) {
            int[] range = new int[]{predicatePhrase.get().start(), predicatePhrase.get().end()};
            String detail = String.join(" ", sentence.tokensAsStrings().subList(range[0], range[1] + 1));
            instr.addDetail(detail);
        }
        return instr;
    }

    private static void smartAddInstruction(InstructionRoot node, Instruction instruction) {
        if (!node.getInstructions().contains(instruction)) {
            node.addInstruction(instruction);
        }
    }

    private static Constituent identifyParentPhrase(Set<Constituent> constituents, int... phraseIndices) {
        int start = 0;
        int end = Integer.MAX_VALUE;
        phraseIndices = Arrays.stream(phraseIndices).sorted().toArray();
        Constituent min = null;
        for (Constituent con : constituents) {
            if ((con.start() < phraseIndices[0] && phraseIndices[phraseIndices.length - 1] <= con.end() || con.start() <= phraseIndices[0] && phraseIndices[phraseIndices.length - 1] < con.end()) && con.end() - con.start() <= end - start) {
                min = con;
                start = min.start();
                end = min.end();
            }
        }
        return min;
    }

    private static Optional<Constituent> identifyPredicatePhrase(Set<Constituent> constituents, Constituent verbPhrase, int verbIndex) {
        int[] range = new int[]{verbPhrase.start(), verbPhrase.end()};
        ConstituentFactory factory = new SimpleConstituentFactory();
        if (range[1] - range[0] == 1 && verbIndex == range[0]) {
            return Optional.of(factory.newConstituent(range[1], range[1]));
        } else if (range[1] - range[0] == 1 && verbIndex == range[1]) {
            return Optional.of(factory.newConstituent(range[0], range[0]));
        }
        for (Constituent con : constituents) {
            if (con.label().toString().equals("PP") && range[0] <= con.start() && con.end() <= range[1] && verbPhrase == identifyParentPhrase(constituents, con.start(), con.end())) {
                return Optional.of(con);
            }
        }
        return Optional.empty();
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
