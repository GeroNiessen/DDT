package de.codecentric.ddt.resourcestrategies.issuetrackers;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public abstract class IssueTrackerStrategy extends ResourceStrategy {
	
	private static final long serialVersionUID = 716404585955706341L;
	public IssueTrackerStrategy(){
		super();
	}
	
	public abstract List<String> getAllProjects(Resource issueTrackerContext);
	public abstract List<String> getIssuesForProject(Resource issueTrackerContext, String projectName);
	public abstract List<String> getOpenIssuesForProject(Resource issueTrackerContext, String projectName);
	public abstract String getIssueStatus(Resource issueTrackerContext, String ticketNumber);
}