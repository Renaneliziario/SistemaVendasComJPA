package br.com.renan.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import br.com.renan.dao.generic.GenericDAO;
import br.com.renan.domain.Venda;
import br.com.renan.domain.Venda.Status;
import br.com.renan.exceptions.DAOException;

/**
@author renan.eliziario
 *
 */
public class VendaDAO extends GenericDAO<Venda, Long> implements IVendaDAO {

	public VendaDAO() {
		super(Venda.class);
	}
	
	public VendaDAO(String persistenceUnitName) {
		super(Venda.class, persistenceUnitName);
	}

	@Override
	public void finalizarVenda(Venda venda) throws DAOException {
		venda.setStatus(Status.CONCLUIDA);
		alterar(venda);
	}

	@Override
	public void cancelarVenda(Venda venda) throws DAOException {
		venda.setStatus(Status.CANCELADA);
		alterar(venda);
	}

	@Override
	public Venda cadastrar(Venda entity) throws DAOException {
		entity.getProdutos().forEach(prod -> prod.setVenda(entity));
		return super.cadastrar(entity);
	}

	@Override
	public Venda consultar(Long id) throws DAOException {
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Venda> query = builder.createQuery(Venda.class);
			Root<Venda> root = query.from(Venda.class);
			root.fetch("cliente", JoinType.LEFT);
			root.fetch("produtos", JoinType.LEFT);
			query.select(root).where(builder.equal(root.get("id"), id));
			TypedQuery<Venda> tpQuery = 
					entityManager.createQuery(query);
			Venda venda = tpQuery.getSingleResult(); 
			venda.recalcularValorTotalVenda();
			return venda;
		} catch (NoResultException e) {
			return null;
		}
	}
}
