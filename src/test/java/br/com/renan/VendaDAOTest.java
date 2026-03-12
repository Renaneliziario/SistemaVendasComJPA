
package br.com.renan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collection;

import jakarta.persistence.PersistenceException;

import org.junit.After;
import org.junit.Before;
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
import br.com.renan.domain.VendaResumo;
import br.com.renan.exceptions.DAOException;
import br.com.renan.exceptions.TipoChaveNaoEncontradaException;

public class VendaDAOTest {
	
	private IVendaDAO vendaDao;
	
	private IClienteDAO clienteDao;
	
	private IProdutoDAO produtoDao;

	private Cliente cliente;
	
	private Produto produto;
	
	public VendaDAOTest() {
		vendaDao = new VendaDAO("sistemaVendas-Test-PU");
		clienteDao = new ClienteDAO("sistemaVendas-Test-PU");
		produtoDao = new ProdutoDAO("sistemaVendas-Test-PU");
	}
	
	@Before
	public void init() throws TipoChaveNaoEncontradaException, DAOException {
		this.cliente = cadastrarCliente();
		this.produto = cadastrarProduto("A1", BigDecimal.TEN);
	}
	
	@After
	public void end() throws DAOException {
		excluirVendas();
		excluirProdutos();
		clienteDao.excluir(this.cliente);
	}
	

	private void excluirProdutos() throws DAOException {
		Collection<Produto> list = this.produtoDao.buscarTodos();
		for (Produto prod : list) {
			this.produtoDao.excluir(prod);
		}
	}

	@Test
	public void pesquisar() throws TipoChaveNaoEncontradaException, DAOException {
		Venda venda = criarVenda("A1");
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		assertNotNull(vendaConsultada);
		assertEquals(venda.getCodigo(), vendaConsultada.getCodigo());
	}
	
