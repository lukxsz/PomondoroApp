package br.com.dev.lukas.pomodorotimer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokeApiServiceTest {

    @Mock
    private TranslationService translatorMock;

    @Test
    @DisplayName("Deve usar a tradução simulada pelo mock")
    void testandoComMock() {
        // 1. Arrange: Ensinamos o mock
        when(translatorMock.translate("Fire", "en", "pt"))
                .thenReturn("Fogo");

        // 2. Act: Executamos o metodo no mock
        String resultado = translatorMock.translate("Fire",
                "en", "pt");

        // 3. Assert: Conferimos o resultado
        assertEquals("Fogo", resultado);
    }
}