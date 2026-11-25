package com.estoque;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositório em memória dos produtos.
 * Guarda os produtos em um Map (id -> produto) e é responsável por
 * gerar os ids automaticamente (1, 2, 3...). Não aplica regras de
 * negócio: isso é papel do ProdutoService.
 */
public class ProdutoRepository {

    private final Map<Long, Produto> produtos = new LinkedHashMap<>();
    private long proximoId = 1;

    /**
     * Salva o produto. Se ele ainda não tiver id, recebe o próximo id
     * disponível; se já tiver, substitui o produto com o mesmo id.
     *
     * @return o produto como ficou armazenado (já com id)
     */
    public Produto salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }
        Produto produtoSalvo = produto.id() == null ? produto.comId(proximoId) : produto;
        produtos.put(produtoSalvo.id(), produtoSalvo);
        proximoId = Math.max(proximoId, produtoSalvo.id() + 1);
        return produtoSalvo;
    }

    /** Busca um produto pelo id. Retorna Optional vazio se não existir. */
    public Optional<Produto> buscarPorId(Long id) {
        return Optional.ofNullable(produtos.get(id));
    }

    /** Lista todos os produtos na ordem em que foram cadastrados. */
    public List<Produto> listarTodos() {
        return List.copyOf(produtos.values());
    }

    /** Remove o produto com o id informado. Retorna true se ele existia. */
    public boolean remover(Long id) {
        return produtos.remove(id) != null;
    }

    /** Indica se existe um produto com o id informado. */
    public boolean existe(Long id) {
        return produtos.containsKey(id);
    }
}
