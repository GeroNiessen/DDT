package de.codecentric.ddt.configuration.logincredentials;

public interface LoginCredentialStore {
	LoginCredential find(String loginCredentialName);
}
