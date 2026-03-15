# SalesPersistence-JPA

![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Hibernate](https://img.shields.io/badge/ORM-Hibernate%20%7C%20JPA-59666C?style=flat&logo=hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/Prod-PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/Test-H2%20In--memory-0056A0?style=flat)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![Status](https://img.shields.io/badge/Status-Concluído-brightgreen?style=flat)

> Sistema de gestão de vendas em Java com **JPA/Hibernate**, arquitetura N-Tier e `GenericDAO` genérico. Demonstra mapeamento ORM avançado, relacionamentos entre entidades, Criteria API com `JOIN FETCH` e estratégia de testes com banco H2 em memória.

> 📐 Quer entender as decisões técnicas e de arquitetura? Veja [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

---

## ✨ Destaques Técnicos

- **`GenericDAO<T, E>` com Java Generics** — CRUD completo reutilizável para qualquer entidade, eliminando código duplicado (princípio DRY)
- **Criteria API com `JOIN FETCH`** no `VendaDAO` — evita o problema N+1 buscando Cliente e Produtos da Venda em uma única query otimizada
- **Dois Persistence Units separados** — `sistemaVendas-PU` (PostgreSQL) e `sistemaVendas-Test-PU` (H2) para isolamento total entre produção e testes
- **`@After` com limpeza de banco** — cada teste roda em ambiente limpo e reproduzível
- **Mapeamento ORM completo**: `@ManyToOne`, `@OneToMany`, `@Enumerated`, `@SequenceGenerator`
- **`BigDecimal` para valores monetários** — sem risco de arredondamento de `double`/`float`

---

## 🏗️ Arquitetura N-Tier

```
┌─────────────────────────────────────────────┐
│              CAMADA DE SERVIÇO              │  ← Regras de negócio
│   ClienteService  │  ProdutoService         │
└──────────────────────┬──────────────────────┘
                       │ usa interface
┌──────────────────────▼──────────────────────┐
│               CAMADA DAO                    │  ← Acesso ao banco
│   ClienteDAO  │  ProdutoDAO  │  VendaDAO    │
│   └── GenericDAO<T, E> (EntityManager JPA)  │
└──────────────────────┬──────────────────────┘
                       │ JPA / Hibernate
┌──────────────────────▼──────────────────────┐
│     PostgreSQL (prod) │ H2 in-memory (test) │
└─────────────────────────────────────────────┘
```

### Criteria API no VendaDAO
```java
// Busca Venda + Cliente + Produtos em uma única query (sem N+1)
CriteriaBuilder builder = entityManager.getCriteriaBuilder();
CriteriaQuery<Venda> query = builder.createQuery(Venda.class);
Root<Venda> root = query.from(Venda.class);
root.fetch("cliente", JoinType.LEFT);
root.fetch("produtos", JoinType.LEFT);
query.select(root).where(builder.equal(root.get("id"), id));
```

---

## 🧪 Estratégia de Testes

| Frente | Banco | Propósito |
|:---|:---|:---|
| Testes de DAO | H2 in-memory (create-drop) | Validar mapeamento ORM e queries reais |
| Testes de Serviço | Mock manual | Validar regras de negócio sem banco |

O `@After` garante que cada teste limpa o banco, tornando a suíte 100% reproduzível e idempotente.

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|:---|:---|:---|
| Java | 17 LTS | Linguagem principal |
| JPA / Hibernate | Jakarta EE | Mapeamento ORM |
| PostgreSQL | latest | Banco de produção |
| H2 Database | latest | Banco de testes em memória |
| JUnit | 4.13 | Framework de testes |
| Maven | 3.x | Build e dependências |

---

## 🚀 Como Executar

**Pré-requisitos:** JDK 17+, Maven 3+, Docker

```bash
# 1. Suba o PostgreSQL e pgAdmin via Docker Compose
docker-compose up -d

# Rodar os testes (usa H2 automaticamente — sem configuração externa):
mvn clean test

# Para rodar com PostgreSQL (produção):
# Configure src/main/resources/META-INF/persistence.xml com suas credenciais
# O Hibernate cria as tabelas automaticamente (hbm2ddl.auto=update)
```

---

## 📌 Contexto no Portfólio

Este é o **projeto 4 de 5** da trilha de evolução técnica:

`UserControl (POO)` → `QualityGuard (Testes)` → `SalesSystem-JDBC` → **`SalesPersistence-JPA`** → `Sales-Microservices`

> *A evolução do JDBC para JPA demonstra a compreensão do trade-off: JPA aumenta produtividade, mas esconde complexidades que o projeto anterior explorou explicitamente.*

---

*Desenvolvido por [Renan Queiroz Eliziario](https://www.linkedin.com/in/renaneliziario/) · [Portfólio completo no GitHub](https://github.com/Renaneliziario)*

