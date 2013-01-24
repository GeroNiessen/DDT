package de.codecentric.ddt.configuration;
import java.beans.Transient;
import java.io.File;
import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlRootElement;


@MappedSuperclass
@XmlRootElement
@Entity
public class Resource implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final String reflectionPackageSearchPath = "de.codecentric.ddt";
	
	@Id
	private String name;
	private String url;
	private String username;
	private String password;

	private File workDirectory;
	
	@OneToOne
	private ResourceStrategy strategy;

	public Resource(){
		this.name="";
		this.url="";
		this.username = "";
		this.password = "";
		this.workDirectory = null;
	}
	
	public Resource(Resource otherResource){
		this.name=otherResource.getName();
		this.url=otherResource.getUrl();
		this.username = otherResource.getUsername();
		this.password = otherResource.getPassword();
		this.strategy = otherResource.getStrategy();
		this.workDirectory = otherResource.getWorkDirectory();		
	}
		
	@Transient
	public boolean passesSmokeTest(){
		return strategy.passesSmokeTest(this);
	}

	private String suggestWorkDirectoryPath(){
		String fileSeparator = System.getProperty("file.separator");
		String suggestedWorkDirectoryPath = Configuration.getBaseWorkDirectory().getPath() + fileSeparator + this.name + fileSeparator;
		return suggestedWorkDirectoryPath;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		if(getWorkDirectory().getPath().equals(Configuration.getBaseWorkDirectory().getPath())){
			this.workDirectory = null;
			getWorkDirectory();
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void purgeWorkDirectory(){
		for(File currentFile: getWorkDirectory().listFiles()){
			delete(currentFile);
		}
	}

	private void delete(File f){
		if((f != null) && (f.exists()==true) ){
			if (f.isDirectory()) {
				for (File c : f.listFiles())
					delete(c);
			}
			f.delete();
		}
	}

	@Transient
	public File getWorkDirectory() {
		if ((this.workDirectory == null) || (this.workDirectory.exists()==false)) {
			setWorkDirectory(new File(suggestWorkDirectoryPath()));
		}
		return this.workDirectory;
	}

	@Transient
	public void setWorkDirectory(File workDirectory) {
		//Purge old dir?
		if(!workDirectory.exists()){
			workDirectory.mkdirs();
		}
		this.workDirectory = workDirectory;
	}

	public String getStrategyName(){
		return strategy.getName();
	}

	public ResourceStrategy getStrategy() {
		return strategy;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setStrategy(ResourceStrategy strategy) {
		this.strategy = strategy;
	}
	
	@Transient
	public boolean isStrategyExtending(Class<?> baseStrategy){
		Set<Class<?>> allStrategyClassImplementations = ReflectionHelper.getAllImplementations(reflectionPackageSearchPath, baseStrategy);
		return allStrategyClassImplementations.contains(this.strategy.getClass());
	}
	
	@Transient
	public static Set<Class<?>> getAllRessources(){
		return ReflectionHelper.getAllImplementations(reflectionPackageSearchPath, Resource.class);
	}	

	@Transient
	public static Set<Class<?>> getAllInstanciableRessource(){
		return ReflectionHelper.getAllInstanciableImplementations(reflectionPackageSearchPath, Resource.class);
	}
	
	@Transient
	public static Set<Class<?>> getAllRessourceStrategies(){
		return ReflectionHelper.getAllImplementations(reflectionPackageSearchPath, ResourceStrategy.class);
	}
	
	@Transient
	public static Set<Class<?>> getAllInstanciableRessourceStrategies(){
		return ReflectionHelper.getAllInstanciableImplementations(reflectionPackageSearchPath, ResourceStrategy.class);
	}
			
	@Override
	public String toString(){
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;
		Resource otherResource = (Resource) obj;
		if(getName().equals(otherResource.getName())){
			return true;
		}
		return false;
	}
}