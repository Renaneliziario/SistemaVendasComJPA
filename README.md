# Sistema de Gestão de Vendas com JPA/Hibernate

Este projeto é um sistema de **back-end em Java** para gestão de vendas, clientes e produtos.  
Ele foi estruturado para demonstrar **práticas avançadas de arquitetura de software**, **padrões de projeto (Design Patterns)** e **persistência de dados robusta** com **JPA/Hibernate**.

---

## 🚀 Principais aprendizados e diferenciais profissionais

Este projeto vai além de um CRUD básico, implementando conceitos fundamentais para o desenvolvimento de sistemas corporativos escaláveis.

### 1. Arquitetura e padrões de projeto (Design Patterns)

- **Generic DAO Pattern**  
  Implementação de uma camada de persistência genérica utilizando **Java Generics**.  
  Isso reduz duplicidade de código (princípio **DRY**) e centraliza a lógica de acesso a dados, facilitando manutenção e expansão para novas entidades.

- **Arquitetura em camadas (Layered Architecture)**  
  Separação clara entre as camadas de **Domínio**, **DAO (Data Access Object)** e **Service**, garantindo **baixo acoplamento** e **alta coesão**.

### 2. Persistência de dados avançada com JPA/Hibernate

- **Mapeamento Objeto-Relacional (ORM)**  
  Uso profissional de anotações JPA para gerenciar relacionamentos complexos (`@OneToMany`, `@ManyToOne`) e integridade de dados via banco de dados  
  (unique constraints, campos `nullable`, sequences, etc.).

- **Ciclo de vida e cascatas**  
  Domínio sobre o gerenciamento de estados de entidades (**Transient, Managed, Detached**) e uso estratégico de `CascadeType` para operações em lote  
  (ex.: salvar uma venda e seus itens simultaneamente).

- **Modelagem de domínio rico**  
  A lógica de negócio reside nas entidades (ex.: cálculo de totais e validação de status), seguindo princípios de **Orientação a Objetos**  
  e evitando *modelos anêmicos*.

### 3. Estratégia de testes e qualidade

- **Testes de integração com banco em memória**  
  Utilização do banco **H2** para execução de testes automatizados rápidos e isolados, enquanto o **PostgreSQL** é utilizado para o ambiente de produção,  
  via arquivos `persistence.xml` distintos.

- **JUnit 4**  
  Cobertura de testes para as camadas de **DAO** e **Service**, garantindo confiabilidade das regras de negócio e prevenindo regressões.

### 4. Java moderno e Clean Code

- **Java Streams & Optional**  
  Uso de APIs modernas para manipulação de coleções e tratamento de valores nulos, tornando o código mais **legível**, **funcional** e **seguro**.

- **Tratamento de exceções personalizado**  
  Criação de uma hierarquia de exceções de negócio para um controle de erros refinado e semântico  
  (ex.: `DAOException`, `TableException`).

- **BigDecimal para operações financeiras**  
  Uso rigoroso de `BigDecimal` em todas as operações financeiras, garantindo precisão nos cálculos de valores de venda e evitando erros de arredondamento.

---

## 🛠️ Tecnologias utilizadas

- **Linguagem:** Java 17  
- **ORM:** JPA / Hibernate 5.6  
- **Bancos de dados:** PostgreSQL (produção) e H2 (testes)  
- **Gerenciador de dependências:** Maven  
- **Testes:** JUnit 4  

---

## 📂 Estrutura do projeto

```text
src/
  main/
    java/
      domain/      # Entidades de negócio mapeadas para o banco
      dao/         # Interfaces e implementações (Generic DAO)
      services/    # Orquestração da lógica de negócio
      exceptions/  # Classes de erro customizadas
  test/
    java/          # Testes automatizados
  main/resources/  # Configura��ões de persistência (PostgreSQL)
  test/resources/  # Configurações de persistência para testes (H2)
```

---

## ⚙️ Como executar

### Requisitos

- Java 17+ instalado  
- Maven instalado  
- Banco PostgreSQL disponível (caso utilize o banco real em execução)

### Configuração do banco de dados

1. Verifique as credenciais no arquivo:  
   `src/main/resources/META-INF/persistence.xml`
2. Ajuste:
   - URL do banco
   - Usuário
   - Senha

### Compilar e executar testes

```bash
mvn clean install
```

### Executar somente os testes

```bash
mvn test
```


## 📌 Próximos passos (ideias de evolução)

- Expor o domínio via **API REST** usando Spring Boot.  
- Adicionar **mais casos de teste** cobrindo regras de negócio complexas.  
- Containerizar o projeto com **Docker** (banco e aplicação).  
- Integrar ferramentas de **CI** (GitHub Actions) para rodar testes automaticamente a cada push.