	@Test
	public void salvar() throws TipoChaveNaoEncontradaException, DAOException {
		Venda venda = criarVenda("A2");
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		
		assertTrue(venda.getValorTotal().compareTo(BigDecimal.valueOf(20)) == 0);
		assertEquals(Status.INICIADA, venda.getStatus());
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		assertNotNull(vendaConsultada.getId());
		assertEquals(venda.getCodigo(), vendaConsultada.getCodigo());
	} 
	
	
	@Test
	public void cancelarVenda() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A3";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		vendaDao.cancelarVenda(venda);
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		assertEquals(codigoVenda, vendaConsultada.getCodigo());
		assertEquals(Status.CANCELADA, vendaConsultada.getStatus());
	}
	
	@Test
	public void adicionarMaisProdutosDoMesmo() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A4";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		vendaConsultada.adicionarProduto(produto, 1);
		
		assertEquals(3, (int) vendaConsultada.getQuantidadeTotalProdutos());
		BigDecimal valorTotal = BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada.getStatus());
		
		vendaDao.alterar(vendaConsultada);
		
		Venda vendaConsultada2 = vendaDao.consultar(venda.getId());
		assertEquals(3, (int) vendaConsultada2.getQuantidadeTotalProdutos());
		assertTrue(valorTotal.compareTo(vendaConsultada2.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada2.getStatus());
	} 
	
	@Test
	public void adicionarMaisProdutosDiferentes() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A5";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		Produto prod = cadastrarProduto(codigoVenda, BigDecimal.valueOf(50));
		assertNotNull(prod);
		assertEquals(codigoVenda, prod.getCodigo());
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		vendaConsultada.adicionarProduto(prod, 1);
		
		assertEquals(3, (int) vendaConsultada.getQuantidadeTotalProdutos());
		BigDecimal valorTotal = BigDecimal.valueOf(70).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada.getStatus());
		
		vendaDao.alterar(vendaConsultada);
		
		Venda vendaConsultada2 = vendaDao.consultar(venda.getId());
		assertEquals(3, (int) vendaConsultada2.getQuantidadeTotalProdutos());
		assertTrue(valorTotal.compareTo(vendaConsultada2.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada2.getStatus());
	} 
	
	@Test(expected = PersistenceException.class)
	public void salvarVendaMesmoCodigoExistente() throws TipoChaveNaoEncontradaException, DAOException {
		Venda venda = criarVenda("A6");
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
	
		Venda venda2 = criarVenda("A6");
		Venda retorno1 = vendaDao.cadastrar(venda2);
		assertNotNull(retorno1);
		assertEquals(Status.INICIADA, venda.getStatus());
	} 
	
	@Test
	public void removerProduto() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A7";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		Produto prod = cadastrarProduto(codigoVenda, BigDecimal.valueOf(50));
		assertNotNull(prod);
		assertEquals(codigoVenda, prod.getCodigo());
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		vendaConsultada.adicionarProduto(prod, 1);
		assertEquals(3, (int) vendaConsultada.getQuantidadeTotalProdutos());
		BigDecimal valorTotal = BigDecimal.valueOf(70).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		
		
		vendaConsultada.removerProduto(prod, 1);
		assertEquals(2, (int) vendaConsultada.getQuantidadeTotalProdutos());
		valorTotal = BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada.getStatus());
		
		vendaDao.alterar(vendaConsultada);
		
		Venda vendaConsultada2 = vendaDao.consultar(venda.getId());
		assertEquals(2, (int) vendaConsultada2.getQuantidadeTotalProdutos());
		assertTrue(valorTotal.compareTo(vendaConsultada2.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada2.getStatus());
	} 
	
	@Test
	public void removerApenasUmProduto() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A8";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		Produto prod = cadastrarProduto(codigoVenda, BigDecimal.valueOf(50));
		assertNotNull(prod);
		assertEquals(codigoVenda, prod.getCodigo());
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		vendaConsultada.adicionarProduto(prod, 1);
		assertEquals(3, (int) vendaConsultada.getQuantidadeTotalProdutos());
		BigDecimal valorTotal = BigDecimal.valueOf(70).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		
		
		vendaConsultada.removerProduto(prod, 1);
		assertEquals(2, (int) vendaConsultada.getQuantidadeTotalProdutos());
		valorTotal = BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada.getStatus());
		
		vendaDao.alterar(vendaConsultada);
		
		Venda vendaConsultada2 = vendaDao.consultar(venda.getId());
		assertEquals(2, (int) vendaConsultada2.getQuantidadeTotalProdutos());
		assertTrue(valorTotal.compareTo(vendaConsultada2.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada2.getStatus());
	} 
	
	@Test
	public void removerTodosProdutos() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A9";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		Produto prod = cadastrarProduto(codigoVenda, BigDecimal.valueOf(50));
		assertNotNull(prod);
		assertEquals(codigoVenda, prod.getCodigo());
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		vendaConsultada.adicionarProduto(prod, 1);
		assertEquals(3, (int) vendaConsultada.getQuantidadeTotalProdutos());
		BigDecimal valorTotal = BigDecimal.valueOf(70).setScale(2, RoundingMode.HALF_DOWN);
		assertTrue(valorTotal.compareTo(vendaConsultada.getValorTotal()) == 0);
		
		
		vendaConsultada.removerTodosProdutos();
		assertEquals(0, (int) vendaConsultada.getQuantidadeTotalProdutos());
		assertTrue(BigDecimal.ZERO.compareTo(vendaConsultada.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada.getStatus());
		
		vendaDao.alterar(vendaConsultada);
		
		Venda vendaConsultada2 = vendaDao.consultar(venda.getId());
		assertEquals(0, (int) vendaConsultada2.getQuantidadeTotalProdutos());
		assertTrue(BigDecimal.ZERO.compareTo(vendaConsultada2.getValorTotal()) == 0);
		assertEquals(Status.INICIADA, vendaConsultada2.getStatus());
	} 
	
	@Test
	public void converterParaResumo() throws TipoChaveNaoEncontradaException, DAOException {
		// Java 21 — Record: VendaResumo é um DTO imutável gerado a partir de uma Venda
		Venda venda = criarVenda("A12");
		vendaDao.cadastrar(venda);

		Venda vendaConsultada = vendaDao.consultar(venda.getId());

		// fromVenda() cria o record com os dados relevantes
		var resumo = VendaResumo.fromVenda(vendaConsultada);

		// getters são os próprios nomes dos campos do record (sem "get")
		assertNotNull(resumo);
		assertEquals("A12", resumo.codigo());
		assertEquals("Renan", resumo.nomeCliente());
		assertEquals(Status.INICIADA, resumo.status());
		assertTrue(resumo.valorTotal().compareTo(BigDecimal.valueOf(20)) == 0);
		assertEquals(2, resumo.totalProdutos());

		// toString() gerado automaticamente — útil para logs
		System.out.println("Resumo: " + resumo);
	}

	@Test
	public void finalizarVenda() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A10";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		vendaDao.finalizarVenda(venda);
		
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		assertEquals(venda.getCodigo(), vendaConsultada.getCodigo());
		assertEquals(Status.CONCLUIDA, vendaConsultada.getStatus());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void tentarAdicionarProdutosVendaFinalizada() throws TipoChaveNaoEncontradaException, DAOException {
		String codigoVenda = "A11";
		Venda venda = criarVenda(codigoVenda);
		Venda retorno = vendaDao.cadastrar(venda);
		assertNotNull(retorno);
		assertNotNull(venda);
		assertEquals(codigoVenda, venda.getCodigo());
		
		vendaDao.finalizarVenda(venda);
		Venda vendaConsultada = vendaDao.consultar(venda.getId());
		assertEquals(venda.getCodigo(), vendaConsultada.getCodigo());
		assertEquals(Status.CONCLUIDA, vendaConsultada.getStatus());
		
		vendaConsultada.adicionarProduto(this.produto, 1);
		
	}

	private Produto cadastrarProduto(String codigo, BigDecimal valor) throws TipoChaveNaoEncontradaException, DAOException {
		Produto produto = new Produto();
		produto.setCodigo(codigo);
		produto.setDescricao("Produto 1");
		produto.setNome("Produto 1");
		produto.setValor(valor);
		return produtoDao.cadastrar(produto);
	}

	private Cliente cadastrarCliente() throws TipoChaveNaoEncontradaException, DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(12312312312L);
		cliente.setNome("Renan");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		return clienteDao.cadastrar(cliente);
	}
	
	private Venda criarVenda(String codigo) {
		Venda venda = new Venda();
		venda.setCodigo(codigo);
		venda.setDataVenda(Instant.now());
		venda.setCliente(this.cliente);
		venda.setStatus(Status.INICIADA);
		venda.adicionarProduto(this.produto, 2);
		return venda;
	}
	
	private void excluirVendas() throws DAOException {
		Collection<Venda> list = this.vendaDao.buscarTodos();
		for (Venda venda : list) {
			this.vendaDao.excluir(venda);
		}
	}
}