package br.com.renan;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;

import br.com.renan.dao.ClienteDAO;
import br.com.renan.dao.IClienteDAO;
import br.com.renan.dao.IProdutoDAO;
import br.com.renan.dao.IVendaDAO;
import br.com.renan.dao.ProdutoDAO;
import br.com.renan.dao.VendaDAO;
import br.com.renan.domain.Cliente;
import br.com.renan.domain.Produto;
import br.com.renan.domain.Venda;
import br.com.renan.domain.Venda.Status;
import br.com.renan.exceptions.DAOException;

/**
 * Teste de inserção de dados para visualização no pgAdmin.
 * Não possui @After, portanto os dados permanecem no banco após a execução.
 */
public class InserirDadosTest {

    private IClienteDAO clienteDAO = new ClienteDAO("sistemaVendas-Test-PU");
    private IProdutoDAO produtoDAO = new ProdutoDAO("sistemaVendas-Test-PU");
    private IVendaDAO vendaDAO = new VendaDAO("sistemaVendas-Test-PU");

    @Test
    public void inserirCliente() throws DAOException {
        Cliente cliente = new Cliente();
        cliente.setCpf(11122233344L);
        cliente.setNome("João Silva");
        cliente.setCidade("São Paulo");
        cliente.setEnd("Rua das Flores");
        cliente.setEstado("SP");
        cliente.setNumero(100);
        cliente.setTel(11987654321L);
        clienteDAO.cadastrar(cliente);
        System.out.println("Cliente inserido: " + cliente.getNome() + " | ID: " + cliente.getId());
    }

    @Test
    public void inserirProduto() throws DAOException {
        Produto produto = new Produto();
        produto.setCodigo("PROD-01");
        produto.setNome("Notebook");
        produto.setDescricao("Notebook i7 16GB RAM");
        produto.setValor(new BigDecimal("3500.00"));
        produtoDAO.cadastrar(produto);
        System.out.println("Produto inserido: " + produto.getNome() + " | ID: " + produto.getId());
    }

    @Test
    public void inserirVendaComProduto() throws DAOException {
        // Cliente
        Cliente cliente = new Cliente();
        cliente.setCpf(55566677788L);
        cliente.setNome("Maria Souza");
        cliente.setCidade("Campinas");
        cliente.setEnd("Av. Brasil");
        cliente.setEstado("SP");
        cliente.setNumero(200);
        cliente.setTel(19912345678L);
        clienteDAO.cadastrar(cliente);

        // Produto
        Produto produto = new Produto();
        produto.setCodigo("PROD-02");
        produto.setNome("Mouse Gamer");
        produto.setDescricao("Mouse 12000 DPI RGB");
        produto.setValor(new BigDecimal("150.00"));
        produtoDAO.cadastrar(produto);

        // Venda
        Venda venda = new Venda();
        venda.setCodigo("VENDA-001");
        venda.setCliente(cliente);
        venda.setDataVenda(Instant.now());
        venda.setStatus(Status.INICIADA);
        venda.adicionarProduto(produto, 2);
        vendaDAO.cadastrar(venda);

        System.out.println("Venda inserida: " + venda.getCodigo() + " | Total: R$ " + venda.getValorTotal());
    }
}
