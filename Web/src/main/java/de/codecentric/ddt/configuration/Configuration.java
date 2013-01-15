package de.codecentric.ddt.configuration;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
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
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToMany
	private Set<de.codecentric.ddt.configuration.Application> applications;
	
	public Configuration(){
		this.applications = new HashSet<de.codecentric.ddt.configuration.Application>();
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
}
