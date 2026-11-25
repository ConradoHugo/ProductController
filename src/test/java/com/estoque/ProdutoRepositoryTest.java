package com.estoque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoRepositoryTest {

    private ProdutoRepository repository;

    @BeforeEach
    void configurar() {
        repository = new ProdutoRepository();
    }

    private Produto novoProduto(String nome) {
        return new Produto(null, nome, new BigDecimal("10.00"), 1);
    }

    // ----- salvar -----

    @Test
    void deveGerarIdsSequenciaisAoSalvar() {
        assertEquals(1L, repository.salvar(novoProduto("A")).id());
        assertEquals(2L, repository.salvar(novoProduto("B")).id());
        assertEquals(3L, repository.salvar(novoProduto("C")).id());
    }

    @Test
    void salvarProdutoComIdExistenteSubstituiSemGerarNovoId() {
        Produto salvo = repository.salvar(novoProduto("A"));
        Produto alterado = new Produto(salvo.id(), "B", BigDecimal.ONE, 5);

        Produto resultado = repository.salvar(alterado);

        assertEquals(salvo.id(), resultado.id());
        assertEquals(1, repository.listarTodos().size());
        assertEquals("B", repository.buscarPorId(salvo.id()).orElseThrow().nome());
    }

    @Test
    void salvarProdutoComIdInformadoContinuaASequenciaAPartirDele() {
        repository.salvar(new Produto(10L, "A", BigDecimal.ONE, 1));

        assertEquals(11L, repository.salvar(novoProduto("B")).id());
    }

    @Test
    void deveRejeitarSalvarProdutoNulo() {
        assertThrows(IllegalArgumentException.class, () -> repository.salvar(null));
    }

    // ----- buscarPorId -----

    @Test
    void deveBuscarProdutoExistente() {
        Produto salvo = repository.salvar(novoProduto("A"));

        assertEquals(salvo, repository.buscarPorId(salvo.id()).orElseThrow());
    }

    @Test
    void buscarProdutoInexistenteRetornaVazio() {
        assertTrue(repository.buscarPorId(99L).isEmpty());
    }

    @Test
    void buscarComIdNuloRetornaVazio() {
        repository.salvar(novoProduto("A"));

        assertTrue(repository.buscarPorId(null).isEmpty());
    }

    // ----- listarTodos -----

    @Test
    void deveListarProdutosNaOrdemDeCadastro() {
        Produto a = repository.salvar(novoProduto("A"));
        Produto b = repository.salvar(novoProduto("B"));

        assertEquals(List.of(a, b), repository.listarTodos());
    }

    @Test
    void listarSemProdutosRetornaListaVazia() {
        assertTrue(repository.listarTodos().isEmpty());
    }

    @Test
    void listaRetornadaNaoPodeSerAlterada() {
        repository.salvar(novoProduto("A"));
        List<Produto> lista = repository.listarTodos();

        assertThrows(UnsupportedOperationException.class, () -> lista.add(novoProduto("B")));
        assertEquals(1, repository.listarTodos().size());
    }

    // ----- remover -----

    @Test
    void deveRemoverProdutoExistente() {
        Produto salvo = repository.salvar(novoProduto("A"));

        assertTrue(repository.remover(salvo.id()));
        assertTrue(repository.listarTodos().isEmpty());
    }

    @Test
    void removerProdutoInexistenteRetornaFalso() {
        assertFalse(repository.remover(99L));
    }

    @Test
    void idDeProdutoRemovidoNaoEhReutilizado() {
        repository.salvar(novoProduto("A"));
        Produto b = repository.salvar(novoProduto("B"));
        repository.remover(b.id());

        assertEquals(3L, repository.salvar(novoProduto("C")).id());
    }

    // ----- existe -----

    @Test
    void existeRetornaVerdadeiroParaProdutoSalvo() {
        Produto salvo = repository.salvar(novoProduto("A"));

        assertTrue(repository.existe(salvo.id()));
    }

    @Test
    void existeRetornaFalsoParaIdInexistente() {
        assertFalse(repository.existe(99L));
        assertFalse(repository.existe(null));
    }

    @Test
    void existeRetornaFalsoAposRemover() {
        Produto salvo = repository.salvar(novoProduto("A"));
        repository.remover(salvo.id());

        assertFalse(repository.existe(salvo.id()));
    }
}
