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



}
