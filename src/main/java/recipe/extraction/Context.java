package recipe.extraction;

import java.net.URI;

public class Context {

    private RecipeExtractAlgorithm algorithm;

    public void setAlgorithm(RecipeExtractAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String[] extractRecipe(String url) throws IllegalArgumentException {
        URI uri = URI.create(url);
        String htmlString = Fetcher.collectHTML(uri);
        return algorithm.extractRecipeData(htmlString);
    }
}

