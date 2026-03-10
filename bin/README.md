# Sistema de Gestão de Vendas com JPA/Hibernate

Este projeto é um sistema de back-end desenvolvido em Java para gestão de vendas, clientes e produtos. Ele foi estruturado para demonstrar práticas avançadas de arquitetura de software, padrões de projeto (Design Patterns) e persistência de dados robusta através do JPA/Hibernate.

---

## 🚀 Principais Aprendizados e Diferenciais Profissionais

Este projeto vai além de um CRUD básico, implementando conceitos fundamentais para o desenvolvimento de sistemas corporativos escaláveis:

### 1. Arquitetura e Padrões de Projeto (Design Patterns)
*   **Generic DAO Pattern:** Implementação de uma camada de persistência genérica utilizando **Java Generics**. Isso reduz a duplicidade de código (DRY) e centraliza a lógica de acesso a dados, facilitando a manutenção e expansão para novas entidades.
*   **Layered Architecture:** Separação clara entre as camadas de **Domínio**, **DAO (Data Access Object)** e **Service**, garantindo baixo acoplamento e alta coesão.

### 2. Persistência de Dados Avançada com JPA/Hibernate
*   **Mapeamento Objeto-Relacional (ORM):** Uso profissional de anotações JPA para gerenciar relacionamentos complexos (`@OneToMany`, `@ManyToOne`) e integridade de dados via banco de dados (`Unique Constraints`, `Nullable`, `Sequences`).
*   **Ciclo de Vida e Cascatas:** Domínio sobre o gerenciamento de estados de entidades (Transient, Managed, Detached) e uso estratégico de `CascadeType` para operações em lote (ex: salvar uma venda e seus itens simultaneamente).
*   **Modelagem de Domínio Rico:** A lógica de negócio reside nas entidades (ex: cálculo de totais e validação de status), seguindo os princípios de Orientação a Objetos e evitando "Modelos Anêmicos".

### 3. Estratégia de Testes e Qualidade
*   **Testes de Integração com Banco em Memória:** Utilização do banco **H2** para execução de testes automatizados rápidos e isolados, enquanto o **PostgreSQL** é utilizado para o ambiente de produção via `persistence.xml` distintos.
*   **JUnit 4:** Cobertura de testes abrangente para as camadas de DAO e Service, garantindo a confiabilidade das regras de negócio e prevenindo regressões.

### 4. Java Moderno e Clean Code
*   **Java Streams & Optional:** Uso de APIs modernas para manipulação de coleções e tratamento de valores nulos, tornando o código mais legível, funcional e seguro.
*   **Tratamento de Exceções Personalizado:** Criação de uma hierarquia de exceções de negócio para um controle de erros refinado e semântico (ex: `DAOException`, `TableException`).
*   **BigDecimal:** Uso rigoroso de `BigDecimal` para todas as operações financeiras, garantindo precisão absoluta nos cálculos de valores de venda e evitando erros comuns de arredondamento.

---

## 🛠️ Tecnologias Utilizadas

*   **Linguagem:** Java 17
*   **Framework ORM:** JPA / Hibernate 5.6
*   **Bancos de Dados:** PostgreSQL (Produção) e H2 (Testes)
*   **Gerenciador de Dependências:** Maven
*   **Testes:** JUnit 4

---

## 📂 Estrutura do Projeto

*   `src/main/java`: Código fonte da aplicação.
    *   `domain`: Entidades de negócio mapeadas para o banco.
    *   `dao`: Interfaces e implementações (Generic DAO).
    *   `services`: Orquestração da lógica de negócio.
    *   `exceptions`: Classes de erro customizadas.
*   `src/test/java`: Testes automatizados.
*   `src/main/resources`: Configurações de persistência (PostgreSQL).
*   `src/test/resources`: Configurações de persistência para testes (H2).

---

## ⚙️ Como Executar

1. **Requisitos:** Java 17+ e Maven instalados.
2. **Configuração do Banco:** Verifique as credenciais no arquivo `src/main/resources/META-INF/persistence.xml`.
3. **Compilar e Testar:**
   ```bash
   mvn clean install
   ```
4. **Executar Testes Específicos:**
   ```bash
   mvn test
   ```

---
**Autor:** Renan Queiroz Eliziario 
*Projeto desenvolvido como parte do aprimoramento em Java e persistência de dados corporativos.*
