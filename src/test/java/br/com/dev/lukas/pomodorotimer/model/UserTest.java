package br.com.dev.lukas.pomodorotimer.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UserTest {
    @Test
    void testVerifyListSize() {
        // 1. Arrange (Preparar o cenário)
        User user = User.getInstance();
        user.getUserPokemons().clear(); // Garante que a lista começa vazia

        // 2. Act (Executar a funcionalidade)
        user.addPokemon(1);
        user.addPokemon(4);
        user.addPokemon(7);

        int tamanhoObtido = user.verifyListSize();

        // 3. Assert (Verificar se o resultado é o esperado)
        // Esperamos que o tamanho seja 3 porque adicionamos 3 pokémons
        assertEquals(3, tamanhoObtido);
    }
}