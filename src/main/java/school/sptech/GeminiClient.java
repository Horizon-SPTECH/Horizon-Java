package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiClient {

    private static final String API_KEY;

    static {
        API_KEY = System.getenv("KEY_IA");
    }

    public static String getCompletion(String prompt) throws Exception {
        String requestBody = String.format(
                "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                prompt
        );

        HttpClient client = SelfCertificatedServer.getHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na requisição: " + response.statusCode() + " - " + response.body());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

        // Retornando o texto do primeiro candidato
        return apiResponse.getCandidates()[0]
                .getContent()
                .getParts()[0]
                .getText()
                .trim();
    }
}
