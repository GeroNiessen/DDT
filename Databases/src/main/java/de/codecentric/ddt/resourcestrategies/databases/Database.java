package de.codecentric.ddt.resourcestrategies.databases;
import de.codecentric.ddt.configuration.Resource;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Database extends Resource by functionality to access and operate on databases.
 * @author Gero Niessen
 */
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

        /**
         * Generates the proxy classes for accessing the stored procedures and packages in a database.
         * Requires the name of the java package in order to generate java conform classes.
         * @param packageName
         */
	public void generateProxyClasses(String packageName){
		getDatabaseStrategy().generateProxyClasses(this, packageName);
	}
        
        /**
         * Gets the implementation of the DataBaseStrategy
         * e.g. Oracle, MySQL, Postfix, etc..
         * @return
         */
        @Transient
	private DatabaseStrategy getDatabaseStrategy(){
		return (DatabaseStrategy) getStrategy();
	}
}
