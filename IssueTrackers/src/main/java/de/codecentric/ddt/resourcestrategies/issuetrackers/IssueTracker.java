package de.codecentric.ddt.resourcestrategies.issuetrackers;

import de.codecentric.ddt.configuration.Resource;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * IssueTracker defines the functionality
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public class IssueTracker extends Resource implements Serializable{
	
	private static final long serialVersionUID = -958706461860413784L;
	
	public IssueTracker(){
		super();
	}
	
	public IssueTracker(Resource otherResource){
		super(otherResource);
	}
	
        /**
         * Gets a list of the names of all projects in the issue-tracker
         * @return 
         */
	public List<String> getAllProjects() {
		return getIssueTrackerStrategy().getAllProjects(this);
	}

        /**
         * Get a list of the names (descriptions) of all tickets in a project
         * @param projectName
         * @return 
         */
	public List<String> getIssuesForProject(String projectName) {
		return getIssueTrackerStrategy().getIssuesForProject(this, projectName);
	}

        /**
         * Gets a list of the names (descriptions) of all OPEN tickets in a specific project
         * @param projectName
         * @return 
         */
        public List<String> getOpenIssuesForProject(String projectName) {
		return getIssueTrackerStrategy().getOpenIssuesForProject(this, projectName);
	}

        /**
         * Gets the status of a tickets (open/closed/in review/blocked) for a certain ticket in the issue-tracker
         * @param ticketNumber
         * @return 
         */
	public String getIssueStatus(String ticketNumber) {
		return getIssueTrackerStrategy().getIssueStatus(this, ticketNumber);
	}
	
        /**
         * Gets the implementation of the IssueTrackerStrategy
         * e.g. Jira, Redmine, Bugzilla
         * @return 
         */
	private IssueTrackerStrategy getIssueTrackerStrategy(){
		return (IssueTrackerStrategy) getStrategy();
	}
}
