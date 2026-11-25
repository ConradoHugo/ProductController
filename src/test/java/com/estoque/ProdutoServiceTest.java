package com.estoque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProdutoServiceTest {

    private ProdutoService service;

    @BeforeEach void configurar() { service = new ProdutoService(new ProdutoRepository()); }

    @Test void deveGerarIdsAutomaticos() {

        assertEquals(1L,service.criar("A",BigDecimal.ONE,1).id());
        assertEquals(2L,service.criar("B",BigDecimal.ONE,1).id());

    }

    @Test void deveBuscarProdutoExistente() {

        Produto p=service.criar("A",BigDecimal.ONE,1);
        assertEquals(p,service.buscar(p.id()));

    }

    @Test void deveFalharAoBuscarInexistente() {

        assertThrows(ProdutoNaoEncontradoException.class,()->service.buscar(99L));

    }

    @Test void deveRejeitarIdInvalido() {

        assertThrows(ProdutoInvalidoException.class,()->service.buscar(0L));
        assertThrows(ProdutoInvalidoException.class,()->service.buscar(null));
    }

    @Test void deveListarProdutos() {

        service.criar("A",BigDecimal.ONE,1);
        service.criar("B",BigDecimal.ONE,1);
        assertEquals(2,service.listar().size());

    }

    @Test void deveAtualizarProduto() {

        Produto p=service.criar("A",BigDecimal.ONE,1);
        Produto a=service.atualizar(p.id(),"B",new BigDecimal("2"),3);
        assertEquals("B",a.nome());
        assertEquals(3,a.quantidade());

    }

    @Test void atualizacaoInvalidaNaoAlteraProduto() {

        Produto p=service.criar("A",BigDecimal.ONE,1);
        assertThrows(ProdutoInvalidoException.class,()->service.atualizar(p.id(),"",BigDecimal.ONE,2));
        assertEquals(p,service.buscar(p.id()));

    }

    @Test void deveRemoverProduto() {

        Produto p=service.criar("A",BigDecimal.ONE,1);
        assertTrue(service.remover(p.id()));
        assertTrue(service.listar().isEmpty());

    }

    @Test void removerInexistenteRetornaFalso() {

        assertFalse(service.remover(99L));

    }

    @Test void deveAdicionarEstoque() {

        Produto p=service.criar("A",BigDecimal.ONE,5);
        assertEquals(8,service.adicionarEstoque(p.id(),3).quantidade());

    }

    @Test void deveRetirarEstoque() {

        Produto p=service.criar("A",BigDecimal.ONE,5);
        assertEquals(3,service.retirarEstoque(p.id(),2).quantidade());

    }

    @Test void naoPermiteEstoqueInsuficiente() {

        Produto p=service.criar("A",BigDecimal.ONE,5);
        assertThrows(ProdutoInvalidoException.class,()->service.retirarEstoque(p.id(),6));
        assertEquals(5,service.buscar(p.id()).quantidade());

    }

    @Test void rejeitaMovimentacaoNaoPositiva() {

        Produto p=service.criar("A",BigDecimal.ONE,5);
        assertThrows(ProdutoInvalidoException.class,()->service.adicionarEstoque(p.id(),0));
        assertThrows(ProdutoInvalidoException.class,()->service.retirarEstoque(p.id(),-1));

    }

    @Test void rejeitaRepositoryNulo() {

        assertThrows(IllegalArgumentException.class,()->new ProdutoService(null));

    }

    // ---------------------------------------------------------------
    // Testes complementares: normal, limite e erro para cada método
    // ---------------------------------------------------------------

    // ----- construtor -----

    @Test void serviceNovoComecaSemProdutos() {

        ProdutoService novo = new ProdutoService(new ProdutoRepository());
        assertTrue(novo.listar().isEmpty());

    }

    @Test void servicesComMesmoRepositoryCompartilhamDados() {

        ProdutoRepository repo = new ProdutoRepository();
        ProdutoService primeiro = new ProdutoService(repo);
        ProdutoService segundo = new ProdutoService(repo);
        Produto p = primeiro.criar("A",BigDecimal.ONE,1);
        assertEquals(p,segundo.buscar(p.id()));

    }

    // ----- criar -----

    @Test void criarComDadosInvalidosLancaExcecaoENaoSalva() {

        assertThrows(ProdutoInvalidoException.class,()->service.criar("",BigDecimal.ONE,1));
        assertThrows(ProdutoInvalidoException.class,()->service.criar("A",new BigDecimal("-1"),1));
        assertThrows(ProdutoInvalidoException.class,()->service.criar("A",BigDecimal.ONE,-1));
        assertTrue(service.listar().isEmpty());

    }

    @Test void criarAceitaPrecoEQuantidadeZero() {

        Produto p = service.criar("Brinde",BigDecimal.ZERO,0);
        assertEquals(BigDecimal.ZERO,p.preco());
        assertEquals(0,p.quantidade());

    }

    @Test void criarInvalidoNaoConsomeId() {

        assertThrows(ProdutoInvalidoException.class,()->service.criar(null,BigDecimal.ONE,1));
        assertEquals(1L,service.criar("A",BigDecimal.ONE,1).id());

    }

    // ----- listar -----

    @Test void listarSemProdutosRetornaListaVazia() {

        assertTrue(service.listar().isEmpty());

    }

    @Test void listarAposRemoverNaoMostraProdutoRemovido() {

        Produto a = service.criar("A",BigDecimal.ONE,1);
        Produto b = service.criar("B",BigDecimal.ONE,1);
        service.remover(a.id());
        assertEquals(1,service.listar().size());
        assertEquals(b,service.listar().get(0));

    }

    @Test void listarRetornaListaQueNaoPodeSerAlterada() {

        service.criar("A",BigDecimal.ONE,1);
        assertThrows(UnsupportedOperationException.class,()->service.listar().clear());
        assertEquals(1,service.listar().size());

    }

    // ----- atualizar -----

    @Test void atualizarProdutoInexistenteLancaExcecao() {

        assertThrows(ProdutoNaoEncontradoException.class,()->service.atualizar(99L,"B",BigDecimal.ONE,1));

    }

    @Test void atualizarComIdInvalidoLancaExcecao() {

        assertThrows(ProdutoInvalidoException.class,()->service.atualizar(0L,"B",BigDecimal.ONE,1));
        assertThrows(ProdutoInvalidoException.class,()->service.atualizar(null,"B",BigDecimal.ONE,1));

    }

    @Test void atualizarMantemIdEQuantidadeDoProduto() {

        Produto p = service.criar("A",BigDecimal.ONE,1);
        service.criar("B",BigDecimal.ONE,1);
        Produto a = service.atualizar(p.id(),"C",BigDecimal.ZERO,0);
        assertEquals(p.id(),a.id());
        assertEquals(2,service.listar().size());
        assertEquals(a,service.buscar(p.id()));

    }

    // ----- remover -----

    @Test void removerDuasVezesRetornaFalsoNaSegunda() {

        Produto p = service.criar("A",BigDecimal.ONE,1);
        assertTrue(service.remover(p.id()));
        assertFalse(service.remover(p.id()));

    }

    @Test void removerComIdInvalidoLancaExcecao() {

        assertThrows(ProdutoInvalidoException.class,()->service.remover(0L));
        assertThrows(ProdutoInvalidoException.class,()->service.remover(null));

    }

    @Test void removerProdutoNaoPodeMaisSerBuscado() {

        Produto p = service.criar("A",BigDecimal.ONE,1);
        service.remover(p.id());
        assertThrows(ProdutoNaoEncontradoException.class,()->service.buscar(p.id()));

    }

    // ----- adicionarEstoque -----

    @Test void adicionarUmaUnidadeAoEstoque() {

        Produto p = service.criar("A",BigDecimal.ONE,0);
        assertEquals(1,service.adicionarEstoque(p.id(),1).quantidade());
        assertEquals(1,service.buscar(p.id()).quantidade());

    }

    @Test void adicionarEstoqueEmProdutoInexistenteLancaExcecao() {

        assertThrows(ProdutoNaoEncontradoException.class,()->service.adicionarEstoque(99L,1));

    }

    @Test void adicionarEstoqueAcimaDoLimiteLancaExcecaoENaoAltera() {

        Produto p = service.criar("A",BigDecimal.ONE,Integer.MAX_VALUE);
        assertThrows(ProdutoInvalidoException.class,()->service.adicionarEstoque(p.id(),1));
        assertEquals(Integer.MAX_VALUE,service.buscar(p.id()).quantidade());

    }

    // ----- retirarEstoque -----

    @Test void retirarTodoEstoqueDeixaQuantidadeZero() {

        Produto p = service.criar("A",BigDecimal.ONE,5);
        assertEquals(0,service.retirarEstoque(p.id(),5).quantidade());
        assertEquals(0,service.buscar(p.id()).quantidade());

    }

    @Test void retirarEstoqueDeProdutoInexistenteLancaExcecao() {

        assertThrows(ProdutoNaoEncontradoException.class,()->service.retirarEstoque(99L,1));

    }



}
