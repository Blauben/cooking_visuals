import org.junit.jupiter.api.Test;
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
}
