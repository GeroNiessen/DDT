package de.codecentric.ddt.repositories;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import de.codecentric.ddt.Resource;

@XmlRootElement
@Entity
public class Repository extends Resource<RepositoryStrategy>{

	private static final long serialVersionUID = -1820059075191723303L;

	public Repository(){
	}

	public Repository(Resource<?> otherResource){
		setName(otherResource.getName());
		setUrl(otherResource.getUrl());
		setWorkDirectory(otherResource.getWorkDirectory());
		setStrategy((RepositoryStrategy) otherResource.getStrategy());
	}
	
	public void getLatestVersion(){
		getStrategy().getLatestVersion(this);
	}
	
	public List<String> getBranches(){
		return getStrategy().getBranches(this);
	}
	
	public void setBranch(String branchName){
		getStrategy().setBranch(branchName, this);
	}
	
	public String getCurrentBranch(){
		return getStrategy().getCurrentBranch(this);
	}
	
	public String getMainBranch(){
		return getStrategy().getMainBranch();
	}
	
	public Map<String, Integer> getLatestBranchRevisions(){
		return getStrategy().getLatestBranchRevisions(this);
	}
	
	public Map<String, Map<String, Integer>> getLatestBranchMerges(){
		return getStrategy().getLatestBranchMerges(this);
	}

	public int getLatestRepositoryRevision(){
		return getStrategy().getLatestRepositoryRevision(this);
	}
}
