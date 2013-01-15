/*
package de.codecentric.ddt.web;

import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.codecentric.ddt.configuration.Application;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.resourcestrategies.databases.Database;
import de.codecentric.ddt.resourcestrategies.databases.OracleDatabaseStrategy;
import de.codecentric.ddt.resourcestrategies.issuetrackers.IssueTracker;
import de.codecentric.ddt.resourcestrategies.issuetrackers.JiraIssueTrackerStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.MercurialRepositoryStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.Repository;

/**
 * @author Gero Niessen


@Stateless
@LocalBean
public class ApplicationDAO {

	@PersistenceContext
	private EntityManager em;// = Persistence.createEntityManagerFactory("DDTPU").createEntityManager();

	public ApplicationDAO(){
	}
	
	public Application save(Application applicationEntity){
		return em.merge(applicationEntity);
	}

	public void remove(Application applicationEntity){
		em.remove(applicationEntity);
	}

	public Application getApplication(String applicationName){
		Query q = em.createQuery("SELECT x FROM Application x WHERE name=" + applicationName);
		Application returnedApplicationEntity = (Application) q.getSingleResult();
		return returnedApplicationEntity;
	}

	public Application getApplication(Long applicationId){
		return em.find(Application.class, applicationId);
	}    

	public List<Application> getAllApplications(){
		Query q = em.createQuery("SELECT x FROM Application x");
		List<Application> resultList = q.getResultList();
		List<Application> returnedApplications = resultList; 
		return  returnedApplications;
	}

	public void generateTestData(){
		Application icisPlusApplication = new Application();
		icisPlusApplication.setName("ICIS_PLUS");

		Repository icisPlusRepository = new Repository(); 
		icisPlusRepository.setName("ICIS_Mercurial_Repository");
		icisPlusRepository.setUrl("http://wgvli39.swlabor.local:8282");
		icisPlusRepository.setStrategy(new MercurialRepositoryStrategy());

		IssueTracker icisPlusIssueTracker = new IssueTracker();
		icisPlusIssueTracker.setName("ICIS_Jira_IssueTracker");
		icisPlusIssueTracker.setUrl("http://wgvli39.swlabor.local:8181");
		icisPlusIssueTracker.setStrategy(new JiraIssueTrackerStrategy());

		Database icisDatabase = new Database();
		icisDatabase.setName("ICIS_Oracle_Database");
		icisDatabase.setUrl("jdbc:oracle:thin:@wgvli36.swlabor.local:1522:ICISPLUS");
		icisDatabase.setStrategy(new OracleDatabaseStrategy());

		//---------------------             
		icisPlusApplication.setResources(new HashSet<Resource<?>>());
		icisPlusApplication.getResources().add(icisPlusIssueTracker);
		icisPlusApplication.getResources().add(icisPlusRepository);
		icisPlusApplication.getResources().add(icisDatabase);
		save(icisPlusApplication);
		//---------------------

		//---------------------
		Application secondTestApplication = new Application();
		secondTestApplication.setName("SecondTestApplication");
		//---------------------
		IssueTracker firstIssueTracker = new IssueTracker();
		firstIssueTracker.setName("FirstJiraIssueTrackerForFirstProject");
		firstIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		firstIssueTracker.setUrl("http://www.example.org");

		IssueTracker secondIssueTracker = new IssueTracker();
		secondIssueTracker.setName("FirstJiraIssueTrackerForSecondApplication");
		secondIssueTracker.setUrl("http://www.beispiel.de");
		secondIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		secondTestApplication.setResources(new HashSet<Resource<?>>());
		secondTestApplication.getResources().add(firstIssueTracker);
		secondTestApplication.getResources().add(secondIssueTracker);
		save(secondTestApplication);
	}
}
*/