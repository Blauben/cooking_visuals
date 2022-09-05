package recipe.extraction;

import java.io.IOException;
import java.net.URI;

public class Context {

    public String[] extractRecipe(String url) throws IOException, InterruptedException {
        URI uri = URI.create(url);
        String htmlString = Fetcher.collectHTML(uri);
        //TODO: integrate HTMLFetcher
        RecipeExtractAlgorithm algorithm = ExtractingPolicy.chooseAlgorithm(url);
        return algorithm.extractRecipeData(htmlString); //TODO: add rawHTML
    }
}

