# Explicação dos Testes — SistemaVendasComJPA

---

## Como os testes funcionam

O projeto usa **JUnit 4** e possui dois tipos de testes:

| Tipo | Onde roda | Usa banco? |
|---|---|---|
| **Teste de integração (DAO)** | H2 ou PostgreSQL | ✅ Sim |
| **Teste unitário (Service)** | Apenas em memória | ❌ Não (usa Mock) |

### Anotações importantes

| Anotação | O que faz |
|---|---|
| `@Test` | Marca o método como um teste a ser executado |
| `@Before` | Executa **antes** de cada teste — usado para preparar dados |
| `@After` | Executa **depois** de cada teste — usado para limpar dados |
| `@Test(expected = X.class)` | Verifica que o teste **lança a exceção esperada** |

---

## 📄 ClienteDAOTest

Testa o DAO de Cliente com banco real. Tem `@After` que limpa todos os clientes após cada teste.

| Teste | O que faz | O que valida |
|---|---|---|
| `pesquisarCliente()` | Insere um cliente e busca pelo CPF | `assertNotNull` — objeto encontrado |
| `salvarCliente()` | Insere e busca pelo CPF | `assertNotNull` — foi salvo |
| `excluirCliente()` | Insere, exclui e busca | `assertNull` — não existe mais |
| `alterarCliente()` | Insere, muda o nome, salva e busca | `assertEquals` — nome foi atualizado |
| `buscarTodos()` | Insere 2 clientes e lista todos | `assertEquals(2, list.size())` — lista tem 2 |

---

## 📄 ProdutoDAOTest

Mesmo padrão do `ClienteDAOTest`, mas para Produto.

| Teste | O que faz | O que valida |
|---|---|---|
| `pesquisar()` | Insere e busca pelo ID | `assertNotNull` |
| `salvar()` | Insere e verifica retorno | `assertNotNull` |
| `excluir()` | Insere, exclui e busca | `assertNull` |
| `alterarProduto()` | Insere, muda o nome e verifica | `assertEquals("Renan", ...)` |
| `buscarTodos()` | Insere 2, verifica lista, exclui e verifica vazia | `assertEquals(2, ...)` e `assertTrue(list.isEmpty())` |

---

## 📄 VendaDAOTest

O mais completo. Usa `@Before` para criar cliente+produto antes de cada teste e `@After` para limpar tudo.

### Configuração

```java
@Before
public void init() {
    this.cliente = cadastrarCliente();       // cria cliente no banco
    this.produto = cadastrarProduto("A1", BigDecimal.TEN); // cria produto de R$10
}

@After
public void end() {
    excluirVendas();
    excluirProdutos();
    clienteDao.excluir(this.cliente);
}
```

### Testes

| Teste | O que faz | O que valida |
|---|---|---|
| `pesquisar()` | Cria venda e busca pelo ID | `assertNotNull` e código igual |
| `salvar()` | Cria venda com 2 produtos de R$10 | Total = R$20 e status `INICIADA` |
| `cancelarVenda()` | Cria e cancela | Status = `CANCELADA` |
| `adicionarMaisProdutosDoMesmo()` | Venda com 2 itens + adiciona +1 do mesmo | Qtd = 3 e total = R$30 |
| `adicionarMaisProdutosDiferentes()` | Venda com 2 itens + adiciona produto de R$50 | Qtd = 3 e total = R$70 |
| `salvarVendaMesmoCodigoExistente()` | Tenta criar 2 vendas com mesmo código | Espera `PersistenceException` |
| `removerProduto()` | Adiciona produto extra e remove | Qtd = 2 e total = R$20 |
| `removerApenasUmProduto()` | Remove 1 unidade de produto com múltiplas | Qtd e valor decrementados |
| `removerTodosProdutos()` | Limpa todos os produtos da venda | Qtd = 0 e total = R$0 |
| `finalizarVenda()` | Finaliza a venda | Status = `CONCLUIDA` |
| `tentarAdicionarProdutosVendaFinalizada()` | Tenta adicionar produto em venda concluída | Espera `UnsupportedOperationException` |

