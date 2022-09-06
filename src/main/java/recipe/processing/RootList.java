package recipe.processing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class RootList implements Iterator<RootList.RootElement> {
    private static final int similarityLimit = 3;
    private final List<RootElement> indexList;
    private int iteratorPointer = 0;

    public RootList(CoreSentence sentence, List<InstructionRoot> roots) {
        indexList = new ArrayList<>();
        getIngredientIndices(sentence, roots);
    }

    private void getIngredientIndices(CoreSentence sentence, List<InstructionRoot> roots) {
        List<CoreLabel> tokens = sentence.tokens();
        Optional<InstructionRoot> concreteInstructionRoot;
        for (int i = 0; i < tokens.size(); i++) {
            CoreLabel current = tokens.get(i);
            if (current.tag().equals("NOUN") && (concreteInstructionRoot = getInstructionRoot(current.toString(), roots)).isPresent()) {
                indexList.add(new RootElement(i, concreteInstructionRoot.get()));
            }
        }
    }

    private Optional<InstructionRoot> getInstructionRoot(String noun, List<InstructionRoot> roots) {
        noun = noun.substring(0, noun.lastIndexOf("-"));
        for (InstructionRoot root : roots) {
            if (SimilarityComputation.similarity(noun, root.toString()) <= similarityLimit) {
                return Optional.of(root);
            }
        }
        return Optional.empty();
    }

    public int size() {
        return indexList.size();
    }

    @Override
    public boolean hasNext() {
        return iteratorPointer < indexList.size();
    }

    @Override
    public RootElement next() {
        return indexList.get(iteratorPointer++);
    }

    public void resetIterator() {
        iteratorPointer = 0;
    }

    record RootElement(int sentenceIndex, InstructionRoot instructionRoot) {
    }
}
