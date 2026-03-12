# Integração Angular + Spring Boot — SistemaVendasComJPA

---

## Visão Geral da Arquitetura

```
┌─────────────┐     HTTP (JSON)     ┌──────────────────┐     JPA     ┌────────────┐
│   Angular   │ ──────────────────► │   Spring Boot    │ ──────────► │ PostgreSQL │
│  (Front-end)│ ◄────────────────── │  (Back-end/API)  │ ◄────────── │            │
└─────────────┘                     └──────────────────┘             └────────────┘
```

O seu projeto Java já possui **Domain + DAO + Service** prontos.  
Falta apenas adicionar a camada **Controller (API REST)** com Spring Boot.

---

## Etapa 1 — Adicionar Spring Boot ao projeto Java

### 1.1 Atualizar o `pom.xml`

Adicione as dependências do Spring Boot:

```xml
<!-- Spring Boot Starter Web (API REST) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- Spring Boot Starter Data JPA (substitui a configuração manual do JPA) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.2.0</version>
</dependency>
```

### 1.2 Criar a classe principal

```java
@SpringBootApplication
public class SistemaVendasApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaVendasApplication.class, args);
    }
}
```

### 1.3 Configurar o banco no `application.properties`

Crie o arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vendas_online_2
spring.datasource.username=renan
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Etapa 2 — Criar os Controllers (API REST)

Os Controllers recebem as requisições HTTP e delegam para os Services existentes.

### ClienteController

```java
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200") // permite o Angular acessar
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    // POST /api/clientes — cadastrar
    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente) throws DAOException {
        Cliente salvo = clienteService.cadastrar(cliente);
        return ResponseEntity.status(201).body(salvo);
    }

    // GET /api/clientes/{id} — buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(@PathVariable Long id) throws DAOException {
        Cliente cliente = clienteService.consultar(id);
        return ResponseEntity.ok(cliente);
    }

    // GET /api/clientes/cpf/{cpf} — buscar por CPF
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable Long cpf) throws DAOException {
        Cliente cliente = clienteService.buscarPorCPF(cpf);
        return ResponseEntity.ok(cliente);
    }

    // GET /api/clientes — listar todos
    @GetMapping
    public ResponseEntity<Collection<Cliente>> listarTodos() throws DAOException {
        return ResponseEntity.ok(clienteService.buscarTodos());
    }

    // PUT /api/clientes — alterar
    @PutMapping
    public ResponseEntity<Cliente> alterar(@RequestBody Cliente cliente) throws DAOException {
        return ResponseEntity.ok(clienteService.alterar(cliente));
    }

    // DELETE /api/clientes — excluir
    @DeleteMapping
    public ResponseEntity<Void> excluir(@RequestBody Cliente cliente) throws DAOException {
        clienteService.excluir(cliente);
        return ResponseEntity.noContent().build();
    }
}
```

### ProdutoController

```java
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdutoController {

    @Autowired
    private IProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody Produto produto) throws DAOException {
        return ResponseEntity.status(201).body(produtoService.cadastrar(produto));
    }

    @GetMapping
    public ResponseEntity<Collection<Produto>> listarTodos() throws DAOException {
        return ResponseEntity.ok(produtoService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscar(@PathVariable Long id) throws DAOException {
        return ResponseEntity.ok(produtoService.consultar(id));
    }
}
```

---

## Etapa 3 — Criar o projeto Angular

### 3.1 Pré-requisitos

```bash
# Instalar Node.js (https://nodejs.org)
# Depois instalar o Angular CLI
npm install -g @angular/cli

# Criar o projeto
ng new sistema-vendas-frontend
cd sistema-vendas-frontend

# Iniciar o servidor de desenvolvimento
ng serve
```
O Angular estará disponível em `http://localhost:4200`.

---

### 3.2 Criar os modelos (interfaces TypeScript)

```typescript
// src/app/models/cliente.model.ts
export interface Cliente {
  id?: number;
  nome: string;
  cpf: number;
  tel: number;
  end: string;
  numero: number;
  cidade: string;
  estado: string;
}
```

```typescript
// src/app/models/produto.model.ts
export interface Produto {
  id?: number;
  codigo: string;
  nome: string;
  descricao: string;
  valor: number;
}
```

---

### 3.3 Criar os Services Angular (comunicação com a API)

