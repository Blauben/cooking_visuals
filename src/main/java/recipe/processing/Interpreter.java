package recipe.processing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.ConstituentFactory;
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory;
import edu.stanford.nlp.trees.SimpleConstituentFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {

    private static StanfordCoreNLP pipeline;
    private List<InstructionRoot> roots;

    public Interpreter(String recipe) {
        roots = new ArrayList<>();
        startRecipeParser(recipe);
    }

    public void startRecipeParser(String recipe) {
        roots = new ArrayList<>();
        recipe = replaceUmlaute(recipe);
        processDocument(recipe);
    }

    private String replaceUmlaute(String output) {
        String[][] UMLAUT_REPLACEMENTS = {{"Ä", "Ae"}, {"Ü", "Ue"}, {"Ö", "Oe"}, {"ä", "ae"}, {"ü", "ue"}, {"ö", "oe"}, {"ß", "ss"}};
        String result = output;
        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
        }
        return result;
    }

    private void processDocument(String recipe) {
        initPipeline();
        CoreDocument document = new CoreDocument(recipe);
        pipeline.annotate(document);

        for (CoreSentence sentence : document.sentences()) {
            processSentence(sentence);
        }
        //mergeIngredientBranches();
    }

    private void initPipeline() {
        if (pipeline == null) {
            pipeline = new StanfordCoreNLP("german");
        }
    }

    private void processSentence(CoreSentence sentence) {
        List<CoreLabel> verbs = sentence.tokens().stream().filter(label -> label.tag().equals("VERB")).collect(Collectors.toList());
        SemanticGraph dependencyParse = sentence.dependencyParse();
        Iterator<CoreLabel> itr = verbs.iterator();
        while (itr.hasNext()) {
            CoreLabel label = itr.next();
            IndexedWord word = dependencyParse.getNodeByIndex(label.index());
            parseElementaryInstruction(word, sentence);
        }
    }

    private void parseElementaryInstruction(IndexedWord verb, CoreSentence sentence) {
        SemanticGraph dependencyParse = sentence.dependencyParse();
        List<SemanticGraphEdge> targets = dependencyParse.outgoingEdgeList(verb);
        for (SemanticGraphEdge edge : targets) {
            IndexedWord target = edge.getTarget();
            if (target.tag().equals("NOUN")) {
                Instruction instr = generateInstructionWithDetails(verb, sentence);
                addInstruction(target, instr);
            }
        }
    }

    private void addInstruction(IndexedWord noun, Instruction instruction) {
        for (InstructionRoot root : roots) {
            if (root.getName().equals(noun.originalText())) {
                root.addInstruction(instruction);
                return;
            }
        }
        InstructionRoot root = new InstructionRoot(noun.originalText());
        root.addInstruction(instruction);
        roots.add(root);
    }

    private Instruction generateInstructionWithDetails(IndexedWord verb, CoreSentence sentence) {
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

    private Constituent identifyParentPhrase(Set<Constituent> constituents, int... phraseIndices) {
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

    private Optional<Constituent> identifyPredicatePhrase(Set<Constituent> constituents, Constituent verbPhrase, int verbIndex) {
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

    public List<InstructionRoot> getRoots() {
        return roots;
    }

    public List<InstructionRoot> retrieveResult() {
        return getRoots();
    }

}
