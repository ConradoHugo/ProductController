# ProductController

Aplicação Java 17 para gerenciamento básico de produtos e estoque.

## Requisitos
- JDK 17 ou superior
- Maven 3.8 ou superior

## Executar os testes
```bash
mvn clean test
```

## Executar a aplicação
```bash
mvn compile exec:java
```

## Estrutura
- `Produto`: entidade imutável e validações dos dados.
- `ProdutoRepository`: armazenamento em memória dos produtos.
- `ProdutoService`: operações e regras de negócio, incluindo movimentação de estoque.
- `ProdutoInvalidoException`: erro de validação.
- `ProdutoNaoEncontradoException`: produto solicitado não cadastrado.
- `Main`: ponto de entrada e demonstração do fluxo.

Os dados são mantidos somente em memória e são perdidos ao encerrar a aplicação.
