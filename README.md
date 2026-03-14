# SalesPersistence-JPA: Sistema de Gestão de Vendas com JPA/Hibernate

Sistema de **back-end em Java** para gerenciamento robusto de vendas, clientes e produtos. Este projeto demonstra o domínio técnico sobre **arquitetura em camadas**, **padrões de projeto** e **persistência avançada com JPA/Hibernate**.

---

## 🏗️ Arquitetura em Camadas (N-Tier)

O projeto segue uma arquitetura de 3 camadas, garantindo a separação de responsabilidades (SoC):

```text
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

---

## 🧩 Modelo de Domínio (JPA Entities)

As entidades são mapeadas para o banco de dados relacional através de anotações JPA, utilizando tipos complexos e precisão financeira.

### `Venda` (Agregador Principal)
Controla o ciclo de vida da venda e o cálculo automático de totais.
*   **Relacionamento:** `@ManyToOne` com Cliente e `@OneToMany` com Itens de Venda (`ProdutoQuantidade`).
*   **Enum Status:** Gerencia estados `INICIADA`, `CONCLUIDA` e `CANCELADA`.
*   **Business Logic:** Métodos internos para `recalcularValorTotalVenda()` e validação de transições de status.

### `Produto` e `Cliente`
*   Uso de `@Column(unique=true)` para CPFs e Códigos de Produto.
*   Uso de `BigDecimal` para valores monetários, garantindo precisão absoluta (evitando erros de arredondamento de tipos `double` ou `float`).

---

## 🗄️ Camada DAO (Data Access Object)

### Padrão Generic DAO
Implementação de CRUD genérico utilizando **Java Generics**, reduzindo a repetição de código (DRY) e padronizando o acesso ao banco:
```java
// T = tipo da entidade | E = tipo da chave primária
public class GenericDAO<T, E extends Serializable> implements IGenericDAO<T, E>
```

### Consultas Avançadas
*   Uso de **Criteria API** no `VendaDAO` para realizar `JOIN FETCH`, evitando o problema de performance N+1 e buscando dados relacionados (Cliente e Produtos) em uma única consulta SQL otimizada.

---

## 🧪 Estratégia de Testes Automatizados

O projeto utiliza uma estratégia de testes em duas frentes:

1.  **Testes de Integração (DAO):** Utilizam o banco **H2 em memória** (`create-drop`) para validar o mapeamento ORM e as queries reais sem afetar o banco de produção.
2.  **Testes Unitários (Service):** Utilizam **Mocks** (através de implementações específicas de teste) para isolar a lógica de negócio e garantir que as regras sejam validadas sem dependência de infraestrutura.

---

## 🛠️ Tecnologias e Ferramentas

| Tecnologia | Finalidade |
|---|---|
| **Java 17+** | Linguagem principal e lógica de negócio |
| **JPA / Hibernate 5.6** | Mapeamento objeto-relacional (ORM) |
| **PostgreSQL** | Banco de dados em ambiente de produção |
| **H2 Database** | Banco em memória para ambiente de testes |
| **JUnit 4.13** | Framework para execução de testes automatizados |
| **Maven** | Gestão de dependências e automação de build |

---

## 🚀 Como Executar

### Pré-requisitos
*   JDK 17 ou superior.
*   Maven 3.x.

### Passos
1.  **Build e Testes:** `mvn clean install` (Isso rodará todos os testes no banco H2 automaticamente).
2.  **Configuração PostgreSQL:** Para rodar fora dos testes, configure suas credenciais no arquivo `src/main/resources/META-INF/persistence.xml`.
3.  **Schema do Banco:** O Hibernate está configurado com `hbm2ddl.auto = update`, criando as tabelas automaticamente no primeiro acesso.

---

## 📌 Evolução Técnica
Este projeto representa o amadurecimento técnico na trilha de Back-End, consolidando o uso de frameworks industriais. Ele serve como base sólida para a transição para arquiteturas modernas de Microserviços e Spring Boot.

---
*Desenvolvido por Renan Queiroz Eliziario como parte do portfólio profissional de arquitetura Java.*
