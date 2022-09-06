import org.junit.jupiter.api.Test;
import recipe.processing.SimilarityComputation;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestingClass {

    @Test
    void testLevensthein() {
        int[][] expected = {
                {0, 1, 2, 3, 4, 5, 6},
                {1, 1, 2, 3, 4, 5, 6},
                {2, 2, 1, 2, 3, 4, 5},
                {3, 3, 2, 1, 2, 3, 4},
                {4, 4, 3, 2, 1, 2, 3},
                {5, 5, 4, 3, 2, 2, 3},
                {6, 6, 5, 4, 3, 3, 2},
                {7, 7, 6, 5, 4, 4, 3}
        };
        assertTrue(Arrays.deepEquals(expected, SimilarityComputation.testCalculateTable("kitten", "sitting")));
    }

}
