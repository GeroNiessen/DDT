package de.codecentric.ddt.resourcestrategies.repositories;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.codecentric.ddt.resourcestrategies.repositories.MercurialRepositoryStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.Repository;

public class MercurialRepositoryStrategyIntegrationTest {
	
	private static de.codecentric.ddt.resourcestrategies.repositories.Repository repository;
	
	@Before
	public void setUp() throws Exception {
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		repository = new Repository();
		repository.setName("ICIS_Mercurial_Repository");
		repository.setUrl("http://wgvli39.swlabor.local:8282");
		repository.setStrategy(new MercurialRepositoryStrategy());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetLatestVersion() {
		try{
			repository.getLatestVersion();
		} catch(Exception e){
			assertTrue("Getting the latest version has caused an error!",false);
		}
	}
	
	@Test
	public void testGetBranches() {
		assertTrue("No branch found!", (repository.getBranches().size() > 0));
		assertTrue("Branches do not contain main branch", repository.getBranches().contains( repository.getMainBranch() ));
	}

	@Test
	public void testSetBranch() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetMainBranch() {
		assertEquals("Main branch does not match", "default", repository.getMainBranch());
	}

	@Test
	public void testGetCurrentBranch() {
		assertEquals("Wrong current Branch", repository.getMainBranch(), repository.getCurrentBranch());
	}

	@Test
	public void testGetName() {
		assertEquals("Strategy name is wrong", "MercurialRepositoryStrategy", repository.getStrategyName());
	}
}
