package com.estoque;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menu de console para gerenciar produtos (ponto de entrada da aplicação).
 * Toda a regra de negócio fica no ProdutoService; aqui só lemos o que o
 * usuário digitou, chamamos o service e mostramos o resultado.
 *
 * A entrada (de onde lê), a saída (onde escreve) e o service são recebidos
 * no construtor. No programa de verdade são o teclado e a tela; nos testes
 * (MainTest) são um texto pronto e um buffer, o que permite testar o menu.
 */
public class Main {

    private final Scanner entrada;
    private final PrintStream saida;
    private final ProdutoService service;

    public Main(Scanner entrada, PrintStream saida, ProdutoService service) {
        if (entrada == null) {
            throw new IllegalArgumentException("A entrada não pode ser nula.");
        }
        if (saida == null) {
            throw new IllegalArgumentException("A saída não pode ser nula.");
        }
        if (service == null) {
            throw new IllegalArgumentException("O service não pode ser nulo.");
        }
        this.entrada = entrada;
        this.saida = saida;
        this.service = service;
    }

    /** Ponto de entrada: monta o menu com o teclado, a tela e um repositório novo. */
    public static void main(String[] args) {
        ProdutoService service = new ProdutoService(new ProdutoRepository());
        new Main(new Scanner(System.in), System.out, service).executar();
    }

    /** Mostra o menu em loop até o usuário escolher 0 (ou a entrada acabar). */
    public void executar() {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            try {
                opcao = lerInteiro("Escolha uma opção: ");
                executarOpcao(opcao);
            } catch (ProdutoInvalidoException | ProdutoNaoEncontradoException e) {
                // Erros de regra de negócio: só mostramos a mensagem e voltamos ao menu.
                saida.println("Erro: " + e.getMessage());
            } catch (NoSuchElementException e) {
                // A entrada foi encerrada (Ctrl+Z / fim do texto): sai sem stack trace.
                saida.println();
                saida.println("Entrada encerrada. Saindo...");
                opcao = 0;
            }
        }
    }

    // ------------------------------------------------------------------
    // Menu
    // ------------------------------------------------------------------

    private void exibirMenu() {
        saida.println();
        saida.println("===== GERENCIADOR DE PRODUTOS =====");
        saida.println("1 - Cadastrar produto");
        saida.println("2 - Buscar produto por ID");
        saida.println("3 - Listar produtos");
        saida.println("4 - Atualizar produto");
        saida.println("5 - Remover produto");
        saida.println("6 - Adicionar estoque");
        saida.println("7 - Retirar estoque");
        saida.println("0 - Sair");
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> buscar();
            case 3 -> listar();
            case 4 -> atualizar();
            case 5 -> remover();
            case 6 -> adicionarEstoque();
            case 7 -> retirarEstoque();
            case 0 -> saida.println("Encerrando o sistema. Até logo!");
            default -> saida.println("Opção inválida. Escolha um número do menu.");
        }
    }

    // ------------------------------------------------------------------
    // Opções do menu (CRUD + estoque)
    // ------------------------------------------------------------------

    /** C do CRUD: cadastra um produto novo; o id é gerado pelo repositório. */
    private void cadastrar() {
        String nome = lerTexto("Nome: ");
        BigDecimal preco = lerPreco("Preço (ex.: 19,90): ");
        int quantidade = lerInteiro("Quantidade inicial: ");

        Produto criado = service.criar(nome, preco, quantidade);
        saida.println("Produto cadastrado: " + formatar(criado));
    }

    /** R do CRUD: mostra todos os produtos. */
    private void listar() {
        List<Produto> produtos = service.listar();
        if (produtos.isEmpty()) {
            saida.println("Nenhum produto cadastrado.");
            return;
        }
        produtos.forEach(p -> saida.println(formatar(p)));
    }

    /** R do CRUD: mostra um produto pelo id. */
    private void buscar() {
        Long id = lerId("ID do produto: ");

        Produto encontrado = service.buscar(id);
        saida.println(formatar(encontrado));
    }

    /** U do CRUD: troca nome, preço e quantidade de um produto existente. */
    private void atualizar() {
        Long id = lerId("ID do produto: ");
        // Mostra o produto antes de pedir os novos dados (e já falha se o id não existir).
        saida.println("Atual: " + formatar(service.buscar(id)));

        String nome = lerTexto("Novo nome: ");
        BigDecimal preco = lerPreco("Novo preço (ex.: 19,90): ");
        int quantidade = lerInteiro("Nova quantidade: ");

        Produto atualizado = service.atualizar(id, nome, preco, quantidade);
        saida.println("Produto atualizado: " + formatar(atualizado));
    }

    /** D do CRUD: remove um produto. O service devolve false se o id não existia. */
    private void remover() {
        Long id = lerId("ID do produto: ");

        if (service.remover(id)) {
            saida.println("Produto " + id + " removido.");
        } else {
            saida.println("Nenhum produto com id " + id + " para remover.");
        }
    }

    /** Entrada de estoque: soma unidades ao produto. */
    private void adicionarEstoque() {
        Long id = lerId("ID do produto: ");
        int quantidade = lerInteiro("Quantidade a adicionar: ");

        Produto atualizado = service.adicionarEstoque(id, quantidade);
        saida.println("Estoque atualizado: " + formatar(atualizado));
    }

    /** Saída de estoque: retira unidades, sem deixar o estoque ficar negativo. */
    private void retirarEstoque() {
        Long id = lerId("ID do produto: ");
        int quantidade = lerInteiro("Quantidade a retirar: ");

        Produto atualizado = service.retirarEstoque(id, quantidade);
        saida.println("Estoque atualizado: " + formatar(atualizado));
    }

    // ------------------------------------------------------------------
    // Leitura de dados e formatação
    // ------------------------------------------------------------------

    private String lerTexto(String mensagem) {
        saida.print(mensagem);
        return entrada.nextLine();
    }

    /** Repete a pergunta até o usuário digitar um número inteiro válido. */
    private int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem).trim());
            } catch (NumberFormatException e) {
                saida.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    /** Igual ao lerInteiro, mas devolve Long (usado para os IDs). */
    private Long lerId(String mensagem) {
        while (true) {
            try {
                return Long.parseLong(lerTexto(mensagem).trim());
            } catch (NumberFormatException e) {
                saida.println("ID inválido. Digite um número inteiro.");
            }
        }
    }

    /** Aceita vírgula ou ponto como separador decimal (19,90 ou 19.90). */
    private BigDecimal lerPreco(String mensagem) {
        while (true) {
            try {
                return new BigDecimal(lerTexto(mensagem).trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                saida.println("Preço inválido. Exemplo: 19,90");
            }
        }
    }

    private String formatar(Produto p) {
        return String.format("[%d] %s | R$ %s | estoque: %d",
                p.id(), p.nome(), p.preco().setScale(2, RoundingMode.HALF_UP), p.quantidade());
    }
}
