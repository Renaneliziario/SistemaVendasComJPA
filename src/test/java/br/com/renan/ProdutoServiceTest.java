package br.com.renan;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.renan.dao.IProdutoDAO;
import br.com.renan.dao.ProdutoDaoMock;
import br.com.renan.domain.Produto;
import br.com.renan.exceptions.DAOException;
import br.com.renan.services.IProdutoService;
import br.com.renan.services.ProdutoService;

/**
@author renan.eliziario
 *
 */
public class ProdutoServiceTest {

	private IProdutoService produtoService;
	
	private Produto produto;

	public ProdutoServiceTest() {
	IProdutoDAO dao = new ProdutoDaoMock();
		produtoService = new ProdutoService(dao);
	}
	
	@Before
	public void init() {
		produto = new Produto();
		produto.setId(1L);
		produto.setCodigo("A1");
		produto.setDescricao("Produto 1");
		produto.setNome("Produto 1");
		produto.setValor(BigDecimal.TEN);
	}
	
	@Test
	public void pesquisar() throws DAOException {
		Produto produtor = this.produtoService.consultar(produto.getId());
		Assert.assertNotNull(produtor);
	}
	
	@Test
	public void salvar() throws DAOException {
		Produto retorno = produtoService.cadastrar(produto);
		Assert.assertNotNull(retorno);
	}
	
	@Test
	public void excluir() throws DAOException {
		produtoService.excluir(produto);
	}	


	
	@Test
	public void alterarCliente() throws DAOException {
		produto.setNome("Renan Queiroz");
		Produto retorno = produtoService.alterar(produto);
		
		Assert.assertEquals("Renan Queiroz", retorno.getNome());
	
	}

}

