package de.codecentric.ddt.resourcestrategies.repositories;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;

@XmlRootElement
@Entity
public abstract class RepositoryStrategy extends ResourceStrategy {
	private static final long serialVersionUID = -1828688334868764283L;
	
	public abstract void getLatestVersion(Resource repositoryContext);
	public abstract List<String> getBranches(Resource repositoryContext);
	public abstract void setBranch(String branchName, Resource repositoryContext);
	public abstract String getCurrentBranch(Resource repositoryContext);
	public abstract String getMainBranch();
	public abstract Map<String, Map<String, Integer>> getLatestBranchMerges(Resource repositoryContext);
	public abstract Map<String, Integer> getLatestBranchRevisions(Resource repositoryContext);
	public abstract int getLatestRepositoryRevision(Resource repositoryContext);
	public abstract void testme(Resource repositoryContext);
}