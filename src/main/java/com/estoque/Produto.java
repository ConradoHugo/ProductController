package com.estoque;

import java.math.BigDecimal;

/**
 * Entidade imutável que representa um produto do estoque.
 * Todas as regras de validação ficam no construtor compacto, então
 * é impossível existir um Produto inválido.
 */
public record Produto(Long id, String nome, BigDecimal preco, int quantidade) {

    public Produto {
        if (id != null && id <= 0) {
            throw new ProdutoInvalidoException("O id deve ser maior que zero.");
        }
        if (nome == null || nome.isBlank()) {
            throw new ProdutoInvalidoException("O nome é obrigatório.");
        }
        if (preco == null || preco.signum() < 0) {
            throw new ProdutoInvalidoException("O preço não pode ser nulo nem negativo.");
        }
        if (quantidade < 0) {
            throw new ProdutoInvalidoException("A quantidade não pode ser negativa.");
        }
        nome = nome.trim();
    }

    /** Devolve uma cópia deste produto com outro id (o record é imutável). */
    public Produto comId(Long novoId) {
        return new Produto(novoId, nome, preco, quantidade);
    }
}
