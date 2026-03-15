# Arquitetura — SalesPersistence-JPA

## Visão Geral

Sistema de gestão de vendas Back-End construído sobre Arquitetura em Camadas (N-Tier), com foco em mapeamento ORM avançado, reutilização de código via Generic DAO e confiabilidade garantida por testes automatizados.

---

## Estrutura de Camadas

```
src/main/java/br/com/renan/
│
├── domain/          → Entidades de negócio (Cliente, Produto, Venda, ProdutoQuantidade)
├── dao/             → Acesso a dados (interfaces + implementações JPA)
│   └── generic/     → GenericDAO e IGenericDAO — contrato e implementação reutilizável
├── services/        → Regras de negócio (interfaces + implementações)
│   └── generic/     → GenericService — abstração de serviço reutilizável
└── exceptions/      → Hierarquia de exceções de domínio

src/test/java/br/com/renan/
├── ClienteDAOTest       → Testes de integração com banco H2
├── ClienteServiceTest   → Testes de unidade com Mocks
├── ProdutoDAOTest
├── ProdutoServiceTest
├── VendaDAOTest
├── dao/
│   ├── ClienteDaoMock   → Implementação fake para testes
│   └── ProdutoDaoMock
└── AllTests             → Suite que agrupa todos os testes
```

---

## Decisões de Arquitetura

### 1. Por que Generic DAO?

O sistema possui três entidades principais (Cliente, Produto, Venda), cada uma com as mesmas operações básicas de CRUD. Sem o Generic DAO, cada entidade teria sua própria implementação duplicada de `salvar`, `alterar`, `deletar`, `buscar` — violando o princípio DRY.

```java
// IGenericDAO define o contrato reutilizável
public interface IGenericDAO<T, E extends Serializable> {
    T cadastrar(T entity) throws Exception;
    void excluir(T entity) throws Exception;
    T alterar(T entity) throws Exception;
    T consultar(E value) throws Exception;
    Collection<T> buscarTodos() throws Exception;
}

// ClienteDAO herda tudo — só implementa o que é específico de Cliente
public class ClienteDAO extends GenericDAO<Cliente, Long> implements IClienteDAO { }
```

**Benefício:** Adicionar uma nova entidade exige apenas uma classe de domínio e uma interface — o CRUD vem de graça pelo GenericDAO.

---

### 2. Por que duas persistence units (prod e test)?

O `persistence.xml` define duas unidades separadas:
- `vendas-pu` → aponta para PostgreSQL (produção/desenvolvimento)
- `vendas-pu-test` → aponta para H2 em memória (testes)

**Motivo:** Os testes de integração precisam de um banco real para validar queries JPA, mas não devem depender do PostgreSQL estar rodando. O H2 em memória sobe automaticamente com o teste e é destruído ao final — ambiente isolado e reproduzível.

---

### 3. Por que programar para interfaces (IClienteDAO, IClienteService)?

Cada camada depende apenas da interface da camada inferior, nunca da implementação concreta. Isso garante:

- **Testabilidade:** Nos testes de Service, substitui-se a implementação real por um Mock (`ClienteDaoMock`) sem alterar nenhuma linha de código de produção.
- **Flexibilidade:** Trocar JPA/Hibernate por outro framework de persistência exigiria apenas uma nova implementação de `IClienteDAO` — o Service não perceberia a mudança.

---

### 4. Por que hierarquia de exceções customizadas?

Em vez de lançar `Exception` genérica, o projeto define exceções específicas:

| Exceção | Quando ocorre |
|---|---|
| `DAOException` | Falha na camada de persistência |
| `MaisDeUmRegistroException` | Query retorna mais de um resultado quando esperava um |
| `TipoChaveNaoEncontradaException` | Entidade sem campo `@Id` mapeado |
| `VendasException` | Regra de negócio de venda violada |

**Benefício:** O código chamador sabe exatamente o que tratou — mensagens de erro claras e rastreabilidade precisa de falhas.

---

### 5. Por que BigDecimal para valores monetários?

Valores financeiros (`double` e `float`) sofrem de imprecisão de ponto flutuante em Java. Um cálculo como `0.1 + 0.2` pode retornar `0.30000000000000004`. Para um sistema de vendas, essa imprecisão é inaceitável. `BigDecimal` garante precisão arbitrária e arredondamento correto.

---

## Fluxo de uma operação (exemplo: cadastrar Cliente)

```
Controller / Teste
       │
       ▼
IClienteService.cadastrar(cliente)
       │
       ▼
ClienteService  ← valida regras de negócio
       │
       ▼
IClienteDAO.cadastrar(cliente)
       │
       ▼
GenericDAO  ← EntityManager.persist()
       │
       ▼
JPA/Hibernate  ← gera e executa SQL
       │
       ▼
PostgreSQL / H2
```

---

## O que eu faria diferente hoje

- **Adicionar Service para Venda:** O projeto tem `VendaDAO` mas não chegou a ter um `VendaService` completo — a regra de negócio de venda ficou parcialmente na camada de DAO.
- **Usar Spring Data JPA:** O GenericDAO resolve o problema de duplicação, mas o Spring Data JPA resolve o mesmo problema com ainda menos código e mais recursos (paginação, queries derivadas).
- **Adicionar um DTO layer:** As entidades de domínio são expostas diretamente nos testes. Em um sistema real, DTOs separariam o contrato de API do modelo interno.
