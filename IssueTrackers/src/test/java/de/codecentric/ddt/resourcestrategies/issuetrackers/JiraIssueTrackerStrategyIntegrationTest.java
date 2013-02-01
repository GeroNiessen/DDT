/*
package de.codecentric.ddt.resourcestrategies.issuetrackers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.codecentric.ddt.resourcestrategies.issuetrackers.IssueTracker;
import de.codecentric.ddt.resourcestrategies.issuetrackers.JiraIssueTrackerStrategy;

public class JiraIssueTrackerStrategyIntegrationTest {
	
	private IssueTracker issueTracker;
        
	private static final String TESTPROJECT = "Twitter4J";
	private static final String TESTISSUE = "TFJ-700";
        private static final String URL = "http://jira.twitter4j.org/";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		issueTracker = new IssueTracker();
		issueTracker.setName("LOG4J_Jira_IssueTracker");
		issueTracker.setUrl(URL);
		issueTracker.setStrategy(new JiraIssueTrackerStrategy());
                issueTracker.setUsername("anonymous");
                issueTracker.setPassword("anonymous");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllProjects() {
		List<String> allProjects = issueTracker.getAllProjects();
		assertTrue("Could not find any projects!", allProjects.size() > 0);
	}

	@Test
	public void testGetIssuesForProject() {
		List<String> allIssuesForProject = issueTracker.getIssuesForProject(TESTPROJECT);
		assertTrue("Could not find any issues for project!", allIssuesForProject.size() > 0);
	}

	@Test
	public void testGetOpenIssuesForProject() {
		List<String> allOpenIssuesForProject = issueTracker.getOpenIssuesForProject(TESTPROJECT);
		assertTrue("Could not find any open issues for project!", allOpenIssuesForProject.size() > 0);
	}

	@Test
	public void testGetIssueStatus() {
		String issueStatus = issueTracker.getIssueStatus(TESTISSUE);
		assertTrue("The issue status is empty!", issueStatus.length() > 0);
	}
}
*/