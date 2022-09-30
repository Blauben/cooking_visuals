package recipe.extraction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Fetcher {

    public static String collectHTML(URI uri) {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> response = null;
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error in retrieving html data from website.");
        }
        return response.body();
    }


}