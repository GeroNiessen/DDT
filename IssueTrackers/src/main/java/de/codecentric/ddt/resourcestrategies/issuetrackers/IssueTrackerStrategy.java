package de.codecentric.ddt.resourcestrategies.issuetrackers;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The IssueTrackerStrategy defines what has to be implemented by ResourceStrategy in order to server IssueTrackers as context
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public abstract class IssueTrackerStrategy extends ResourceStrategy {
	
	private static final long serialVersionUID = 716404585955706341L;
	public IssueTrackerStrategy(){
		super();
	}
	
        /**
         * Gets a list of all names (descriptions) of the project in an issueTrcker defined by the context
         * @param issueTrackerContext
         * @return 
         */
	public abstract List<String> getAllProjects(Resource issueTrackerContext);
        
        /**
         * Gets a list of all issues of a certain project for and IssueTracker defined by the context.
         * @param issueTrackerContext
         * @param projectName
         * @return 
         */
	public abstract List<String> getIssuesForProject(Resource issueTrackerContext, String projectName);
        
         /**
         * Gets a list of all OPEN issues of a certain project for and IssueTracker defined by the context.
         * @param issueTrackerContext
         * @param projectName
         * @return 
         */
	public abstract List<String> getOpenIssuesForProject(Resource issueTrackerContext, String projectName);
        
        /**
         * Gets the status of a ticket, defined by the ticket number for an IsssueTracker defined by the context.
         * @param issueTrackerContext
         * @param ticketNumber
         * @return 
         */
	public abstract String getIssueStatus(Resource issueTrackerContext, String ticketNumber);
}