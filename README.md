# Sistema de Gerenciamento de Produtos

## Sobre o projeto

Este projeto consiste em um sistema de gerenciamento de produtos desenvolvido em Java, com o objetivo de praticar conceitos de Programação Orientada a Objetos (POO), organização de código, tratamento de exceções e desenvolvimento orientado a testes (TDD).

O sistema permite realizar operações de cadastro, consulta, atualização e remoção de produtos, além de controlar a movimentação do estoque.

## Tecnologias utilizadas

- **Java 17** — linguagem de programação.
- **Maven** — gerenciamento de dependências e construção do projeto.
- **JUnit 5** — criação e execução de testes automatizados.
- **Git e GitHub** — versionamento e armazenamento do código.

## Funcionalidades

- Cadastro de produtos.
- Geração automática de IDs.
- Consulta de produtos por ID.
- Listagem de todos os produtos.
- Atualização de informações dos produtos.
- Remoção de produtos.
- Adição de unidades ao estoque.
- Retirada de unidades do estoque.
- Validação de dados.
- Tratamento de exceções para operações inválidas.

## Estrutura do projeto

```text
Product-Management/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── estoque/
    │               ├── Main.java
    │               ├── Produto.java
    │               ├── ProdutoRepository.java
    │               ├── ProdutoService.java
    │               ├── ProdutoInvalidoException.java
    │               └── ProdutoNaoEncontradoException.java
    └── test/
        └── java/
            └── com/
                └── estoque/
                    ├── ProdutoTest.java
                    └── ProdutoServiceTest.java
```

## Como executar o projeto

### Pré-requisitos

- Java JDK 17 ou superior.
- Maven instalado.

### 1. Clonar o repositório

```bash
git clone URL_DO_SEU_REPOSITORIO
```

### 2. Entrar na pasta do projeto

```bash
cd Product-Management
```

### 3. Executar os testes

```bash
mvn clean test
```

### 4. Executar a aplicação

```bash
mvn compile exec:java
```

## Testes automatizados

O projeto utiliza JUnit 5 para verificar o funcionamento das classes e garantir que as regras de negócio sejam respeitadas.

Os testes verificam situações como:

- Criação válida de produtos.
- Validação de nome, preço e quantidade.
- Geração de IDs.
- Busca de produtos existentes e inexistentes.
- Atualização e remoção de produtos.
- Entrada e saída de estoque.
- Impedimento de estoque negativo.

Para executar todos os testes:

```bash
mvn test
```

## Aprendizados

Este projeto foi desenvolvido com foco no aprendizado e na prática de:

- Programação Orientada a Objetos.
- Organização de projetos Java.
- Separação de responsabilidades.
- Padrão Repository e camada de Service.
- Tratamento de exceções.
- Testes unitários com JUnit 5.
- Conceitos de TDD (Test-Driven Development).
- Controle de versão com Git.

## Observação

Os dados dos produtos são armazenados em memória utilizando uma estrutura `Map`. Portanto, os dados cadastrados são perdidos quando a aplicação é encerrada.

---

Desenvolvido para fins de estudo e prática de desenvolvimento Java.