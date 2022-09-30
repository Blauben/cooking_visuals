package recipe.extraction;

import java.net.MalformedURLException;
import java.net.URL;

public class ExtractingPolicy {

    public static RecipeExtractAlgorithm chooseAlgorithm(String url) throws IllegalArgumentException {
        String website = extractWebsiteFromURL(url);
        return switch (website) {
            case "chefkoch" -> ChefkochAlgorithm.create();
            default -> throw new IllegalArgumentException();
        };
    }

    private static String extractWebsiteFromURL(String url) throws IllegalArgumentException {
        try {
            URL urlWrapper = new URL(url);
            return urlWrapper.getHost().split("\\.")[1];
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }
    }

}
