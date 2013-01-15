package de.codecentric.ddt.configuration;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@LocalBean
public class JPAConfigurationDAO implements ConfigurationDAO{
	
	@PersistenceContext
	private EntityManager em;
	
	public JPAConfigurationDAO(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("DDTPU");
		em = emf.createEntityManager();
	}

	@Override
	public void save(Configuration configuration) {
		//em.getTransaction().begin();
		em.merge(configuration);
		//em.flush();
		//em.getTransaction().commit();
	}

	@Override
	public Configuration load() {
		Query q = em.createQuery("SELECT x FROM Configuration x");
		@SuppressWarnings("unchecked")
		List<Configuration> allConfigurations = q.getResultList();
		Configuration retrievedConfiguration = null;
		if(allConfigurations.iterator().hasNext()){
			retrievedConfiguration = allConfigurations.iterator().next();
		}
		return retrievedConfiguration;
	}	
}
