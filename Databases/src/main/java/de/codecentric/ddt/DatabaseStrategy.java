package de.codecentric.ddt;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public abstract class DatabaseStrategy extends ResourceStrategy {
	private static final long serialVersionUID = 5278375323743063152L;

	public abstract void generateProxyClasses(Database databaseContext, String packageName);
}
