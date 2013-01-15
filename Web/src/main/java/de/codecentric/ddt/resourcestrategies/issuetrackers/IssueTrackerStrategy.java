package de.codecentric.ddt.resourcestrategies.issuetrackers;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.configuration.ResourceStrategy;

@XmlRootElement
@Entity
public abstract class IssueTrackerStrategy extends ResourceStrategy {
	
	private static final long serialVersionUID = 716404585955706341L;
	public IssueTrackerStrategy(){
		super();
	}
	
	public abstract List<String> getAllProjects(IssueTracker issueTrackerContext);
	public abstract List<String> getIssuesForProject(IssueTracker issueTrackerContext, String projectName);
	public abstract List<String> getOpenIssuesForProject(IssueTracker issueTrackerContext, String projectName);
	public abstract String getIssueStatus(IssueTracker issueTrackerContext, String ticketNumber);
}