package recipe.extraction;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;

public class Fetcher {

    public static String collectHTML(URI uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> response = null;
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }


}