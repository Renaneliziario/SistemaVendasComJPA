package br.com.renan.dao.generic;

import java.io.Serializable;
import java.util.Collection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import br.com.renan.exceptions.DAOException;

/**
@author renan.eliziario
 *
 * Classe genérica que implementa interface genérica com os métodos de CRUD
 */
public class GenericDAO<T, E extends Serializable> implements IGenericDAO<T,E> {

    private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "sistemaVendas-PU";
    
    protected EntityManagerFactory entityManagerFactory;
    
    protected EntityManager entityManager;
    
    private Class<T> persistentClass;

    public GenericDAO(Class<T> persistentClass) {
        this(persistentClass, DEFAULT_PERSISTENCE_UNIT_NAME);
    }

    public GenericDAO(Class<T> persistentClass, String persistenceUnitName) {
        this.persistentClass = persistentClass;
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public T cadastrar(T entity) throws DAOException {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        return entity;
    }


    @Override
    public void excluir(T entity) throws DAOException {
        entityManager.getTransaction().begin();
        entity = entityManager.merge(entity);
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    @Override
    public T alterar(T entity) throws DAOException {
        entityManager.getTransaction().begin();
        entity = entityManager.merge(entity);
        entityManager.getTransaction().commit();
        return entity;
    }

    @Override
    public T consultar(E id) throws DAOException {
        return entityManager.find(persistentClass, id);
    }
    
    @Override
    public Collection<T> buscarTodos() throws DAOException {
        // Java 21: var infere o tipo List<T> automaticamente
        var list = entityManager.createQuery(
                "SELECT e FROM " + this.persistentClass.getSimpleName() + " e", this.persistentClass)
                .getResultList();
        return list;
    }
}
