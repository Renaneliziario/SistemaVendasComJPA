package br.com.renan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.renan.dao.ClienteDAO;
import br.com.renan.dao.IClienteDAO;
import br.com.renan.domain.Cliente;
import br.com.renan.exceptions.DAOException;

/**
@author renan.eliziario
 *
 */
public class ClienteDAOTest {
	
	private IClienteDAO clienteDao;

	@Before
	public void setUp() {
		clienteDao = new ClienteDAO("sistemaVendas-Test-PU");
	}
	
	@After
	public void tearDown() throws DAOException {
		Collection<Cliente> list = clienteDao.buscarTodos();
		list.forEach(cli -> {
			try {
				clienteDao.excluir(cli);
			} catch (DAOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Test
	public void pesquisarCliente() throws DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(12312312312L);
		cliente.setNome("Renan");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		clienteDao.cadastrar(cliente);
		
		Cliente clienteConsultado = clienteDao.buscarPorCPF(cliente.getCpf());
		assertNotNull(clienteConsultado);
	}
	
	@Test
	public void salvarCliente() throws DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(56565656565L);
		cliente.setNome("Renan");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		Cliente retorno = clienteDao.cadastrar(cliente);
		assertNotNull(retorno);
		
		Cliente clienteConsultado = clienteDao.buscarPorCPF(retorno.getCpf());
		assertNotNull(clienteConsultado);
	}
	
	
	@Test
	public void excluirCliente() throws DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(10203040506L);
		cliente.setNome("Renan");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		Cliente retorno = clienteDao.cadastrar(cliente);
		assertNotNull(retorno);
		
		clienteDao.excluir(retorno);
		
		Cliente clienteConsultado = clienteDao.buscarPorCPF(retorno.getCpf());
		assertNull(clienteConsultado);
	}
	
	@Test
	public void alterarCliente() throws DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(70809010011L);
		cliente.setNome("Renan");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		Cliente retorno = clienteDao.cadastrar(cliente);
		assertNotNull(retorno);
		
		retorno.setNome("Rodrigo Pires");
		clienteDao.alterar(retorno);
		
		Cliente clienteAlterado = clienteDao.buscarPorCPF(retorno.getCpf());
		assertNotNull(clienteAlterado);
		assertEquals("Renan", clienteAlterado.getNome());
	}
	
	@Test
	public void buscarTodos() throws DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(22334455667L);
		cliente.setNome("Renan");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		clienteDao.cadastrar(cliente);
		
		Cliente cliente1 = new Cliente();
		cliente1.setCpf(88990011223L);
		cliente1.setNome("Renan");
		cliente1.setCidade("São Paulo");
		cliente1.setEnd("End");
		cliente1.setEstado("SP");
		cliente1.setNumero(10);
		cliente1.setTel(1199999999L);
		clienteDao.cadastrar(cliente1);
		
		Collection<Cliente> list = clienteDao.buscarTodos();
		assertNotNull(list);
		assertEquals(2, list.size());
	}
}