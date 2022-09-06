package recipe.extraction;

public class ChefkochAlgorithm implements RecipeExtractAlgorithm {

    private static ChefkochAlgorithm instance;

    private ChefkochAlgorithm() {
    }

    public static ChefkochAlgorithm create() {
        if (instance == null) {
            instance = new ChefkochAlgorithm();
        }
        return instance;
    }

    @Override
    public String[] extractRecipeData(String htmlString) {
        int beginning = htmlString.indexOf("[", htmlString.indexOf("recipeIngredient")); // index Anfang
        htmlString.indexOf("]", beginning); // index Ende
        return new String[]{};
    }
}
