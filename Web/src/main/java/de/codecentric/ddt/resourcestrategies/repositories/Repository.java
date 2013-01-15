package de.codecentric.ddt.resourcestrategies.repositories;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.configuration.Resource;

@XmlRootElement
@Entity
public class Repository extends Resource{

	private static final long serialVersionUID = -1820059075191723303L;

	public Repository(){
	}

	public Repository(Resource otherResource){
		setName(otherResource.getName());
		setUrl(otherResource.getUrl());
		setWorkDirectory(otherResource.getWorkDirectory());
		setStrategy((RepositoryStrategy) otherResource.getStrategy());
	}
	
	public void getLatestVersion(){
		getRepositoryStrategy().getLatestVersion(this);
	}
	
	public List<String> getBranches(){
		return getRepositoryStrategy().getBranches(this);
	}
	
	public void setBranch(String branchName){
		getRepositoryStrategy().setBranch(branchName, this);
	}
	
	public String getCurrentBranch(){
		return getRepositoryStrategy().getCurrentBranch(this);
	}
	
	public String getMainBranch(){
		return getRepositoryStrategy().getMainBranch();
	}
	
	public Map<String, Integer> getLatestBranchRevisions(){
		return getRepositoryStrategy().getLatestBranchRevisions(this);
	}
	
	public Map<String, Map<String, Integer>> getLatestBranchMerges(){
		return getRepositoryStrategy().getLatestBranchMerges(this);
	}

	public int getLatestRepositoryRevision(){
		return getRepositoryStrategy().getLatestRepositoryRevision(this);
	}
	
	private RepositoryStrategy getRepositoryStrategy(){
		return (RepositoryStrategy) getStrategy();
	}
}
