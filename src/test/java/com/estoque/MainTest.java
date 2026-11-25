package com.estoque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes do menu de console.
 * Em vez de digitar no teclado, o teste entrega ao Main um texto pronto
 * (cada linha é uma resposta do "usuário") e guarda tudo o que o Main
 * escreveu, para conferir as mensagens e o estado do service.
 */
class MainTest {

    private ProdutoService service;

    @BeforeEach
    void configurar() {
        service = new ProdutoService(new ProdutoRepository());
    }

    /** Roda o menu com as linhas informadas como entrada e devolve tudo o que foi escrito. */
    private String rodar(String... linhas) {
        String texto = linhas.length == 0 ? "" : String.join("\n", linhas) + "\n";
        Scanner entrada = new Scanner(
                new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        new Main(entrada, saida, service).executar();

        return buffer.toString(StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------
    // Construtor
    // ------------------------------------------------------------------

    @Test
    void construtorRejeitaEntradaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Main(null, System.out, service));
    }

    @Test
    void construtorRejeitaSaidaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Main(new Scanner(""), null, service));
    }

    @Test
    void construtorRejeitaServiceNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Main(new Scanner(""), System.out, null));
    }

    // ------------------------------------------------------------------
    // Menu (executar)
    // ------------------------------------------------------------------

    @Test
    void opcaoZeroEncerraOSistema() {
        String saida = rodar("0");

        assertTrue(saida.contains("GERENCIADOR DE PRODUTOS"));
        assertTrue(saida.contains("Encerrando o sistema. Até logo!"));
    }

    @Test
    void opcaoForaDoMenuMostraAvisoEContinua() {
        String saida = rodar("9", "0");

        assertTrue(saida.contains("Opção inválida. Escolha um número do menu."));
        assertTrue(saida.contains("Encerrando o sistema."));
    }

    @Test
    void textoNoLugarDaOpcaoPedeNumeroDeNovo() {
        String saida = rodar("abc", "0");

        assertTrue(saida.contains("Valor inválido. Digite um número inteiro."));
        assertTrue(saida.contains("Encerrando o sistema."));
    }

    @Test
    void entradaEncerradaSemOpcaoZeroSaiSemQuebrar() {
        String saida = rodar();

        assertTrue(saida.contains("Entrada encerrada. Saindo..."));
    }

    @Test
    void menuTemTodasAsOpcoesEmNoMaximoQuatroLinhas() {
        String saida = rodar("0");

        String menu = saida.substring(saida.indexOf("====="), saida.indexOf("Escolha uma opção"));
        assertTrue(menu.lines().count() <= 4, "menu alto demais esconde o resultado no terminal");
        for (String opcao : new String[]{"1 - Cadastrar", "2 - Buscar", "3 - Listar", "4 - Atualizar",
                "5 - Remover", "6 - Adicionar estoque", "7 - Retirar estoque", "0 - Sair"}) {
            assertTrue(menu.contains(opcao), opcao);
        }
    }

    // ------------------------------------------------------------------
    // 1 - Cadastrar
    // ------------------------------------------------------------------

    @Test
    void cadastrarProdutoValido() {
        String saida = rodar("1", "Caneta", "2,50", "10", "0");

        assertTrue(saida.contains("Produto cadastrado: [1] Caneta | R$ 2.50 | estoque: 10"));
        assertEquals(1, service.listar().size());
    }

    @Test
    void cadastrarComPrecoEQuantidadeZero() {
        String saida = rodar("1", "Brinde", "0", "0", "0");

        assertTrue(saida.contains("Produto cadastrado: [1] Brinde | R$ 0.00 | estoque: 0"));
    }

    @Test
    void cadastrarComNomeVazioMostraErroENaoSalva() {
        String saida = rodar("1", "", "2,50", "10", "0");

        assertTrue(saida.contains("Erro: O nome é obrigatório."));
        assertTrue(service.listar().isEmpty());
    }

    @Test
    void cadastrarComPrecoEmTextoPedeOPrecoDeNovo() {
        String saida = rodar("1", "Caneta", "abc", "2.50", "10", "0");

        assertTrue(saida.contains("Preço inválido. Exemplo: 19,90"));
        assertEquals(new BigDecimal("2.50"), service.buscar(1L).preco());
    }

    // ------------------------------------------------------------------
    // 2 - Buscar
    // ------------------------------------------------------------------

    @Test
    void buscarProdutoExistente() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);

        String saida = rodar("2", "1", "0");

        assertTrue(saida.contains("[1] Caneta | R$ 2.50 | estoque: 10"));
    }

    @Test
    void buscarComIdZeroMostraErro() {
        String saida = rodar("2", "0", "0");

        assertTrue(saida.contains("Erro: O id deve ser informado e maior que zero."));
    }

    @Test
    void buscarProdutoInexistenteMostraErro() {
        String saida = rodar("2", "99", "0");

        assertTrue(saida.contains("Erro: Produto com id 99 não encontrado."));
    }

    @Test
    void buscarComIdEmTextoPedeOIdDeNovo() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);

        String saida = rodar("2", "x", "1", "0");

        assertTrue(saida.contains("ID inválido. Digite um número inteiro."));
        assertTrue(saida.contains("[1] Caneta"));
    }

    // ------------------------------------------------------------------
    // 3 - Listar
    // ------------------------------------------------------------------

    @Test
    void listarSemProdutosAvisaQueEstaVazio() {
        String saida = rodar("3", "0");

        assertTrue(saida.contains("Nenhum produto cadastrado."));
    }

    @Test
    void listarComUmProduto() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);

        String saida = rodar("3", "0");

        assertTrue(saida.contains("[1] Caneta | R$ 2.50 | estoque: 10"));
        assertFalse(saida.contains("Nenhum produto cadastrado."));
    }

    @Test
    void listarMostraTodosOsProdutos() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);
        service.criar("Caderno", new BigDecimal("15"), 5);

        String saida = rodar("3", "0");

        assertTrue(saida.contains("[1] Caneta"));
        assertTrue(saida.contains("[2] Caderno | R$ 15.00 | estoque: 5"));
    }

    @Test
    void listarMostraTituloComAQuantidadeEExplicaOId() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);
        service.criar("Caderno", new BigDecimal("15"), 5);

        String saida = rodar("3", "0");

        assertTrue(saida.contains("Produtos cadastrados: 2 (o ID é o número entre colchetes)"));
    }

    // ------------------------------------------------------------------
    // 4 - Atualizar
    // ------------------------------------------------------------------

    @Test
    void atualizarProdutoExistente() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);

        String saida = rodar("4", "1", "Caneta Azul", "3", "8", "0");

        assertTrue(saida.contains("Atual: [1] Caneta | R$ 2.50 | estoque: 10"));
        assertTrue(saida.contains("Produto atualizado: [1] Caneta Azul | R$ 3.00 | estoque: 8"));
    }

    @Test
    void atualizarProdutoInexistenteNemPedeOsNovosDados() {
        String saida = rodar("4", "99", "0");

        assertTrue(saida.contains("Erro: Produto com id 99 não encontrado."));
        assertFalse(saida.contains("Novo nome"));
    }

    @Test
    void atualizarComNomeVazioMantemOProdutoIgual() {
        Produto original = service.criar("Caneta", new BigDecimal("2.50"), 10);

        String saida = rodar("4", "1", "", "3", "8", "0");

        assertTrue(saida.contains("Erro: O nome é obrigatório."));
        assertEquals(original, service.buscar(1L));
    }

    // ------------------------------------------------------------------
    // 5 - Remover
    // ------------------------------------------------------------------

    @Test
    void removerProdutoExistente() {
        service.criar("Caneta", new BigDecimal("2.50"), 10);

        String saida = rodar("5", "1", "0");

        assertTrue(saida.contains("Produto 1 removido."));
        assertTrue(service.listar().isEmpty());
    }

    @Test
    void removerProdutoInexistenteAvisaSemErro() {
        String saida = rodar("5", "99", "0");

        assertTrue(saida.contains("Nenhum produto com id 99 para remover."));
        assertFalse(saida.contains("Erro:"));
    }

    @Test
    void removerComIdZeroMostraErro() {
        String saida = rodar("5", "0", "0");

        assertTrue(saida.contains("Erro: O id deve ser informado e maior que zero."));
    }

    // ------------------------------------------------------------------
    // 6 - Adicionar estoque
    // ------------------------------------------------------------------

    @Test
    void adicionarEstoque() {
        service.criar("Caneta", new BigDecimal("2.50"), 5);

        String saida = rodar("6", "1", "3", "0");

        assertTrue(saida.contains("Estoque atualizado: [1] Caneta | R$ 2.50 | estoque: 8"));
    }

    @Test
    void adicionarUmaUnidadeAoEstoque() {
        service.criar("Caneta", new BigDecimal("2.50"), 0);

        rodar("6", "1", "1", "0");

        assertEquals(1, service.buscar(1L).quantidade());
    }

    @Test
    void adicionarQuantidadeZeroMostraErro() {
        service.criar("Caneta", new BigDecimal("2.50"), 5);

        String saida = rodar("6", "1", "0", "0");

        assertTrue(saida.contains("Erro: A quantidade da movimentação deve ser maior que zero."));
        assertEquals(5, service.buscar(1L).quantidade());
    }

    // ------------------------------------------------------------------
    // 7 - Retirar estoque
    // ------------------------------------------------------------------

    @Test
    void retirarEstoque() {
        service.criar("Caneta", new BigDecimal("2.50"), 5);

        String saida = rodar("7", "1", "2", "0");

        assertTrue(saida.contains("Estoque atualizado: [1] Caneta | R$ 2.50 | estoque: 3"));
    }

    @Test
    void retirarTodoOEstoqueDeixaZero() {
        service.criar("Caneta", new BigDecimal("2.50"), 5);

        rodar("7", "1", "5", "0");

        assertEquals(0, service.buscar(1L).quantidade());
    }

    @Test
    void retirarMaisDoQueOEstoqueMostraErro() {
        service.criar("Caneta", new BigDecimal("2.50"), 5);

        String saida = rodar("7", "1", "6", "0");

        assertTrue(saida.contains("Erro: Estoque insuficiente: disponível 5, solicitado 6."));
        assertEquals(5, service.buscar(1L).quantidade());
    }

    // ------------------------------------------------------------------
    // main(String[]): o ponto de entrada usa o teclado e a tela de verdade
    // ------------------------------------------------------------------

    /** Troca o teclado e a tela pelo texto informado, roda o main e devolve o que apareceu. */
    private String rodarMain(String texto) {
        InputStream tecladoOriginal = System.in;
        PrintStream telaOriginal = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setIn(tecladoOriginal);
            System.setOut(telaOriginal);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void mainAbreOMenuEEncerraComZero() {
        String saida = rodarMain("0\n");

        assertTrue(saida.contains("GERENCIADOR DE PRODUTOS"));
        assertTrue(saida.contains("Encerrando o sistema."));
    }

    @Test
    void mainCadastraEListaUsandoOProprioRepositorio() {
        String saida = rodarMain("1\nCaneta\n2,50\n10\n3\n0\n");

        assertTrue(saida.contains("Produto cadastrado: [1] Caneta"));
        assertTrue(saida.contains("[1] Caneta | R$ 2.50 | estoque: 10"));
    }

    @Test
    void mainComEntradaVaziaNaoQuebra() {
        String saida = rodarMain("");

        assertTrue(saida.contains("Entrada encerrada. Saindo..."));
    }
}
