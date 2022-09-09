import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import recipe.processing.Interpreter;
import recipe.processing.SimilarityComputation;

import static org.junit.jupiter.api.Assertions.*;

public class TestingClass {

    @Test
    void testSimilarity() {
        assertEquals(3, SimilarityComputation.similarity("kitten", "sitting"));
        assertEquals(3, SimilarityComputation.similarity("sitting", "kitten"));
        assertTrue(3 > SimilarityComputation.similarity("Tomate", "tomaten"));
        assertFalse(3 > SimilarityComputation.similarity("Tomate", "tomate(n)"));
        assertFalse(3 > SimilarityComputation.similarity("Zwiebel", "Tomate"));
    }

    @Test
    @Disabled
    void testSingleIngredientRecipe() {
        Interpreter.main(new String[]{});
    }
}
