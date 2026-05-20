package br.com.dev.lukas.pomodorotimer.model;


import java.util.HashSet;
import java.util.Set;

public class User {
    private static User instance;
    private Set<Integer> userPokemons;

    private User(){
        userPokemons = new HashSet<>();
    }

    public static User getInstance(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    public Set<Integer> getUserPokemons() {
        return this.userPokemons;
    }

    public void setUserPokemons(Set<Integer> userPokemons) {
        this.userPokemons = userPokemons;
    }

    public void addPokemon(int pokemon) {
        userPokemons.add(pokemon);
        System.out.println("Pokemon sorteado: "+pokemon);
        System.out.println("pokemons armazenados: "+userPokemons);
    }

    public boolean verifyPokemon (int pokemon) {
        if (userPokemons.contains(pokemon)) {
            return false;
        }
        return true;
    }




}