---

## 📄 ClienteServiceTest

Testa a camada **Service** de Cliente **sem banco de dados**, usando `ClienteDaoMock`.

```java
public ClienteServiceTest() {
    IClienteDAO dao = new ClienteDaoMock(); // DAO falso, sem banco
    clienteService = new ClienteService(dao);
}
```

| Teste | O que faz | O que valida |
|---|---|---|
| `pesquisarCliente()` | Chama `buscarPorCPF()` | `assertNotNull` |
| `salvarCliente()` | Chama `cadastrar()` | `assertNotNull` |
| `excluirCliente()` | Chama `excluir()` | Não lança exceção |
| `alterarCliente()` | Chama `alterar()` com nome novo | `assertEquals("Renan", ...)` |

---

## 📄 ProdutoServiceTest

Mesmo padrão do `ClienteServiceTest`, usando `ProdutoDaoMock`.

| Teste | O que faz | O que valida |
|---|---|---|
| `pesquisar()` | Busca produto por ID | `assertNotNull` |
| `salvar()` | Cadastra e verifica retorno | `assertNotNull` |
| `excluir()` | Exclui sem erro | Não lança exceção |
| `alterarCliente()` | Altera nome do produto | `assertEquals("Renan Queiroz", ...)` |

---

## 📄 ClienteDaoMock e ProdutoDaoMock

São **implementações falsas** das interfaces de DAO. Não tocam no banco.

```java
// Não faz INSERT — apenas devolve o objeto recebido
public Cliente cadastrar(Cliente entity) {
    return entity;
}

// Não faz SELECT — cria um objeto vazio com o ID informado
public Cliente consultar(Long id) {
    Cliente cliente = new Cliente();
    cliente.setId(id);
    return cliente;
}
```

**Por que usar Mock?**
- Testes mais rápidos (sem conexão com banco)
- Testam **apenas a lógica da Service**, sem depender de infraestrutura

---

## 📄 AllTests

Agrupa todos os testes em uma suite para rodar de uma só vez:

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ClienteServiceTest.class,
    ClienteDAOTest.class,
    ProdutoServiceTest.class,
    ProdutoDAOTest.class,
    VendaDAOTest.class
})
public class AllTests { }
```

Basta rodar `AllTests` no IntelliJ e todos os testes do projeto são executados juntos.

---

## 📄 testeConexao

Não é um teste JUnit — é uma classe com `main()` para verificar manualmente se a conexão JPA está funcionando.

```java
emf = Persistence.createEntityManagerFactory("sistemaVendas-PU");
em = emf.createEntityManager();
System.out.println("Conexão JPA estabelecida com sucesso!");
```

> ⚠️ Usa a unidade `sistemaVendas-PU` (produção/PostgreSQL). Se rodar a partir da pasta de testes, troque para `sistemaVendas-Test-PU`.

---

## 📄 InserirDadosTest

Teste criado para inserir dados visíveis no pgAdmin. **Não possui `@After`**, então os dados permanecem no banco.

| Teste | O que insere |
|---|---|
| `inserirCliente()` | Um cliente na `TB_CLIENTE` |
| `inserirProduto()` | Um produto na `TB_PRODUTO` |
| `inserirVendaComProduto()` | Cliente + Produto + Venda com 2 itens (4 tabelas preenchidas) |

> ⚠️ Se rodar mais de uma vez sem limpar o banco, vai gerar erro de CPF/código duplicado.
> Limpe com SQL antes de rodar novamente:
> ```sql
> DELETE FROM TB_PRODUTO_QUANTIDADE;
> DELETE FROM TB_VENDA;
> DELETE FROM TB_PRODUTO;
> DELETE FROM TB_CLIENTE;
> ```
