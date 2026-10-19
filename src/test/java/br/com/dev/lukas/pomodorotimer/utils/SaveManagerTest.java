package br.com.dev.lukas.pomodorotimer.utils;

import br.com.dev.lukas.pomodorotimer.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {
    @TempDir
    Path tempFolder;

    private User user;
    private File testeFolder;

    @BeforeEach
    void setUp(){
        user = User.getInstance();
        user.getUserPokemons().clear();
        testeFolder = tempFolder.toFile();
    }
    @Test
    @DisplayName("Deve salvar os dados do usuario e carregar com sucesso")
    void salvarECarregarDados(){
        user.addPokemon(25);
        user.addPokemon(1);

        SaveManager.saveData(testeFolder);

        File arquivoGerado = new File(testeFolder, "userSave.json");
        assertTrue(arquivoGerado.exists(), "O arquivo userSave.json deveria ter sido criado");

        user.getUserPokemons().clear();
        assertEquals(0, user.verifyListSize());

        SaveManager.loadData(testeFolder);

        assertEquals(2, user.verifyListSize());
        assertFalse(user.verifyPokemon(25), "User deve possuir o pokemon 25");
        assertFalse(user.verifyPokemon(1), "User deve possuir o pokemon 1");
    }
}