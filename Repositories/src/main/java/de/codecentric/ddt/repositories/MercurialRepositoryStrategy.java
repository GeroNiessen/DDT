package de.codecentric.ddt.repositories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import com.aragost.javahg.BaseRepository;
import com.aragost.javahg.Changeset;
import com.aragost.javahg.Repository;
import com.aragost.javahg.RepositoryConfiguration;
import com.aragost.javahg.commands.BranchCommand;
import com.aragost.javahg.commands.LogCommand;
import com.aragost.javahg.commands.PullCommand;
import com.aragost.javahg.commands.UpdateCommand;

@XmlRootElement
@Entity
public class MercurialRepositoryStrategy extends RepositoryStrategy {
	
	private static final long serialVersionUID = 34478867951614463L;
	private static final int commandWaitTimeoutInSeconds = 10;

	@Override
	public String getName() {
		return "MercurialRepositoryStrategy";
	}

	@Override
	public String getMainBranch() {
		return "default";
	}
	
	@Override
	public List<String> getBranches(de.codecentric.ddt.repositories.Repository repositoryContext) {
		return getBranches(getMercurialRepository(repositoryContext));
	}
	private List<String> getBranches(BaseRepository mercurialRepository){
		return getBranches(getHeadChangesets(mercurialRepository));
	}

