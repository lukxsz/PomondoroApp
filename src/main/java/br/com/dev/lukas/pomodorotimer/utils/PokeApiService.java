package br.com.dev.lukas.pomodorotimer.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import br.com.dev.lukas.pomodorotimer.model.PokemonDetails;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class PokeApiService {
    public void buscarPagina(int offset, int limite, Consumer<JsonArray> quandoTerminar) {

        String urlApi = "https://pokeapi.co/api/v2/pokemon?limit=" + limite + "&offset=" + offset;

        // Cria a Thread paralela
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlApi)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray resultados = json.getAsJsonArray("results");

                // Avisa quem chamou que os dados estão prontos, entregando a bandeja de resultados!
                quandoTerminar.accept(resultados);

            } catch (Exception e) {
                System.out.println("Erro na PokeAPI: " + e.getMessage());
            }
        }).start();
    }

    public PokemonDetails getPokemonDetails(int id) {
        PokemonDetails pokemonDetails = new PokemonDetails();

        try {
            HttpClient client = HttpClient.newHttpClient();
            //Busca dos tipos:
            String urlApi = "https://pokeapi.co/api/v2/pokemon/" + id;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlApi))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonBase = JsonParser.parseString(response.body()).getAsJsonObject();

            //Nome:
            String rawName = jsonBase.get("name").getAsString();
            pokemonDetails.name = rawName.substring(0, 1).toUpperCase() + rawName.substring(1);

            //Tipos:
            JsonArray typesArray = jsonBase.getAsJsonArray("types");

            pokemonDetails.type1 = typesArray.get(0).getAsJsonObject()
                                                    .getAsJsonObject("type")
                                                    .get("name").getAsString();

            if (typesArray.size() > 1) {
                pokemonDetails.type2 = typesArray.get(1).getAsJsonObject()
                                                        .getAsJsonObject("type")
                                                        .get("name").getAsString();
            }else{
                pokemonDetails.type2 ="";
            }

            String urlSpecies =  "https://pokeapi.co/api/v2/pokemon-species/" + id;
            HttpRequest requestSpecie = HttpRequest.newBuilder().uri(URI.create(urlSpecies)).GET().build();
            HttpResponse<String> responseSpecie = client.send(requestSpecie, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonSpecie = JsonParser.parseString(responseSpecie.body()).getAsJsonObject();

            //Detalhes da Pokedex:
            JsonArray dexEntryArray = jsonSpecie.getAsJsonArray("flavor_text_entries");

            for (int i = 0; i < dexEntryArray.size(); i++) {
                JsonObject dexEntry = dexEntryArray.get(i).getAsJsonObject();
                String idioma = dexEntry.getAsJsonObject("language").get("name").getAsString();

                if(idioma.equals("en")){
                    String textDirty = dexEntry.get("flavor_text").getAsString();
                    pokemonDetails.pokedexEntry = textDirty.replaceAll("\\s+", " ");

                    break;
                }
            }

            return  pokemonDetails;

        }catch (Exception e){
            System.out.println("Erro na PokeAPI: " + e.getMessage());
            return null;
        }
    }
}
