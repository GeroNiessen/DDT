package de.codecentric.ddt.resourcestrategies.repositories;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.configuration.ResourceStrategy;

@XmlRootElement
@Entity
public abstract class RepositoryStrategy extends ResourceStrategy {
	private static final long serialVersionUID = -1828688334868764283L;
	
	public abstract void getLatestVersion(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract List<String> getBranches(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract void setBranch(String branchName, de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract String getCurrentBranch(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract String getMainBranch();
	public abstract Map<String, Map<String, Integer>> getLatestBranchMerges(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract Map<String, Integer> getLatestBranchRevisions(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract int getLatestRepositoryRevision(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
	public abstract void testme(de.codecentric.ddt.resourcestrategies.repositories.Repository repositoryContext);
}