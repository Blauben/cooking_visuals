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
    }

    @Test
    void testSingleIngredientRecipe() {
        Interpreter.startRecipeParser(new String[]{"Tomaten"}, "Die Tomaten vom Strunk befreien und in Spalten schneiden.");
        assertEquals("[Tomaten: [befreien([von dem Strunk]), schneiden([in Spalten])]]", Interpreter.getRoots().toString());
    }
}
