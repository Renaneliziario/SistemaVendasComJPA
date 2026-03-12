/**
 * 
 */
package br.com.renan.dao;

import jakarta.persistence.TypedQuery;

import br.com.renan.dao.generic.GenericDAO;
import br.com.renan.domain.Cliente;
import br.com.renan.exceptions.DAOException;

/**
@author renan.eliziario
 *
 */
public class ClienteDAO extends GenericDAO<Cliente, Long> implements IClienteDAO {

	public ClienteDAO() {
		super(Cliente.class);
	}
	
	public ClienteDAO(String persistenceUnitName) {
		super(Cliente.class, persistenceUnitName);
	}

	@Override
	public Cliente buscarPorCPF(Long cpf) throws DAOException {
		try {
            TypedQuery<Cliente> query = this.entityManager.createQuery("SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class);
            query.setParameter("cpf", cpf);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
	}

}