	@Override
	public void setBranch(String branchName, de.codecentric.ddt.repositories.Repository repositoryContext) {
		BaseRepository mercurialRepository = getMercurialRepository(repositoryContext);

		UpdateCommand switchToBranch = new UpdateCommand(mercurialRepository);
		switchToBranch.cmdAppend("-c", branchName);//new String[]{branchName}
		try {
			//UpdateResult updateResult = 
			switchToBranch.execute();
			//updateResult.notifyAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getLatestVersion(de.codecentric.ddt.repositories.Repository repositoryContext) {
		String fileSeparator = System.getProperty("file.separator");
		String mercurialFolderPath = repositoryContext.getWorkDirectory().getPath() + fileSeparator + ".hg";

		File mercurialFolder = new File(mercurialFolderPath);
		BaseRepository mercurialRepository = BaseRepository.open(repositoryContext.getWorkDirectory());
		if (!mercurialFolder.exists()){
			Repository.clone(repositoryContext.getWorkDirectory(), repositoryContext.getUrl());
		}
		try {
			PullCommand.on(mercurialRepository).execute();
			UpdateCommand.on(mercurialRepository).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getCurrentBranch(de.codecentric.ddt.repositories.Repository repositoryContext) {
		BaseRepository mercurialRepository = getMercurialRepository(repositoryContext);
		String currentBranch = BranchCommand.on(mercurialRepository).get();
		return currentBranch;
	}
	//===========================================================================
	@Override
	public int getLatestRepositoryRevision(de.codecentric.ddt.repositories.Repository repositoryContext){		
		return getLatestRepositoryRevision(getMercurialRepository(repositoryContext));
	}
	private int getLatestRepositoryRevision(BaseRepository mercurialRepository){
		return getLatestRevision(getHeadChangesets(mercurialRepository));
	}
	//===========================================================================
	public Map<String, Integer> getLatestBranchRevisions(de.codecentric.ddt.repositories.Repository repositoryContext){
		return getLatestBranchRevisions(getMercurialRepository(repositoryContext));
	}
	private Map<String, Integer> getLatestBranchRevisions(BaseRepository mercurialRepository){
		Map<String, Integer> latestBranchRevisions = new HashMap<String, Integer>();
		for(Changeset currentHead: getHeadChangesets(mercurialRepository)){
			latestBranchRevisions.put(currentHead.getBranch(), currentHead.getRevision());
		}		
		return latestBranchRevisions;		
	}
	//===========================================================================
	private List<Changeset> getAllChangesets(BaseRepository mercurialRepository){
		LogCommand logcommand = new LogCommand(mercurialRepository);
		return logcommand.execute();
	}

	private BaseRepository getMercurialRepository(de.codecentric.ddt.repositories.Repository repositoryContext){
		RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
		repositoryConfiguration.setCommandWaitTimeout(commandWaitTimeoutInSeconds);
		repositoryConfiguration.setServerIdleTime(commandWaitTimeoutInSeconds);
		return BaseRepository.open(repositoryConfiguration, repositoryContext.getWorkDirectory());
	}

	public Map<String, Map<String, Integer>> getLatestBranchMerges(de.codecentric.ddt.repositories.Repository repositoryContext){
		return getLatestBranchMerges(getMercurialRepository(repositoryContext));
	}
	
	private Map<String, Map<String, Integer>> getLatestBranchMerges(BaseRepository mercurialRepository){

		List<Changeset> allChangesets = getAllChangesets(mercurialRepository);
		List<Changeset> headChangesets = getHeadChangesets(mercurialRepository);
		
		Map<String, Set<Changeset>> allBranchAncestorChangesets = getAllBranchAncestorChangesets(headChangesets, allChangesets);
		Map<String, Set<Changeset>> allBranchChangesets = getAllBranchChangesets(headChangesets, allChangesets);

		Map<String, Map<String, Integer>> latestBranchMerges = new HashMap<String, Map<String, Integer>>();
		List<String> allBranches = getBranches(headChangesets);

		for(String currentBranch: allBranches){
			latestBranchMerges.put(currentBranch, new HashMap<String, Integer>());		

			List<String> allBranchesExceptCurrent = new ArrayList<String>(allBranches);
			allBranchesExceptCurrent.remove(currentBranch);
			for(String otherBranch : allBranchesExceptCurrent){

				List<Changeset> joinedChangesets = new ArrayList<Changeset>(allBranchAncestorChangesets.get(currentBranch));
				joinedChangesets.retainAll(allBranchChangesets.get(otherBranch));

				int latestRevision = getLatestRevision(joinedChangesets);
				if(latestRevision > -1){
					latestBranchMerges.get(currentBranch).put(otherBranch, latestRevision);
				}
			}
		}
		return latestBranchMerges;
	}

	private Map<String, Set<Changeset>> getAllBranchChangesets(List<Changeset> headChangesets, List<Changeset> allChangesets){
		
		Map<String, Set<Changeset>> allBranchChangesets = new HashMap<String, Set<Changeset>>();
		List<String> allBranches = getBranches(headChangesets);

		for(Changeset currentChangeset: allChangesets){
			String currentBranch = currentChangeset.getBranch();
			if(allBranches.contains(currentBranch)){
				if(!allBranchChangesets.containsKey(currentBranch)){
					allBranchChangesets.put(currentBranch, new HashSet<Changeset>());
				}
				allBranchChangesets.get(currentBranch).add(currentChangeset);
			}
		}
		return allBranchChangesets;
	}

	private Map<String, Set<Changeset>> getAllBranchAncestorChangesets(List<Changeset> headChangesets, List<Changeset> allChangesets){
		Map<String, Set<Changeset>> allBranchAncestorChangesets = new HashMap<String, Set<Changeset>>(); 

		for(Changeset currentHead: headChangesets){
			int latestBranchRevision = currentHead.getRevision();
			for(Changeset currentChangeset: allChangesets){
				int currentChangesetRevision = currentChangeset.getRevision();
				if(currentChangesetRevision == latestBranchRevision){
					allBranchAncestorChangesets.put(currentChangeset.getBranch(), getAllAncestorChangesets(currentChangeset));	
				}
			}
		}
		return allBranchAncestorChangesets;
	}

	private Set<Changeset> getAllAncestorChangesets(Changeset startChangeset){
		Set<Changeset> allAncestorChangesets = new HashSet<Changeset>();
		getAllAncestorChangesets(startChangeset, allAncestorChangesets);
		return allAncestorChangesets;
	}

	private void getAllAncestorChangesets(Changeset currentChangeset, Set<Changeset> allAncestorChangesets){
		allAncestorChangesets.add(currentChangeset);
		Changeset[] parentChangesets = new Changeset[]{currentChangeset.getParent1(), currentChangeset.getParent2()};
		for(Changeset currentParentChangeset: parentChangesets){
			if(currentParentChangeset != null && !allAncestorChangesets.contains(currentParentChangeset)){
				getAllAncestorChangesets(currentParentChangeset, allAncestorChangesets);
			}
		}
	}

	//==================================================================
	private List<Changeset> getHeadChangesets(BaseRepository mercurialRepository){
		return mercurialRepository.heads();
	}

	private List<String> getBranches(List<Changeset> changesets){
		List<String> branches = new ArrayList<String>();
		for(Changeset currentChangeset: changesets){
			branches.add(currentChangeset.getBranch());
		}
		return branches;
	}

	/*
	private List<Integer> getRevisions(List<Changeset> changesets){
		List<Integer> revisions = new ArrayList<Integer>();
		for(Changeset currentChangeset: changesets){
			revisions.add(currentChangeset.getRevision());
		}
		return revisions;
	}
	*/

	private int getLatestRevision(List<Changeset> changesets){
		int latestRevision = -1;
		for(Changeset currentChangeset: changesets){
			int currentRevision = currentChangeset.getRevision();
			if( latestRevision < currentRevision){
				latestRevision = currentRevision;
			}
		}
		return latestRevision;
	}

	@Override
	public void testme(de.codecentric.ddt.repositories.Repository repositoryContext){
		System.out.println("Start: ANCESTORS");

		Map<String, Integer> latestVersions = getLatestBranchRevisions(repositoryContext);
		Map<String, Map<String, Integer>> latestBranchMerges = getLatestBranchMerges(repositoryContext);
		for(String currentBranch: latestBranchMerges.keySet()){
			System.out.println("The branch: " + currentBranch + " contains the branches:");
			Map<String, Integer> containedBranches = latestBranchMerges.get(currentBranch);
			for(String containedBranch: containedBranches.keySet()){
				Integer latestVersion = containedBranches.get(containedBranch);
				System.out.println(" - " + containedBranch + " in version: " + latestVersion + ". The latest revision is :" + latestVersions.get(containedBranch));				
			}
			System.out.print("=======================\n");
		}
	}
}
