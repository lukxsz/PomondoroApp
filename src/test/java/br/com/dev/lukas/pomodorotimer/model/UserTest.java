package br.com.dev.lukas.pomodorotimer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UserTest {
    private User user;

    @BeforeEach
    void setUp(){
        user = User.getInstance();
        user.getUserPokemons().clear();
    }

    @Test
    @DisplayName("Deve retornar true quando o pokemon nao estiver na coleção")
    void deveRetornarTrueQuandoPokemonNaoExiste() {

        int idPokemonNovo = 25;
        boolean podeAdicionar = user.verifyPokemon(idPokemonNovo);

        assertTrue(podeAdicionar ,
                "Deveria retornar true para" +
                        " um Pokémon que o usuário ainda não possui");

    }

    @Test
    @DisplayName("Deve retornar false quando o Pokémon JÁ estiver na coleção")
    void deveRetornarFalseQuandoPokemonJaExiste() {
        int idPokemonExistente = 25;
        user.addPokemon(idPokemonExistente);

        boolean podeAdicionar = user.verifyPokemon(idPokemonExistente);

        assertFalse(podeAdicionar , "Deveria retornar false quando repetido");
    }

    @Test
    void testVerifyListSize() {
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