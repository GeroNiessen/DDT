package de.codecentric.ddt.configuration.logincredentials;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;


public class PropertiesFileLoginCredentialStore implements LoginCredentialStore{
	
	private static final String loginCredentialStorePropertiesFile = "LoginCredentials.properties";

	public PropertiesFileLoginCredentialStore(){
		
	}
	
	private Properties getPropertiesFiles(){
		Properties properties = new Properties();
		InputStream pfis = PropertiesFileLoginCredentialStore.class.getClassLoader().getResourceAsStream(loginCredentialStorePropertiesFile);		
		try {
			properties.load(pfis);
		} catch (IOException e) {
			savePropertiesToFile(properties, "Created new properties file");
			try {
				properties.load(pfis);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return properties;
	}
	
	private void savePropertiesToFile(Properties properties, String comment){
		URL propertyFileURL = PropertiesFileLoginCredentialStore.class.getClassLoader().getResource(loginCredentialStorePropertiesFile);
		File propertyFile = new File(propertyFileURL.getPath());
		try {
			OutputStream pfos = new FileOutputStream(propertyFile);
			properties.store(pfos, comment);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void persist(LoginCredential loginCredentialToPersist){
		Properties properties = getPropertiesFiles();
		
		String propertyPrefix = loginCredentialToPersist.getName() + ".";
		properties.setProperty(propertyPrefix + "username", loginCredentialToPersist.getUsername());
		properties.setProperty(propertyPrefix + "password", loginCredentialToPersist.getPassword());
		savePropertiesToFile(properties, "Added new login credential: " + loginCredentialToPersist.getName());
	}
	
	public LoginCredential find(String loginCredentialName){

		LoginCredential returnedLoginCredential = new LoginCredential();
		returnedLoginCredential.setName(loginCredentialName);
		String propertyPrefix = loginCredentialName + ".";
		try {
			Properties properties = getPropertiesFiles();
			returnedLoginCredential.setUsername(properties.getProperty(propertyPrefix + "username"));
			returnedLoginCredential.setPassword(properties.getProperty(propertyPrefix + "password"));
		} catch(Exception ex){
			returnedLoginCredential.setUsername("anonymous");
			returnedLoginCredential.setPassword("anonymous");
		}
		return returnedLoginCredential;
	}
	
	public void remove(String loginCredentialName){
		Properties properties = getPropertiesFiles();
		String propertyPrefix = loginCredentialName + ".";
		properties.remove(propertyPrefix + "username");
		properties.remove(propertyPrefix + "password");
		savePropertiesToFile(properties, "Removed login credential: " + loginCredentialName);
	}
	
	//private void purgeAllLoginCredentials(){
	//	new File(loginCredentialStorePropertiesFile).delete();
	//}
}
