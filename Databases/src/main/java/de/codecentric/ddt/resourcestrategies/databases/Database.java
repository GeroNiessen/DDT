package de.codecentric.ddt.resourcestrategies.databases;
import de.codecentric.ddt.configuration.Resource;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Database extends Resource{
	private static final long serialVersionUID = -8505911767674295696L;

	public Database(){
		super();
	}

	public Database(Resource otherResource){
		super(otherResource);
	}

	public void generateProxyClasses(String packageName){
		getDatabaseStrategy().generateProxyClasses(this, packageName);
	}
        	
        @Transient
	private DatabaseStrategy getDatabaseStrategy(){
		return (DatabaseStrategy) getStrategy();
	}
}
