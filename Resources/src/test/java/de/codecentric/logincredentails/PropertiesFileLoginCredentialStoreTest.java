package de.codecentric.logincredentails;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.codecentric.ddt.logincredentials.LoginCredential;
import de.codecentric.ddt.logincredentials.PropertiesFileLoginCredentialStore;

public class PropertiesFileLoginCredentialStoreTest {
	
	private LoginCredential firstLoginCredential;
	private LoginCredential secondLoginCredential;
	private LoginCredential sameAsFirstButWithDifferentUsernameAndPassword;
	
	private PropertiesFileLoginCredentialStore propertiesFileLoginCredentailStore;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		propertiesFileLoginCredentailStore = new PropertiesFileLoginCredentialStore();

		firstLoginCredential = new LoginCredential();
		firstLoginCredential.setName("first");
		firstLoginCredential.setUsername("firstUserName");
		firstLoginCredential.setPassword("firstPassword");
		
		secondLoginCredential = new LoginCredential();
		secondLoginCredential.setName("second");
		secondLoginCredential.setUsername("secondUsername");
		secondLoginCredential.setPassword("secondPassword");
		
		sameAsFirstButWithDifferentUsernameAndPassword = new LoginCredential();
		sameAsFirstButWithDifferentUsernameAndPassword.setName("first");
		sameAsFirstButWithDifferentUsernameAndPassword.setUsername("otherUsername");
		sameAsFirstButWithDifferentUsernameAndPassword.setPassword("otherPassword");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPersist() {
		propertiesFileLoginCredentailStore.persist(firstLoginCredential);
		//Persist again by intention
		propertiesFileLoginCredentailStore.persist(firstLoginCredential);
		propertiesFileLoginCredentailStore.persist(secondLoginCredential);
		propertiesFileLoginCredentailStore.persist(sameAsFirstButWithDifferentUsernameAndPassword);
	}

	@Test
	public void testFind() {
		LoginCredential retrievedFirstLoginCredential = propertiesFileLoginCredentailStore.find(firstLoginCredential.getName());
		assertEquals("", sameAsFirstButWithDifferentUsernameAndPassword, retrievedFirstLoginCredential);
		LoginCredential retrievedSecondLoginCredential = propertiesFileLoginCredentailStore.find(secondLoginCredential.getName());
		assertEquals("", secondLoginCredential, retrievedSecondLoginCredential);
	}
	
	@Test
	public void testDelete(){
		//propertiesFileLoginCredentailStore.remove(firstLoginCredential.getName());
		//propertiesFileLoginCredentailStore.remove(secondLoginCredential.getName());
	}
}
