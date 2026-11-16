
package com.estoque;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    @Test
    void deveCriarProdutoValido() {
        Produto produto = new Produto(
                null, "Teclado", new BigDecimal("150.00"), 10
        );

        assertNull(produto.id());
        assertEquals("Teclado", produto.nome());
        assertEquals(new BigDecimal("150.00"), produto.preco());
        assertEquals(10, produto.quantidade());
    }

    @Test
    void deveRemoverEspacosDoNome() {
        Produto produto = new Produto(
                null, "  Teclado  ", new BigDecimal("150.00"), 10
        );

        assertEquals("Teclado", produto.nome());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void deveRejeitarNomeNuloOuVazio(String nome) {
        assertThrows(ProdutoInvalidoException.class,
                () -> new Produto(null, nome, new BigDecimal("10.00"), 1));
    }

    @Test
    void deveRejeitarPrecoNulo() {
        assertThrows(ProdutoInvalidoException.class,
                () -> new Produto(null, "Teclado", null, 1));
    }

}
