package de.codecentric.ddt.resourcestrategies.databases;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.configuration.Resource;

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
	
	private DatabaseStrategy getDatabaseStrategy(){
		return (DatabaseStrategy) getStrategy();
	}
}
