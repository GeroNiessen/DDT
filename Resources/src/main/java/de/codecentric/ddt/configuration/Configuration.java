package de.codecentric.ddt.configuration;

import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@Entity
public class Configuration implements Serializable{

	private static final long serialVersionUID = -2128961018429921607L;
	private static final String configurationDAOPropertiesFile = "ConfigurationDAO.properties";
	private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Configuration.class .getName());
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToMany
	private Set<de.codecentric.ddt.configuration.Application> applications;
	
	public Configuration(){
		this.applications = new HashSet<>();
	}

	public Set<de.codecentric.ddt.configuration.Application> getApplications() {
		return applications;
	}

	public void setApplications(Set<de.codecentric.ddt.configuration.Application> applications) {
		this.applications = applications;
	}
	
	public static File getBaseWorkDirectory(){
		String userHomePath = System.getProperty("user.home");
		String fileSeparator = System.getProperty("file.separator");
		String baseWorkDirectory = userHomePath + fileSeparator + ".DDT_WORK_DIR";
		File baseWorkDirectoryFolder = new File(baseWorkDirectory);
		if(!baseWorkDirectoryFolder.exists()){
			baseWorkDirectoryFolder.mkdirs();
		}
		return baseWorkDirectoryFolder;
	}
	
	@Transient
	public static ConfigurationDAO getConfigurationDAO(){
		ConfigurationDAO configurationDAO;
		Properties configurationDAOConfiguration = new Properties();
		try {
			configurationDAOConfiguration.load(Configuration.class.getClassLoader().getResourceAsStream(configurationDAOPropertiesFile));
			String configurationDAOClass = configurationDAOConfiguration.getProperty("ConfigurationDAOImplementation");
			Class<?> c = Class.forName(configurationDAOClass);
			configurationDAO = (ConfigurationDAO) c.newInstance();
		} catch (IOException|ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			configurationDAO = new XMLConfigurationDAO();
			LOGGER.warning("Failed to load ConfigurationDAO implementation from file:" + configurationDAOPropertiesFile + "\n Using XMLConfigurationDAO as default!");
			e.printStackTrace();
		}
		return configurationDAO;
	}
	
	/*
	public Configuration generateTestConfiguration(){

		Configuration configuration = new Configuration();

		de.codecentric.ddt.configuration.Application icisPlusApplication = new de.codecentric.ddt.configuration.Application();
		icisPlusApplication.setName("ICIS_PLUS");

		//Repository
		Resource icisPlusRepository = new Resource(); 
		icisPlusRepository.setName("ICIS_Mercurial_Repository");
		icisPlusRepository.setUrl("http://wgvli39.swlabor.local:8282");
		icisPlusRepository.setStrategy(new MercurialRepositoryStrategy());

		Resource icisPlusIssueTracker = new Resource();
		icisPlusIssueTracker.setName("ICIS_Jira_IssueTracker");
		icisPlusIssueTracker.setUrl("http://wgvli39.swlabor.local:8181");
		icisPlusIssueTracker.setStrategy(new JiraIssueTrackerStrategy());

		Resource icisDatabase = new Resource();
		icisDatabase.setName("ICIS_Oracle_Database");
		icisDatabase.setUrl("jdbc:oracle:thin:@wgvli36.swlabor.local:1522:ICISPLUS");
		icisDatabase.setStrategy(new OracleDatabaseStrategy());

		//---------------------             
		icisPlusApplication.setResources(new HashSet<Resource>());
		icisPlusApplication.getResources().add(icisPlusIssueTracker);
		icisPlusApplication.getResources().add(icisPlusRepository);
		icisPlusApplication.getResources().add(icisDatabase);

		//---------------------

		//---------------------
		de.codecentric.ddt.configuration.Application secondTestApplication = new de.codecentric.ddt.configuration.Application();
		secondTestApplication.setName("SecondTestApplication");
		//---------------------
		Resource firstIssueTracker = new Resource();
		firstIssueTracker.setName("FirstJiraIssueTrackerForFirstProject");
		firstIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		firstIssueTracker.setUrl("http://www.example.org");

		Resource secondIssueTracker = new Resource();
		secondIssueTracker.setName("FirstJiraIssueTrackerForSecondApplication");
		secondIssueTracker.setUrl("http://www.beispiel.de");
		secondIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		secondTestApplication.setResources(new HashSet<Resource>());
		secondTestApplication.getResources().add(firstIssueTracker);
		secondTestApplication.getResources().add(secondIssueTracker);

		configuration.getApplications().add(secondTestApplication);
		configuration.getApplications().add(icisPlusApplication);
		return configuration;
	}
	*/
}
