package de.codecentric.ddt;
import javax.ejb.Remote;

@Remote
public interface ConfigurationDAO {
	public void save(Configuration configuration);
	public Configuration load();
}
