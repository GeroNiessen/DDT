package de.codecentric.ddt.issuetrackers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JiraIssueTrackerStrategyTest {
	
	private IssueTracker issueTracker;
	private static final String TESTPROJECT = "CRM 4.0";
	private static final String TESTISSUE = "ICISCRM-891";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		issueTracker = new IssueTracker();
		issueTracker.setName("ICIS_Jira_IssueTracker");
		issueTracker.setUrl("http://wgvli39.swlabor.local:8181");
		issueTracker.setStrategy(new JiraIssueTrackerStrategy());
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
