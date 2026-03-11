# Sistema de Gestão de Vendas com JPA/Hibernate

Sistema de **back-end em Java** para gerenciamento de vendas, clientes e produtos.
Demonstra **arquitetura em camadas**, **padrões de projeto** e **persistência com JPA/Hibernate**.

---

## 📑 Índice

1. [Visão Geral](#-visão-geral)
2. [Tecnologias](#️-tecnologias)
3. [Estrutura do Projeto](#-estrutura-do-projeto)
4. [Arquitetura em Camadas](#️-arquitetura-em-camadas)
5. [Domínio (Entidades)](#-domínio-entidades)
6. [Camada DAO](#-camada-dao-data-access-object)
7. [Camada Service](#-camada-service)
8. [Exceções Customizadas](#-exceções-customizadas)
9. [Banco de Dados](#️-banco-de-dados)
10. [Testes](#-testes)
11. [Como Executar](#️-como-executar)
12. [Conceitos Importantes](#-conceitos-importantes)
13. [Próximos Passos](#-próximos-passos)

---

## 🔍 Visão Geral

O projeto implementa as operações básicas de um sistema de vendas:

- **Cadastro e consulta de Clientes** (com busca por CPF)
- **Cadastro e consulta de Produtos**
- **Criação e gerenciamento de Vendas** (adicionar/remover produtos, finalizar, cancelar)
- **Cálculo automático de totais** de venda

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem principal |
| JPA / Hibernate | 5.6 | Mapeamento objeto-relacional (ORM) |
| PostgreSQL | 42.5.0 (driver) | Banco de dados em produção |
| H2 Database | 2.1.214 | Banco em memória para testes |
| JUnit | 4.13.2 | Framework de testes |
| Maven | — | Gerenciamento de dependências e build |

---

## 📂 Estrutura do Projeto

```text
SistemaVendasComJPA/
├── pom.xml                                        # Configuração Maven (dependências)
└── src/
    ├── main/
    │   └── java/br/com/renan/
    │       ├── domain/                            # Entidades JPA (tabelas do banco)
    │       │   ├── Cliente.java
    │       │   ├── Produto.java
    │       │   ├── Venda.java
    │       │   └── ProdutoQuantidade.java
    │       ├── dao/                               # Acesso ao banco de dados (DAO)
    │       │   ├── generic/
    │       │   │   ├── IGenericDAO.java           # Interface genérica de CRUD
    │       │   │   └── GenericDAO.java            # Implementação genérica de CRUD
    │       │   ├── IClienteDAO.java
    │       │   ├── ClienteDAO.java
    │       │   ├── IProdutoDAO.java
    │       │   ├── ProdutoDAO.java
    │       │   ├── IVendaDAO.java
    │       │   └── VendaDAO.java
    │       ├── services/                          # Lógica de negócio (Service)
    │       │   ├── generic/
    │       │   │   ├── IGenericService.java
    │       │   │   └── GenericService.java
    │       │   ├── IClienteService.java
    │       │   ├── ClienteService.java
    │       │   ├── IProdutoService.java
    │       │   └── ProdutoService.java
    │       └── exceptions/                        # Exceções customizadas
    │           ├── DAOException.java
    │           ├── MaisDeUmRegistroException.java
    │           ├── TableException.java
    │           ├── TipoChaveNaoEncontradaException.java
    │           └── TipoElementoNaoConhecidoException.java
    ├── resources/java/META-INF/
    │   └── persistence.xml                        # Configuração JPA (PostgreSQL - produção)
    └── test/
        ├── java/br/com/renan/
        │   ├── ClienteDAOTest.java
        │   ├── ProdutoDAOTest.java
        │   ├── VendaDAOTest.java
        │   ├── ClienteServiceTest.java
        │   ├── ProdutoServiceTest.java
        │   ├── AllTests.java
        │   ├── testeConexao.java
        │   └── dao/
        │       ├── ClienteDaoMock.java            # Mock para testes de Service
        │       └── ProdutoDaoMock.java
        └── resources/META-INF/
            └── persistence.xml                    # Configuração JPA (H2 - testes)
```

---

## 🏗️ Arquitetura em Camadas

O projeto segue uma **arquitetura em 3 camadas**, onde cada camada tem uma responsabilidade bem definida e só se comunica com a camada imediatamente abaixo dela:

```
┌─────────────────────────────────────────────┐
│              CAMADA DE SERVIÇO              │  ← Regras de negócio
│   ClienteService  │  ProdutoService         │
│      (usa IClienteDAO / IProdutoDAO)        │
└──────────────────────┬──────────────────────┘
                       │ chama
┌──────────────────────▼──────────────────────┐
│               CAMADA DAO                    │  ← Acesso ao banco
│   ClienteDAO  │  ProdutoDAO  │  VendaDAO    │
│         (usa EntityManager JPA)             │
└──────────────────────┬──────────────────────┘
                       │ persiste
┌──────────────────────▼──────────────────────┐
│              CAMADA DE DOMÍNIO              │  ← Dados e regras da entidade
│  Cliente │ Produto │ Venda │ ProdutoQtd     │
└─────────────────────────────────────────────┘
```

**Por que separar em camadas?**
- **Manutenção**: alterar como os dados são salvos não afeta a lógica de negócio.
- **Testabilidade**: é possível testar a `Service` sem banco de dados real (usando Mocks).
- **Legibilidade**: cada classe tem uma única responsabilidade clara.

---

## 🧩 Domínio (Entidades)

As entidades são classes Java mapeadas para tabelas do banco de dados via anotações JPA.

### `Cliente` → Tabela `TB_CLIENTE`

Representa um cliente do sistema.

| Campo Java | Coluna SQL | Tipo | Regras |
|---|---|---|---|
| `id` | `id` | `Long` | PK, gerado por sequência `sq_cliente` |
| `nome` | `nome` | `String` | Obrigatório, máx. 50 caracteres |
| `cpf` | `cpf` | `Long` | Obrigatório, único |
| `tel` | `tel` | `Long` | Obrigatório |
| `end` | `endereco` | `String` | Obrigatório, máx. 100 caracteres |
| `numero` | `numero` | `Integer` | Obrigatório |
| `cidade` | `cidade` | `String` | Obrigatório, máx. 50 caracteres |
| `estado` | `estado` | `String` | Obrigatório, máx. 2 caracteres |

---

### `Produto` → Tabela `TB_PRODUTO`

Representa um produto disponível para venda.

| Campo Java | Coluna SQL | Tipo | Regras |
|---|---|---|---|
| `id` | `id` | `Long` | PK, gerado por sequência `sq_produto` |
| `codigo` | `codigo` | `String` | Obrigatório, único, máx. 10 caracteres |
| `nome` | `nome` | `String` | Obrigatório, máx. 50 caracteres |
| `descricao` | `descricao` | `String` | Obrigatório, máx. 70 caracteres |
| `valor` | `valor` | `BigDecimal` | Obrigatório |

> **Por que `BigDecimal` e não `double`?**  
> `double` tem imprecisão em cálculos decimais (ex.: `0.1 + 0.2 = 0.30000000000000004`).  
> `BigDecimal` garante precisão total para valores monetários.

---

### `Venda` → Tabela `TB_VENDA`

Representa uma venda associada a um cliente, com vários produtos.

| Campo Java | Coluna SQL | Tipo | Regras |
|---|---|---|---|
| `id` | `id` | `Long` | PK, gerado por sequência `sq_venda` |
| `codigo` | `codigo` | `String` | Obrigatório, único |
| `cliente` | `id_cliente_fk` | `Cliente` | FK para `TB_CLIENTE`, obrigatório |
| `produtos` | — | `Set<ProdutoQuantidade>` | Relacionamento `@OneToMany` |
| `valorTotal` | `valor_total` | `BigDecimal` | Calculado automaticamente |
| `dataVenda` | `data_venda` | `Instant` | Obrigatório |
| `status` | `status_venda` | `Status` (enum) | `INICIADA`, `CONCLUIDA` ou `CANCELADA` |

**Enum `Status`** — controla o ciclo de vida da venda:
```
INICIADA → CONCLUIDA
         → CANCELADA
```
> Uma venda `CONCLUIDA` **não pode mais ser alterada**. Qualquer tentativa lança `UnsupportedOperationException`.

**Métodos de negócio da entidade `Venda`:**

| Método | O que faz |
|---|---|
| `adicionarProduto(produto, qtd)` | Adiciona ou incrementa um produto; recalcula o total |
| `removerProduto(produto, qtd)` | Remove ou decrementa um produto; recalcula o total |
| `removerTodosProdutos()` | Limpa todos os produtos e zera o total |
| `recalcularValorTotalVenda()` | Soma o valor de todos os `ProdutoQuantidade` |
| `getQuantidadeTotalProdutos()` | Retorna a soma das quantidades de todos os itens |

---

### `ProdutoQuantidade` → Tabela `TB_PRODUTO_QUANTIDADE`

Tabela intermediária que representa **quantos de cada produto** estão em uma venda (padrão de modelagem: *item de pedido*).

| Campo Java | Coluna SQL | Tipo | Descrição |
|---|---|---|---|
| `id` | `id` | `Long` | PK |
| `produto` | `id_produto_fk` | `Produto` | FK para `TB_PRODUTO` |
| `quantidade` | `quantidade` | `Integer` | Quantidade do produto nesta venda |
| `valorTotal` | `valor_total` | `BigDecimal` | `quantidade × produto.valor` |
| `venda` | `id_venda_fk` | `Venda` | FK para `TB_VENDA` |

---

### Diagrama de Relacionamento entre Entidades

```
┌──────────┐        ┌──────────┐        ┌───────────────────┐        ┌──────────┐
│ TB_VENDA │───────►│TB_CLIENTE│        │TB_PRODUTO_QUANTIDADE│───────►│TB_PRODUTO│
│          │  N:1   └──────────┘  1:N   │                   │  N:1   └──────────┘
│          │◄───────────────────────────│                   │
└──────────┘                            └───────────────────┘
```

- Uma **Venda** pertence a um **Cliente** (`@ManyToOne`)
- Uma **Venda** possui vários **ProdutoQuantidade** (`@OneToMany`)
- Cada **ProdutoQuantidade** referencia um **Produto** (`@ManyToOne`)

---

## 🗄️ Camada DAO (Data Access Object)

Responsável **exclusivamente** por comunicar com o banco de dados. Nenhuma regra de negócio fica aqui.

### Generic DAO — o coração da camada

O padrão **Generic DAO** usa **Java Generics** para criar uma implementação de CRUD que serve para qualquer entidade, evitando repetição de código.

```java
// T = tipo da entidade (ex.: Cliente)
// E = tipo da chave primária (ex.: Long)
public class GenericDAO<T, E extends Serializable> implements IGenericDAO<T, E>
```

**Interface `IGenericDAO<T, E>`** — define o contrato:

| Método | Descrição |
|---|---|
| `cadastrar(T entity)` | Persiste a entidade no banco (`INSERT`) |
| `excluir(T entity)` | Remove a entidade do banco (`DELETE`) |
| `alterar(T entity)` | Atualiza a entidade no banco (`UPDATE`) |
| `consultar(E id)` | Busca uma entidade pela chave primária (`SELECT` por ID) |
| `buscarTodos()` | Retorna todas as entidades da tabela (`SELECT *`) |

**`GenericDAO`** implementa esses métodos usando o `EntityManager` do JPA:

```
cadastrar  → entityManager.persist(entity)
alterar    → entityManager.merge(entity)
excluir    → entityManager.remove(entity)
consultar  → entityManager.find(Class, id)
buscarTodos→ entityManager.createQuery("SELECT e FROM ...")
```

### DAOs Específicos

| Classe | Herda de | Método extra |
|---|---|---|
| `ClienteDAO` | `GenericDAO<Cliente, Long>` | `buscarPorCPF(Long cpf)` — JPQL com parâmetro |
| `ProdutoDAO` | `GenericDAO<Produto, Long>` | Nenhum (CRUD genérico basta) |
| `VendaDAO` | `GenericDAO<Venda, Long>` | `finalizarVenda()`, `cancelarVenda()`, `consultar()` com JOIN FETCH |

**Destaque — `VendaDAO.consultar()`:**  
Usa a **Criteria API** do JPA para buscar a venda **junto com** cliente e produtos em uma única query (evitando o problema N+1):
```java
root.fetch("cliente", JoinType.LEFT);
root.fetch("produtos", JoinType.LEFT);
```

---

## ⚙️ Camada Service

Responsável por **orquestrar a lógica de negócio**. Recebe as chamadas de quem usa o sistema e delega a persistência ao DAO.

### Generic Service

Segue o mesmo padrão genérico do DAO:

```java
public abstract class GenericService<T, E extends Serializable> implements IGenericService<T, E> {
    protected IGenericDAO<T, E> dao; // recebe o DAO por injeção no construtor
}
```

> **Atenção:** a `Service` depende da **interface** `IGenericDAO`, não da implementação concreta.  
> Isso é o princípio **"Programe para interfaces, não para implementações"** — permite trocar o DAO por um Mock nos testes.

### Services Específicos

| Classe | Interface | Comportamento extra |
|---|---|---|
| `ClienteService` | `IClienteService` | `buscarPorCPF()` — delega ao `IClienteDAO` |
| `ProdutoService` | `IProdutoService` | Apenas CRUD genérico |

---

## ⚠️ Exceções Customizadas

Exceções com nomes descritivos tornam o código mais legível e o tratamento de erros mais preciso.

| Classe | Quando usar |
|---|---|
| `DAOException` | Erro genérico ao acessar o banco de dados |
| `MaisDeUmRegistroException` | Consulta que deveria retornar 1 registro retornou mais |
| `TableException` | Problema relacionado a uma tabela |
| `TipoChaveNaoEncontradaException` | Tipo da chave primária não reconhecido |
| `TipoElementoNaoConhecidoException` | Elemento de tipo desconhecido encontrado |

---

## 🗃️ Banco de Dados

### Configuração de Produção (PostgreSQL)

Arquivo: `src/resources/java/META-INF/persistence.xml`

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/vendas_online_2" />
<property name="javax.persistence.jdbc.user" value="renan" />
<property name="javax.persistence.jdbc.password" value="admin" />
<property name="hibernate.hbm2ddl.auto" value="update" />
```

> `hibernate.hbm2ddl.auto = update` — o Hibernate **atualiza** o schema automaticamente ao iniciar.  
> Em produção real recomenda-se usar `validate` e gerenciar o schema com ferramentas como Flyway.

### Configuração de Testes (H2 em memória)

Arquivo: `src/test/resources/META-INF/persistence.xml`

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1" />
<property name="hibernate.hbm2ddl.auto" value="create-drop" />
```

> `create-drop` — cria as tabelas ao iniciar os testes e **apaga tudo** ao terminar. Isso garante isolamento total entre execuções de teste.

### Schema gerado pelo Hibernate

```sql
-- Sequências para geração de IDs
CREATE SEQUENCE sq_cliente  START 1 INCREMENT 1;
CREATE SEQUENCE sq_produto  START 1 INCREMENT 1;
CREATE SEQUENCE sq_venda    START 1 INCREMENT 1;
CREATE SEQUENCE sq_prod_qtd START 1 INCREMENT 1;

CREATE TABLE TB_CLIENTE (
    id      BIGINT PRIMARY KEY,
    nome    VARCHAR(50)  NOT NULL,
    cpf     BIGINT       NOT NULL UNIQUE,
    tel     BIGINT       NOT NULL,
    endereco VARCHAR(100) NOT NULL,
    numero  INTEGER      NOT NULL,
    cidade  VARCHAR(50)  NOT NULL,
    estado  VARCHAR(2)   NOT NULL
);

CREATE TABLE TB_PRODUTO (
    id        BIGINT PRIMARY KEY,
    codigo    VARCHAR(10)    NOT NULL UNIQUE,
    nome      VARCHAR(50)    NOT NULL,
    descricao VARCHAR(70)    NOT NULL,
    valor     NUMERIC(19, 2) NOT NULL
);

CREATE TABLE TB_VENDA (
    id           BIGINT PRIMARY KEY,
    codigo       VARCHAR(255)   NOT NULL UNIQUE,
    id_cliente_fk BIGINT        NOT NULL REFERENCES TB_CLIENTE(id),
    valor_total  NUMERIC(19, 2) NOT NULL,
    data_venda   TIMESTAMP      NOT NULL,
    status_venda VARCHAR(255)   NOT NULL
);

CREATE TABLE TB_PRODUTO_QUANTIDADE (
    id           BIGINT PRIMARY KEY,
    id_produto_fk BIGINT        NOT NULL REFERENCES TB_PRODUTO(id),
    id_venda_fk  BIGINT         NOT NULL REFERENCES TB_VENDA(id),
    quantidade   INTEGER        NOT NULL,
    valor_total  NUMERIC(19, 2) NOT NULL
);
```

---

## 🧪 Testes

O projeto possui dois tipos de testes:

### Testes de Integração (DAO Tests)

Utilizam o banco **H2 em memória** para testar as operações reais de banco de dados.

| Classe de Teste | O que testa |
|---|---|
| `ClienteDAOTest` | `cadastrar`, `excluir`, `alterar`, `buscarPorCPF`, `buscarTodos` |
| `ProdutoDAOTest` | CRUD completo de Produto |
| `VendaDAOTest` | Criar venda, adicionar/remover produtos, finalizar, cancelar, regras de status |

**Padrão `@Before` / `@After`:**
```java
@Before  // executa ANTES de cada teste — prepara dados
public void init() { ... }

@After   // executa APÓS cada teste — limpa o banco
public void end() { ... }
```
Isso garante que cada teste rode de forma **independente e isolada**.

**Exemplo de teste com exceção esperada:**
```java
@Test(expected = UnsupportedOperationException.class)
public void tentarAdicionarProdutosVendaFinalizada() { ... }
```
Verifica que o sistema **lança a exceção correta** quando a regra de negócio é violada.

### Testes Unitários com Mock (Service Tests)

Testam a camada `Service` **sem banco de dados**, usando implementações falsas (Mocks) do DAO.

| Classe de Teste | Mock usado | O que testa |
|---|---|---|
| `ClienteServiceTest` | `ClienteDaoMock` | Operações do `ClienteService` em isolamento |
| `ProdutoServiceTest` | `ProdutoDaoMock` | Operações do `ProdutoService` em isolamento |

> **Por que usar Mocks?**  
> Testes unitários devem ser rápidos e não depender de infraestrutura externa.  
> O Mock simula o comportamento do DAO, permitindo testar **apenas a lógica da Service**.

---

## ⚙️ Como Executar

### Pré-requisitos

- Java 17+
- Maven 3+
- PostgreSQL (para rodar fora dos testes)

### 1. Configurar o banco de dados

Edite o arquivo `src/resources/java/META-INF/persistence.xml` com suas credenciais:

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/SEU_BANCO" />
<property name="javax.persistence.jdbc.user" value="SEU_USUARIO" />
<property name="javax.persistence.jdbc.password" value="SUA_SENHA" />
```

### 2. Compilar o projeto

```bash
mvn clean compile
```

### 3. Executar os testes (usa H2, não precisa de PostgreSQL)

```bash
mvn test
```

### 4. Build completo (compila + testa + empacota)

```bash
mvn clean install
```

---

## 📚 Conceitos Importantes

### O que é JPA?
**JPA (Java Persistence API)** é uma especificação Java para mapeamento objeto-relacional.  
Ela define **como** mapear classes Java para tabelas de banco de dados usando anotações.  
O **Hibernate** é a implementação mais popular dessa especificação.

### Principais anotações JPA usadas

| Anotação | Onde aparece | O que faz |
|---|---|---|
| `@Entity` | Nas classes de domínio | Marca a classe como uma entidade mapeada para o banco |
| `@Table(name="...")` | Nas classes de domínio | Define o nome da tabela no banco |
| `@Id` | No campo `id` | Marca o campo como chave primária |
| `@GeneratedValue` | No campo `id` | Define a estratégia de geração automática do ID |
| `@SequenceGenerator` | No campo `id` | Configura a sequência SQL usada para gerar IDs |
| `@Column` | Nos campos | Define nome, tamanho e restrições da coluna |
| `@ManyToOne` | Em `Venda.cliente` e `ProdutoQuantidade` | N registros para 1 registro relacionado |
| `@OneToMany` | Em `Venda.produtos` | 1 registro para N registros relacionados |
| `@JoinColumn` | Em relacionamentos | Define a coluna de chave estrangeira (FK) |
| `@Enumerated` | Em `Venda.status` | Mapeia um enum Java para o banco |
| `@ForeignKey` | Em relacionamentos | Define o nome da constraint de FK no banco |
| `CascadeType.ALL` | Em `@OneToMany` | Operações na Venda propagam para os ProdutoQuantidade |

### Estados de uma entidade JPA

```
new Cliente()        → TRANSIENT  (não gerenciado pelo JPA)
    ↓ persist()
entityManager        → MANAGED    (JPA rastreia alterações)
    ↓ commit() / detach()
                     → DETACHED   (desconectado do contexto)
    ↓ merge()
entityManager        → MANAGED    (reconectado)
    ↓ remove()
                     → REMOVED    (será deletado no commit)
```

### O que é o padrão DAO?
**DAO (Data Access Object)** isola todo o código de acesso ao banco em classes específicas.  
O restante da aplicação não sabe **como** os dados são salvos — só sabe **que** pode chamar `cadastrar()`, `consultar()`, etc.

---

## 📌 Próximos Passos

- Expor o domínio via **API REST** com Spring Boot
- Adicionar cobertura de testes para `VendaService`
- Containerizar com **Docker** (banco + aplicação)
- Integrar **CI/CD** com GitHub Actions
- Migrar gestão de schema para **Flyway**
