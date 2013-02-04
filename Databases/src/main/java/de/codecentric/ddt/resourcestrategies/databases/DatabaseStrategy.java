package de.codecentric.ddt.resourcestrategies.databases;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public abstract class DatabaseStrategy extends ResourceStrategy {
	private static final long serialVersionUID = 5278375323743063152L;

	public abstract void generateProxyClasses(Resource databaseContext, String packageName);
	
}