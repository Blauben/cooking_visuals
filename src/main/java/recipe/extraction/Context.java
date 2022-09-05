package recipe.extraction;

public class Context {

    public String[] extractRecipe(String url) {
        //TODO: integrate HTMLFetcher
        RecipeExtractAlgorithm algorithm = ExtractingPolicy.chooseAlgorithm(url);
        return algorithm.extractRecipeData(""); //TODO: add rawHTML
    }
}
