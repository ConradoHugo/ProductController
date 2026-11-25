package com.estoque;

import java.math.BigDecimal;
import java.util.List;

/**
 * Camada de regras de negócio dos produtos (CRUD + movimentação de estoque).
 * Valida as entradas, usa o ProdutoRepository para armazenar os dados e
 * lança as exceções do domínio quando alguma regra é violada.
 */
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("O repositório não pode ser nulo.");
        }
        this.repository = repository;
    }

    /** Cadastra um novo produto. O id é gerado automaticamente. */
    public Produto criar(String nome, BigDecimal preco, int quantidade) {
        Produto novo = new Produto(null, nome, preco, quantidade);
        return repository.salvar(novo);
    }

    /** Busca um produto pelo id. */
    public Produto buscar(Long id) {
        validarId(id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(
                        "Produto com id " + id + " não encontrado."));
    }

    /** Lista todos os produtos cadastrados. */
    public List<Produto> listar() {
        return repository.listarTodos();
    }

    /**
     * Atualiza nome, preço e quantidade de um produto existente.
     * O novo Produto é criado (e validado) antes de salvar, então, se os
     * dados forem inválidos, o produto antigo continua intacto.
     */
    public Produto atualizar(Long id, String nome, BigDecimal preco, int quantidade) {
        buscar(id);
        Produto atualizado = new Produto(id, nome, preco, quantidade);
        return repository.salvar(atualizado);
    }

    /** Remove um produto. Retorna true se removeu e false se ele não existia. */
    public boolean remover(Long id) {
        validarId(id);
        return repository.remover(id);
    }

    /** Dá entrada de unidades no estoque de um produto. */
    public Produto adicionarEstoque(Long id, int quantidade) {
        validarQuantidadeMovimentacao(quantidade);
        Produto atual = buscar(id);
        int novaQuantidade;
        try {
            novaQuantidade = Math.addExact(atual.quantidade(), quantidade);
        } catch (ArithmeticException e) {
            throw new ProdutoInvalidoException("A quantidade em estoque ultrapassa o limite permitido.");
        }
        return salvarComQuantidade(atual, novaQuantidade);
    }

    /** Dá saída de unidades do estoque de um produto, sem permitir estoque negativo. */
    public Produto retirarEstoque(Long id, int quantidade) {
        validarQuantidadeMovimentacao(quantidade);
        Produto atual = buscar(id);
        if (quantidade > atual.quantidade()) {
            throw new ProdutoInvalidoException("Estoque insuficiente: disponível "
                    + atual.quantidade() + ", solicitado " + quantidade + ".");
        }
        return salvarComQuantidade(atual, atual.quantidade() - quantidade);
    }

    private Produto salvarComQuantidade(Produto produto, int novaQuantidade) {
        Produto atualizado = new Produto(produto.id(), produto.nome(), produto.preco(), novaQuantidade);
        return repository.salvar(atualizado);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ProdutoInvalidoException("O id deve ser informado e maior que zero.");
        }
    }

    private void validarQuantidadeMovimentacao(int quantidade) {
        if (quantidade <= 0) {
            throw new ProdutoInvalidoException("A quantidade da movimentação deve ser maior que zero.");
        }
    }
}
