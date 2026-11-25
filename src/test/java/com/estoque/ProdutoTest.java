
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

    @Test
    void deveRejeitarPrecoNegativo() {
        assertThrows(ProdutoInvalidoException.class,
                () -> new Produto(null, "Teclado", new BigDecimal("-0.01"), 1));
    }

    @Test
    void deveAceitarPrecoZero() {
        Produto produto = new Produto(null, "Brinde", BigDecimal.ZERO, 1);

        assertEquals(BigDecimal.ZERO, produto.preco());
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(ProdutoInvalidoException.class,
                () -> new Produto(null, "Teclado", new BigDecimal("10.00"), -1));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -10})
    void deveRejeitarIdMenorOuIgualAZero(Long id) {
        assertThrows(ProdutoInvalidoException.class,
                () -> new Produto(id, "Teclado", new BigDecimal("10.00"), 1));
    }

    @Test
    void deveCriarCopiaComNovoId() {
        Produto produto = new Produto(
                null, "Teclado", new BigDecimal("10.00"), 1
        );

        Produto produtoComId = produto.comId(7L);

        assertEquals(7L, produtoComId.id());
        assertEquals(produto.nome(), produtoComId.nome());
    }

    @Test
    void deveAceitarQuantidadeZero() {
        Produto produto = new Produto(null, "Teclado", new BigDecimal("10.00"), 0);

        assertEquals(0, produto.quantidade());
    }

    @Test
    void deveAceitarIdIgualAUm() {
        Produto produto = new Produto(1L, "Teclado", new BigDecimal("10.00"), 1);

        assertEquals(1L, produto.id());
    }

    @Test
    void deveManterPrecoEQuantidadeAoCriarCopiaComNovoId() {
        Produto produto = new Produto(
                null, "Teclado", new BigDecimal("10.00"), 4
        );

        Produto produtoComId = produto.comId(7L);

        assertEquals(produto.preco(), produtoComId.preco());
        assertEquals(produto.quantidade(), produtoComId.quantidade());
    }

    @Test
    void deveRejeitarNovoIdInvalidoAoCriarCopia() {
        Produto produto = new Produto(
                null, "Teclado", new BigDecimal("10.00"), 1
        );

        assertThrows(ProdutoInvalidoException.class, () -> produto.comId(0L));
    }

}
