package de.codecentric.ddt.configuration;
import javax.ejb.Remote;

@Remote
public interface ConfigurationDAO {
        /**
         * Saves the configuration permanently.
         * @param configuration 
         */
	public void save(Configuration configuration);
        
        /**
         * Loads the stored configuration.
         * @return
         */
	public Configuration load();
}
