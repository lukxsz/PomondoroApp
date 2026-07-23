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
    private final TranslationService translator = new TranslationService();

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

            //ID, Altura e Peso:
            pokemonDetails.id = id;
            pokemonDetails.height = jsonBase.get("height").getAsInt() / 10.0;  // API retorna em decímetros
            pokemonDetails.weight = jsonBase.get("weight").getAsInt() / 10.0;  // API retorna em hectogramas

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
                    String cleanText = textDirty.replaceAll("\\s+", " ");
                    pokemonDetails.pokedexEntry = translator.translate(cleanText, "en", "pt");
                    break;
                }
            }

            //Categoria (genera):
            JsonArray generaArray = jsonSpecie.getAsJsonArray("genera");
            for (int i = 0; i < generaArray.size(); i++) {
                JsonObject genera = generaArray.get(i).getAsJsonObject();
                String idiomaGenera = genera.getAsJsonObject("language").get("name").getAsString();
                if (idiomaGenera.equals("en")) {
                    pokemonDetails.category = genera.get("genus").getAsString();
                    break;
                }
            }

            return pokemonDetails;

        }catch (Exception e){
            System.out.println("Erro na PokeAPI: " + e.getMessage());
            return null;
        }
    }
}
