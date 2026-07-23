package br.com.dev.lukas.pomodorotimer.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TranslationService {

    private static final String API_URL = "http://localhost:5000/translate";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String translate(String text, String source, String target){
        try{
            JsonObject payload = new JsonObject();
            payload.addProperty("q", text);
            payload.addProperty("source", source);
            payload.addProperty("target", target);
            payload.addProperty("format","text");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200){
                JsonObject jsonResponse = JsonParser.parseString(response.body())
                        .getAsJsonObject();
                return jsonResponse.get("translatedText").getAsString();
            } else {
                System.out.println("Erro na tradução (HTTP " + response.statusCode() + ")");
                return text;
            }

        } catch (Exception e){
            System.out.println("Falha ao traduzir: " + e.getMessage());
            return text;
        }
    }
}
