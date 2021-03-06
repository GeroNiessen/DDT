package de.codecentric.ddt.resourcestrategies.issuetrackers;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import de.codecentric.ddt.configuration.ConnectionTestHelper;
import de.codecentric.ddt.configuration.Resource;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JiraIssueTrackerStrategy implements the IssueTrackerTrackerStrategy in order to operate on Jira.
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public class JiraIssueTrackerStrategy extends IssueTrackerStrategy implements Serializable {
	
	private static final long serialVersionUID = -2611926459062283519L;
	private static final int MAXIMUM_SEARCH_RESULTS = 1000;
	
	public JiraIssueTrackerStrategy(){
		setName("JiraIssueTrackerStrategy");
	}
	
	@Override
	public boolean passesSmokeTest(Resource context) {
		return ConnectionTestHelper.testURLConnection(context.getUrl(), 2500);
	}
	
        /**
         * Gets the REST client to access Jira. 
         * REST has long term support by Jira.
         * @param issueTrackerContext
         * @return 
         */
	private JiraRestClient getJiraRestClient(Resource issueTrackerContext){
		JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		JiraRestClient jiraRestClient = null;
		try {
			URI jiraServerUri = new URI(issueTrackerContext.getUrl());
			String username = issueTrackerContext.getUsername();
			String password = issueTrackerContext.getPassword();
			jiraRestClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
                        
			e.printStackTrace();
		}
		return jiraRestClient;
	}
	
        /**
         * Gets the REST client for Jira projects
         * @param issueTrackerContext
         * @return 
         */
	private ProjectRestClient getProjectRestClient(Resource issueTrackerContext){
		return getJiraRestClient(issueTrackerContext).getProjectClient();
	}
	
        /**
         * Gets the REST client for Jira issues
         * @param issueTrackerContext
         * @return 
         */
	private IssueRestClient getIssueRestClient(Resource issueTrackerContext){
		return getJiraRestClient(issueTrackerContext).getIssueClient();
	}
	
        /**
         * Gets the REST client for custom seraches in Jira
         * @param issueTrackerContext
         * @return 
         */
	private SearchRestClient getSearchRestClient(Resource issueTrackerContext){
		return getJiraRestClient(issueTrackerContext).getSearchClient();
	}
	
        /**
         * Gets only the issues defined by JQL (Jira Query Language) Queries
         * @param issueTrackerContext
         * @param jqlString
         * @return 
         */
	private Iterable<BasicIssue> getSearchedIssues(Resource issueTrackerContext, String jqlString){
		ProgressMonitor progressMonitor = new NullProgressMonitor();
		SearchResult searchResult = getSearchRestClient(issueTrackerContext).searchJql(jqlString, MAXIMUM_SEARCH_RESULTS, 0, progressMonitor);
		return searchResult.getIssues();
	}
		
	@Override
	public List<String> getAllProjects(Resource issueTrackerContext) {
		List<String> returnedProjects = new ArrayList<String>();
		ProgressMonitor progressMonitor = new NullProgressMonitor();
		Iterable<BasicProject> projectList = getProjectRestClient(issueTrackerContext).getAllProjects(progressMonitor);
		for (BasicProject currentProject: projectList) {
			returnedProjects.add(currentProject.getName());
		}
		return returnedProjects;
	}

	@Override
	public List<String> getIssuesForProject(Resource issueTrackerContext, String projectName) {
		String jqlString = "project = \"" + projectName + "\"";
		List<String> returnedTickets = new ArrayList<>();
		for(BasicIssue currentIssue : getSearchedIssues(issueTrackerContext, jqlString)){
			returnedTickets.add(currentIssue.getKey());
		}
		return returnedTickets;
	}

	@Override
	public List<String> getOpenIssuesForProject(Resource issueTrackerContext, String projectName) {
		String jqlString = "project = \"" + projectName + "\" and status != \"closed\"";
		List<String> returnedTickets = new ArrayList<>();
		for(BasicIssue currentIssue : getSearchedIssues(issueTrackerContext, jqlString)){
			returnedTickets.add(currentIssue.getKey());
		}
		return returnedTickets;
	}

	@Override
	public String getIssueStatus(Resource issueTrackerContext, String ticketNumber) {
		ProgressMonitor progressMonitor = new NullProgressMonitor();
		Issue issue = getIssueRestClient(issueTrackerContext).getIssue(ticketNumber, progressMonitor);
		return issue.getStatus().getName();
	}
		
	/*
	public List<String> getTransitions(Resource issueTrackerContext, String ticketNumber){
		final Iterable<Transition> transitions = getIssueRestClient(issueTrackerContext).getTransitions(issue.getTransitionsUri(), new NullProgressMonitor());
		for (Transition transition : transitions) {
	        //if (transition.getName().equals(transitionName)) {
	            System.out.println(transition.getName());
	        //}
	    }		
	}
	*/
}
