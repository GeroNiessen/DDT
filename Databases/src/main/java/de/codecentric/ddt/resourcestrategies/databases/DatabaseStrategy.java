package de.codecentric.ddt.resourcestrategies.databases;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DatabaseStrategy defines the extension of the functionality of a ResouceStrategy in order to be used by Database Resources
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public abstract class DatabaseStrategy extends ResourceStrategy {
	private static final long serialVersionUID = 5278375323743063152L;

        /**
         * Generates the proxy classes for the stored procedures and packages in a database defined by the context.
         * Requires a javaPackageName in order to generate valid Java classes.
         * @param databaseContext
         * @param packageName 
         */
	public abstract void generateProxyClasses(Resource databaseContext, String packageName);
	
}
