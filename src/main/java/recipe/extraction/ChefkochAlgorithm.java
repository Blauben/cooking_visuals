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
        String[] recipe = new String[2];
        int begin, end, temp;
        begin = htmlString.indexOf("[", htmlString.indexOf("recipeIngredient")) + 1; // index Anfang
        temp = htmlString.indexOf("]", begin); // index Ende
        end = htmlString.lastIndexOf("\"", temp);
        recipe[0] = htmlString.substring(begin, end).replaceAll("\"", "");

        temp = htmlString.indexOf("recipeInstructions") + "recipeInstructions".length() + 1;
        begin = htmlString.indexOf("\"", temp) + 1;
        end = htmlString.indexOf("\"", begin);
        recipe[1] = htmlString.substring(begin, end);
        return recipe;
    }
}
