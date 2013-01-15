package de.codecentric.ddt.logincredentials;

import java.beans.Transient;
import java.io.IOException;
import java.util.Properties;

public class LoginCredential {
	
	private static String loginCredentialsConfigurationFile = "LoginCredentialsConfiguration.properties";
	
	private String name;
	private String username;
	private String Password;
	
	public LoginCredential(){
		this.name = "";
		this.username = "";
		this.Password = "";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return Password;
	}
	
	public void setPassword(String password) {
		Password = password;
	}
	
	@Transient
	public static LoginCredentialStore getLoginCredentialStore(){
		LoginCredentialStore loginCredentialStore;
		Properties loginCredentialsConfiguration = new Properties();
		try {
			//loginCredentialsConfiguration.load(new FileInputStream(loginCredentialsConfigurationFile));
			loginCredentialsConfiguration.load(LoginCredential.class.getClassLoader().getResourceAsStream(loginCredentialsConfigurationFile));
			//loginCredentialsConfiguration.load(Class.class.getResourceAsStream(loginCredentialsConfigurationFile));
			String loginCredentialStoreImplementation = loginCredentialsConfiguration.getProperty("LoginCredentialStoreImplementation");
			Class<?> c = Class.forName(loginCredentialStoreImplementation);
			loginCredentialStore = (LoginCredentialStore) c.newInstance();
		} catch (IOException|ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			loginCredentialStore = new PropertiesFileLoginCredentialStore();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loginCredentialStore;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((Password == null) ? 0 : Password.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginCredential other = (LoginCredential) obj;
		if (Password == null) {
			if (other.Password != null)
				return false;
		} else if (!Password.equals(other.Password))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
}
