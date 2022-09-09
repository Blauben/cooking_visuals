package recipe.processing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreSentence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SentenceRootList implements Iterator<RootElement> {
    private static final int similarityLimit = 3;
    private final List<RootElement> indexList;
    private int iteratorPointer = 0;

    public SentenceRootList(CoreSentence sentence, List<InstructionRoot> roots) {
        indexList = new ArrayList<>();
        getIngredientIndices(sentence, roots);
    }

    private void getIngredientIndices(CoreSentence sentence, List<InstructionRoot> roots) {
        List<CoreLabel> tokens = sentence.tokens();
        Optional<InstructionRoot> concreteInstructionRoot = null;
        for (int i = 0; i < tokens.size(); i++) {
            CoreLabel current = tokens.get(i);
            if ((current.tag().equals("NOUN") || current.tag().equals("PROPN")) && (concreteInstructionRoot = getInstructionRoot(current.originalText(), roots)).isPresent()) {
                indexList.add(new RootElement(i, concreteInstructionRoot.get()));
            }
        }
    }

    private Optional<InstructionRoot> getInstructionRoot(String noun, List<InstructionRoot> roots) {
        for (InstructionRoot root : roots) {
            if (root instanceof Ingredient ingredient && SimilarityComputation.similarity(noun, ingredient.getName()) <= similarityLimit) {
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

}
