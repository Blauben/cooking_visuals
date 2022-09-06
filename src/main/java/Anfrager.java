import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Anfrager {
    public static void main(String[] args) {
        System.out.println("187");
        try {
            collectHTML(URI.create("https://javabeginners.de/Grundlagen/Bibliothek_einbinden.php"));
        } catch (Exception e) {
        }
    }

    public static String collectHTML(URI url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> response = null;
        URI endPoint = url;
        URI uri = URI.create(endPoint + "?foo=bar&foo2=bar2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Body :" + response.body());
        return response.body();
    }


}