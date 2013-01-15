/*
package de.codecentric.ddt.issuetrackers;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;


public class App 
{
    public static void main( String[] args )
    {
    	try {
    		System.out.println( "Starting JRJC Test!" );

    		// JiraRestClient anfordern
    		JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
    		URI jiraServerUri = new URI(jiraURL);
    		JiraRestClient jiraRestClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);

    		// Mit dem ProjectRestClient die Liste aller sichtbaren Projekte anfordern und die Namen ausgeben
    		System.out.println("Getting all Projects...");
    		ProjectRestClient projectRestClient = jiraRestClient.getProjectClient();  
    		//projectRestClient.getProject("sdds", new NullProgressMonitor());
    		Iterable<BasicProject> projectList = projectRestClient.getAllProjects(new NullProgressMonitor());
    		for (BasicProject basicProject: projectList) {
    			System.out.println(basicProject.getName());
    		}

    		//projectRestClient.getProject("sjdj", new NullProgressMonitor());
    		//
    		System.out.println("Searching all isses of project \"CRM 4.0\"");
    		SearchResult searchResult = jiraRestClient.getSearchClient().searchJql("project = \"CRM 4.0\" and status != \"closed\"", 1000, 0, new NullProgressMonitor());  		
    		
    		Iterable<BasicIssue> searchedIssues = searchResult.getIssues();
    		for(BasicIssue currentIssue : searchedIssues){
    			System.out.println(currentIssue.getKey());
    		}
    		
    		IssueRestClient issueRestClient = jiraRestClient.getIssueClient();
    		
    		//
    		
    		//BasicIssue projectIssue = issueRestClient.getIssue("ICISCRM-891", new NullProgressMonitor());
    		//System.out.println(projectIssue.toString());
    		//for (BasicIssue currenBasicIssue: issueRestClient.get)
    		
    		System.out.println("Getting issue ICISCRM-891");
    		final Issue issue = issueRestClient.getIssue("ICISCRM-891", new NullProgressMonitor());
    		System.out.println("Getting status of ICISCRM-891");
    		System.out.println(issue.getStatus().toString());
    		//System.out.println(issue.getFieldByName("BasicStatus"));
    		
    		System.out.println("Getting all possible transitions for ICISCRM-891");
    		final Iterable<Transition> transitions = issueRestClient.getTransitions(issue.getTransitionsUri(), new NullProgressMonitor());
    		for (Transition transition : transitions) {
                //if (transition.getName().equals(transitionName)) {
                    System.out.println(transition.getName());
                //}
            }
    		//issueRestClient.ge
    		
    	} catch (Exception ex) {
    		// Fehler aufgetreten!
    		Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
    	}
    }
}
*/

