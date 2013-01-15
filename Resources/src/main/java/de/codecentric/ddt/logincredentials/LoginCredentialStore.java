package de.codecentric.ddt.logincredentials;

public interface LoginCredentialStore {
	LoginCredential find(String loginCredentialName);
}
