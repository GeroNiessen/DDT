package de.codecentric.ddt.resourcestrategies.issuetrackers;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.configuration.Resource;

@XmlRootElement
@Entity
public class IssueTracker extends Resource implements Serializable{
	
	private static final long serialVersionUID = -958706461860413784L;
	
	public List<String> getAllProjects() {
		return getIssueTrackerStrategy().getAllProjects(this);
	}

	public List<String> getIssuesForProject(String projectName) {
		return getIssueTrackerStrategy().getIssuesForProject(this, projectName);
	}

	public List<String> getOpenIssuesForProject(String projectName) {
		return getIssueTrackerStrategy().getOpenIssuesForProject(this, projectName);
	}

	public String getIssueStatus(String ticketNumber) {
		return getIssueTrackerStrategy().getIssueStatus(this, ticketNumber);
	}
	
	private IssueTrackerStrategy getIssueTrackerStrategy(){
		return (IssueTrackerStrategy) getStrategy();
	}
}