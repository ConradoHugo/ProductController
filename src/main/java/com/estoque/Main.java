package com.estoque;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação: menu de console para gerenciar produtos.
 * Toda a regra de negócio fica no ProdutoService; aqui só lemos o que o
 * usuário digitou, chamamos o service e mostramos o resultado.
 */
public class Main {

    private static final Scanner entrada = new Scanner(System.in);
    private static final ProdutoService service = new ProdutoService(new ProdutoRepository());

    public static void main(String[] args) {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            try {
                opcao = lerInteiro("Escolha uma opção: ");
                executar(opcao);
            } catch (ProdutoInvalidoException | ProdutoNaoEncontradoException e) {
                // Erros de regra de negócio: só mostramos a mensagem e voltamos ao menu.
                System.out.println("Erro: " + e.getMessage());
            } catch (NoSuchElementException e) {
                // A entrada foi encerrada (Ctrl+D / Ctrl+Z): sai sem stack trace.
                System.out.println("\nEntrada encerrada. Saindo...");
                opcao = 0;
            }
        }
    }

    // ------------------------------------------------------------------
    // Menu
    // ------------------------------------------------------------------

    private static void exibirMenu() {
        System.out.println();
        System.out.println("===== GERENCIADOR DE PRODUTOS =====");
        System.out.println("1 - Cadastrar produto");
        System.out.println("2 - Buscar produto por ID");
        System.out.println("3 - Listar produtos");
        System.out.println("4 - Atualizar produto");
        System.out.println("5 - Remover produto");
        System.out.println("6 - Adicionar estoque");
        System.out.println("7 - Retirar estoque");
        System.out.println("0 - Sair");
    }

    private static void executar(int opcao) {
        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> buscar();
            case 3 -> listar();
            case 4 -> atualizar();
            case 5 -> remover();
            case 6 -> adicionarEstoque();
            case 7 -> retirarEstoque();
            case 0 -> System.out.println("Encerrando o sistema. Até logo!");
            default -> System.out.println("Opção inválida. Escolha um número do menu.");
        }
    }

    // ------------------------------------------------------------------
    // Opções do menu (CRUD + estoque)
    // ------------------------------------------------------------------

    /** C do CRUD: cadastra um produto novo; o id é gerado pelo repositório. */
    private static void cadastrar() {
        String nome = lerTexto("Nome: ");
        BigDecimal preco = lerPreco("Preço (ex.: 19,90): ");
        int quantidade = lerInteiro("Quantidade inicial: ");

        Produto criado = service.criar(nome, preco, quantidade);
        System.out.println("Produto cadastrado: " + formatar(criado));
    }

    /** R do CRUD: mostra todos os produtos. */
    private static void listar() {
        List<Produto> produtos = service.listar();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        produtos.forEach(p -> System.out.println(formatar(p)));
    }

    /** R do CRUD: mostra um produto pelo id. */
    private static void buscar() {
        Long id = lerId("ID do produto: ");

        Produto encontrado = service.buscar(id);
        System.out.println(formatar(encontrado));
    }

    /** U do CRUD: troca nome, preço e quantidade de um produto existente. */
    private static void atualizar() {
        Long id = lerId("ID do produto: ");
        // Mostra o produto antes de pedir os novos dados (e já falha se o id não existir).
        System.out.println("Atual: " + formatar(service.buscar(id)));

        String nome = lerTexto("Novo nome: ");
        BigDecimal preco = lerPreco("Novo preço (ex.: 19,90): ");
        int quantidade = lerInteiro("Nova quantidade: ");

        Produto atualizado = service.atualizar(id, nome, preco, quantidade);
        System.out.println("Produto atualizado: " + formatar(atualizado));
    }

    /** D do CRUD: remove um produto. O service devolve false se o id não existia. */
    private static void remover() {
        Long id = lerId("ID do produto: ");

        if (service.remover(id)) {
            System.out.println("Produto " + id + " removido.");
        } else {
            System.out.println("Nenhum produto com id " + id + " para remover.");
        }
    }

    /** Entrada de estoque: soma unidades ao produto. */
    private static void adicionarEstoque() {
        Long id = lerId("ID do produto: ");
        int quantidade = lerInteiro("Quantidade a adicionar: ");

        Produto atualizado = service.adicionarEstoque(id, quantidade);
        System.out.println("Estoque atualizado: " + formatar(atualizado));
    }

    /** Saída de estoque: retira unidades, sem deixar o estoque ficar negativo. */
    private static void retirarEstoque() {
        Long id = lerId("ID do produto: ");
        int quantidade = lerInteiro("Quantidade a retirar: ");

        Produto atualizado = service.retirarEstoque(id, quantidade);
        System.out.println("Estoque atualizado: " + formatar(atualizado));
    }

    // ------------------------------------------------------------------
    // Leitura de dados e formatação
    // ------------------------------------------------------------------

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine();
    }

    /** Repete a pergunta até o usuário digitar um número inteiro válido. */
    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem).trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    /** Igual ao lerInteiro, mas devolve Long (usado para os IDs). */
    private static Long lerId(String mensagem) {
        while (true) {
            try {
                return Long.parseLong(lerTexto(mensagem).trim());
            } catch (NumberFormatException e) {
                System.out.println("ID inválido. Digite um número inteiro.");
            }
        }
    }

    /** Aceita vírgula ou ponto como separador decimal (19,90 ou 19.90). */
    private static BigDecimal lerPreco(String mensagem) {
        while (true) {
            try {
                return new BigDecimal(lerTexto(mensagem).trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Preço inválido. Exemplo: 19,90");
            }
        }
    }

    private static String formatar(Produto p) {
        return String.format("[%d] %s | R$ %s | estoque: %d",
                p.id(), p.nome(), p.preco().setScale(2, RoundingMode.HALF_UP), p.quantidade());
    }
}
