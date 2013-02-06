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

/**
 * Resource is a basic representation of a resource in the real world in order to get information and process commands
 * @author Gero Niessen
 */
//@MappedSuperclass
@XmlRootElement
@Entity
public class Resource implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final String reflectionPackageSearchPath = "de.codecentric.ddt";
		
        @Id
	protected String name;
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
	
        /**
         * Checks if a resource can be reached.
         * @return 
         */
	@Transient
	public boolean passesSmokeTest(){
		return strategy.passesSmokeTest(this);
	}
        
        /**
         * Suggests a work directory for a resource, based on the name of the resource
         * @return 
         */
	private String suggestWorkDirectoryPath(){
		String fileSeparator = System.getProperty("file.separator");
		String suggestedWorkDirectoryPath = Configuration.getBaseWorkDirectory().getPath() + fileSeparator + this.name + fileSeparator;
		return suggestedWorkDirectoryPath;
	}

        /**
         * The unique name (description) of the resource
         * @return 
         */
        public String getName() {
		return name;
	}
	
        /**
         * Sets the unique name (description) of the resource
         * @param name 
         */
	public void setName(String name) {
		this.name = name;
		if(getWorkDirectory().getPath().equals(Configuration.getBaseWorkDirectory().getPath())){
			this.workDirectory = null;
			getWorkDirectory();
		}
	}

        /**
         * Gets the URL of the resource.
         * @return 
         */
	public String getUrl() {
		return url;
	}

        /**
         * Sets the URL of the resource 
         * @param url 
         */
	public void setUrl(String url) {
		this.url = url;
	}

        /**
         * Cleans the work-directory of the resource
         */
        public void purgeWorkDirectory(){
            if(!getWorkDirectory().getPath().equals(suggestWorkDirectoryPath())){
		for(File currentFile: getWorkDirectory().listFiles()){
			delete(currentFile);
		}
            }
	}

	private void delete(File f){
		if((f != null) && (f.exists()==true) ){
			if (f.isDirectory()) {
				for (File c : f.listFiles()){
					delete(c);
                                }
			}
			f.delete();
		}
	}

        /**
         * Gets the work-directory of the resource.
         * The work-directory contains temporary contents in order to process operations and calculations
         * @return 
         */
	@Transient
	public File getWorkDirectory() {
		if ((this.workDirectory == null) || (this.workDirectory.exists()==false)) {
			setWorkDirectory(new File(suggestWorkDirectoryPath()));
		}
		return this.workDirectory;
	}

         /**
         * Sets the work-directory of the resource.
         * The work-directory contains temporary contents in order to process operations and calculations
         * @return 
         */
	@Transient
	public void setWorkDirectory(File workDirectory) {
		//Purge old dir?
		if(!workDirectory.exists()){
			workDirectory.mkdirs();
		}
		this.workDirectory = workDirectory;
	}

        /**
         * Gets the name (description) of the ResourceStrategy of the resource.
         * @return
         */
	public String getStrategyName(){
		return strategy.getName();
	}
        
         /**
         * Gets the ResourceStrategy of the resource.
         * The resource strategy implements the operations for the resource.
         * @return 
         */
	public ResourceStrategy getStrategy() {
		return strategy;
	}
        

         /**
         * Sets the ResourceStrategy of the resource.
         * The resource strategy implements the operations for the resource.
         * @return 
         */
	public void setStrategy(ResourceStrategy strategy) {
		this.strategy = strategy;
	}

	
        /**
         * Gets the username required to connect to the resource.
         * @return 
         */
	public String getUsername() {
		return username;
	}

         /**
         * Sets the username required to connect to the resource.
         */
	public void setUsername(String username) {
		this.username = username;
	}

        /**
         * Gets the password required to connect to the resource.
         * @return 
         */
	public String getPassword() {
		return password;
	}

         /**
         * Sets the password required to connect to the resource.
         * @return 
         */
	public void setPassword(String password) {
		this.password = password;
	}
	
        /**
         * Checks if the ResourceStrategy is extending a given ResourceStrategy class or interface by using reflection.
         * @param baseStrategy
         * @return 
         */
	@Transient
	public boolean isStrategyExtending(Class<?> baseStrategy){
		Set<Class<?>> allStrategyClassImplementations = ReflectionHelper.getAllImplementations(reflectionPackageSearchPath, baseStrategy);
		return allStrategyClassImplementations.contains(this.strategy.getClass());
	}
	
        /**
         * Get all resources in the class-path using reflection
         * @return 
         */
	@Transient
	public static Set<Class<?>> getAllRessources(){
		return ReflectionHelper.getAllImplementations(reflectionPackageSearchPath, Resource.class);
	}	

        /**
         * Gets all instanciable resources in the class-path using reflection.
         * Filters abstract classes and interfaces.
         * @return 
         */
	@Transient
	public static Set<Class<?>> getAllInstanciableRessource(){
		return ReflectionHelper.getAllInstanciableImplementations(reflectionPackageSearchPath, Resource.class);
	}
	
        /**
         * Gets all ResourceStrategies in the class-path using reflection.
         * @return 
         */
	@Transient
	public static Set<Class<?>> getAllRessourceStrategies(){
		return ReflectionHelper.getAllImplementations(reflectionPackageSearchPath, ResourceStrategy.class);
	}

         /**
         * Gets all instanciable ResourceStrategies in the class-path using reflection.
         * Filters abstract classes and interfaces
         * @return 
         */
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
		if(obj == null){
			return false;
                }
		if (obj == this) {
			return true;
                }
		if (obj.getClass() != getClass()) {
			return false;
                }
		Resource otherResource = (Resource) obj;
		if(getName().equals(otherResource.getName())){
			return true;
		}
		return false;
	}
}