```typescript
// src/app/services/cliente.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {

  private apiUrl = 'http://localhost:8080/api/clientes';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  cadastrar(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  alterar(cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(this.apiUrl, cliente);
  }

  excluir(cliente: Cliente): Observable<void> {
    return this.http.delete<void>(this.apiUrl, { body: cliente });
  }
}
```

---

### 3.4 Criar o componente de cadastro de Cliente

```typescript
// src/app/components/cliente/cliente.component.ts
import { Component } from '@angular/core';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-cliente',
  templateUrl: './cliente.component.html'
})
export class ClienteComponent {

  clientes: Cliente[] = [];

  novoCliente: Cliente = {
    nome: '', cpf: 0, tel: 0,
    end: '', numero: 0, cidade: '', estado: ''
  };

  constructor(private clienteService: ClienteService) {
    this.carregarClientes();
  }

  carregarClientes() {
    this.clienteService.listarTodos().subscribe(
      (lista) => this.clientes = lista
    );
  }

  cadastrar() {
    this.clienteService.cadastrar(this.novoCliente).subscribe(
      () => {
        alert('Cliente cadastrado com sucesso!');
        this.carregarClientes();
      }
    );
  }
}
```

```html
<!-- src/app/components/cliente/cliente.component.html -->
<h2>Cadastro de Cliente</h2>

<form (ngSubmit)="cadastrar()">
  <input [(ngModel)]="novoCliente.nome"   name="nome"   placeholder="Nome"    required />
  <input [(ngModel)]="novoCliente.cpf"    name="cpf"    placeholder="CPF"     required />
  <input [(ngModel)]="novoCliente.tel"    name="tel"    placeholder="Telefone" required />
  <input [(ngModel)]="novoCliente.end"    name="end"    placeholder="Endereço" required />
  <input [(ngModel)]="novoCliente.numero" name="numero" placeholder="Número"  required />
  <input [(ngModel)]="novoCliente.cidade" name="cidade" placeholder="Cidade"  required />
  <input [(ngModel)]="novoCliente.estado" name="estado" placeholder="UF"      required maxlength="2" />
  <button type="submit">Cadastrar</button>
</form>

<h3>Clientes cadastrados</h3>
<table>
  <tr>
    <th>ID</th><th>Nome</th><th>CPF</th><th>Cidade</th><th>Estado</th>
  </tr>
  <tr *ngFor="let c of clientes">
    <td>{{ c.id }}</td>
    <td>{{ c.nome }}</td>
    <td>{{ c.cpf }}</td>
    <td>{{ c.cidade }}</td>
    <td>{{ c.estado }}</td>
  </tr>
</table>
```

---

### 3.5 Habilitar o HttpClient no Angular

```typescript
// src/app/app.module.ts
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    HttpClientModule,  // necessário para fazer requisições HTTP
    FormsModule        // necessário para [(ngModel)]
  ]
})
export class AppModule { }
```

---

## Fluxo completo de uma requisição

```
Usuário preenche o formulário no Angular
        ↓
clienteService.cadastrar(novoCliente)
        ↓
HTTP POST → http://localhost:8080/api/clientes
        ↓
ClienteController.cadastrar(@RequestBody Cliente)
        ↓
clienteService.cadastrar(cliente)
        ↓
clienteDAO.cadastrar(cliente)
        ↓
entityManager.persist(cliente)
        ↓
INSERT INTO TB_CLIENTE ... (PostgreSQL)
        ↓
Retorna o cliente salvo com ID gerado
        ↓
Angular exibe "Cliente cadastrado com sucesso!"
```

---

## Ordem de aprendizado recomendada

```
1. Spring Boot Básico
   → @SpringBootApplication, application.properties

2. REST com Spring MVC
   → @RestController, @GetMapping, @PostMapping, @RequestBody, @PathVariable

3. Angular Básico
   → Components, Templates, Data Binding ([(ngModel)])

4. Angular Services + HttpClient
   → Comunicação com API, Observable, subscribe()

5. Integração completa
   → CORS, tratar erros, variáveis de ambiente
```

---

## Comandos úteis

```bash
# Rodar o back-end Spring Boot
mvn spring-boot:run

# Criar um novo componente Angular
ng generate component components/cliente

# Criar um novo service Angular
ng generate service services/cliente

# Rodar o front-end Angular
ng serve
```
