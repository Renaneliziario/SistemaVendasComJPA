package br.com.renan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class testeConexao {
	public static void main(String [] args) {
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory("sistemaVendas-Test-PU");
			em = emf.createEntityManager();
			System.out.println("Conexão JPA estabelecida com sucesso!");
		} catch (Exception e) {
			System.err.println("Erro ao estabelecer conexão JPA: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (em != null) {
				em.close();
			}
			if (emf != null) {
				emf.close();
			}
		}
	}

}
