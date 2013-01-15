package de.codecentric.ddt.issuetrackers;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.Resource;

@XmlRootElement
@Entity
public class IssueTracker extends Resource<IssueTrackerStrategy> implements Serializable{
	
	private static final long serialVersionUID = -958706461860413784L;
	
	public List<String> getAllProjects() {
		return getStrategy().getAllProjects(this);
	}

	public List<String> getIssuesForProject(String projectName) {
		return getStrategy().getIssuesForProject(this, projectName);
	}

	public List<String> getOpenIssuesForProject(String projectName) {
		return getStrategy().getOpenIssuesForProject(this, projectName);
	}

	public String getIssueStatus(String ticketNumber) {
		return getStrategy().getIssueStatus(this, ticketNumber);
	}
}
