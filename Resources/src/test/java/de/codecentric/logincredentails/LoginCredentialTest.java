package de.codecentric.logincredentails;

import static org.junit.Assert.*;

import org.junit.Test;

import de.codecentric.ddt.logincredentials.LoginCredential;
import de.codecentric.ddt.logincredentials.LoginCredentialStore;

public class LoginCredentialTest {

	@Test
	public void testGetLoginCredentialStore() {
		LoginCredentialStore loginCredentialStore = LoginCredential.getLoginCredentialStore();
		assertNotNull("LoginCredentialStore is Null!", loginCredentialStore);
	}
}
