package de.codecentric.ddt.mercurialrepositorystrategy;

import com.aragost.javahg.BaseRepository;
import com.aragost.javahg.Changeset;
import com.aragost.javahg.Repository;
import com.aragost.javahg.RepositoryConfiguration;
import com.aragost.javahg.commands.BranchCommand;
import com.aragost.javahg.commands.LogCommand;
import com.aragost.javahg.commands.PullCommand;
import com.aragost.javahg.commands.UpdateCommand;
import de.codecentric.ddt.configuration.Configuration;
import de.codecentric.ddt.configuration.ConnectionTestHelper;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.resourcestrategies.repositories.RepositoryStrategy;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * MercurialRepositoryStrategy implements the functionality of a RespositoryStrategy for Mercurial repositories.
 * @author user
 */
@XmlRootElement
@Entity
public class MercurialRepositoryStrategy extends RepositoryStrategy {

	private static final long serialVersionUID = 34478867951614463L;
	private static final int commandWaitTimeoutInSeconds = 10;
        private static final String fileSeparator = System.getProperty("file.separator");
	private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MercurialRepositoryStrategy.class .getName());

	public MercurialRepositoryStrategy(){
		setName("MercurialRepositoryStrategy");
	}

	@Override
	public String getMainBranch() {
		return "default";
	}

	@Override
	public boolean passesSmokeTest(Resource context) {
		String urlWithUsernameAndPassword = getUrlWithUsernameAndPassword(context);
		System.out.println(urlWithUsernameAndPassword);
		return ConnectionTestHelper.testURLConnection(urlWithUsernameAndPassword, 2000);
	}

        /**
         * Gets the repository URL from the context and injects username and password to it.
         * @param context
         * @return 
         */
	private String getUrlWithUsernameAndPassword(Resource context){
		String urlWithUsernameAndPassword = context.getUrl(); 
		URL url;
		try {
			String urlString = context.getUrl();
			url = new URL(urlString);
			String host = url.getHost();
			String username = context.getUsername();
			String password = context.getUsername();
			// http://username:password@mydomain.com/myproject
			urlWithUsernameAndPassword = urlString.replace(host, username + ":" + password + "@" + host);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlWithUsernameAndPassword;
	}

	@Override
	public List<String> getBranches(Resource repositoryContext) {
		return getBranches(getMercurialRepository(repositoryContext));
	}
        /**
         * See documentation: getBranches(Resource repositoryContext)
         * @param mercurialRepository
         * @return 
         */
	private List<String> getBranches(BaseRepository mercurialRepository){
		return getBranches(getHeadChangesets(mercurialRepository));
	}

	@Override
	public void setBranch(String branchName, Resource repositoryContext) {
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

        /**
         * Checks if the repository has been at least downloaded (pulled) once to disk by checking the .hg file in the folder.
         * @param repositoryContext
         * @return 
         */
	private boolean isRepositoryAlreadyDownloaded(Resource repositoryContext){
		
		String mercurialFolderPath = repositoryContext.getWorkDirectory().getPath() + fileSeparator + ".hg";
		File mercurialFolder = new File(mercurialFolderPath);
		return mercurialFolder.exists();
	}

	@Override
	public void getLatestVersion(Resource repositoryContext) {

		if (!isRepositoryAlreadyDownloaded(repositoryContext)){
			try{
				Repository.clone(repositoryContext.getWorkDirectory(), repositoryContext.getUrl());
			} catch(Exception ex){
				LOGGER.warning("Failed to clone repository: " + repositoryContext.getName());
			}
		}
		if(isRepositoryAlreadyDownloaded(repositoryContext)){
			try {
				BaseRepository mercurialRepository = BaseRepository.open(repositoryContext.getWorkDirectory());
				PullCommand.on(mercurialRepository).execute();
				UpdateCommand.on(mercurialRepository).execute();
			} catch (IOException e) {
				LOGGER.warning("Failed to pull and update repository: " + repositoryContext.getName());
				//e.printStackTrace();
			}
		}
	}

	@Override
	public String getCurrentBranch(Resource repositoryContext) {
		BaseRepository mercurialRepository = getMercurialRepository(repositoryContext);
		String currentBranch = BranchCommand.on(mercurialRepository).get();
		return currentBranch;
	}
	//===========================================================================
	@Override
	public int getLatestRepositoryRevision(Resource repositoryContext){		
		return getLatestRepositoryRevision(getMercurialRepository(repositoryContext));
	}
        /**
         * See documentation: getLatestRepositoryRevision(Resource repositoryContext)
         * @param mercurialRepository
         * @return 
         */
	private int getLatestRepositoryRevision(BaseRepository mercurialRepository){
		return getLatestRevision(getHeadChangesets(mercurialRepository));
	}
	//===========================================================================
        @Override
	public Map<String, Integer> getLatestBranchRevisions(Resource repositoryContext){
		return getLatestBranchRevisions(getMercurialRepository(repositoryContext));
	}
        /**
         * See documentation: getLatestBranchRevisions(Resource repositoryContext)
         * @param mercurialRepository
         * @return 
         */
	private Map<String, Integer> getLatestBranchRevisions(BaseRepository mercurialRepository){
		Map<String, Integer> latestBranchRevisions = new HashMap<>();
		for(Changeset currentHead: getHeadChangesets(mercurialRepository)){
			latestBranchRevisions.put(currentHead.getBranch(), currentHead.getRevision());
		}		
		return latestBranchRevisions;		
	}
	//===========================================================================
        /**
         * Gets a list of simply every change-set in the repository.
         * @param mercurialRepository
         * @return 
         */
	private List<Changeset> getAllChangesets(BaseRepository mercurialRepository){
		LogCommand logcommand = new LogCommand(mercurialRepository);
		return logcommand.execute();
	}

        /**
         * Gets the javaHG BaseRepository Object.
         * @param repositoryContext
         * @return 
         */
	private BaseRepository getMercurialRepository(Resource repositoryContext){
		RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
		repositoryConfiguration.setCommandWaitTimeout(commandWaitTimeoutInSeconds);
		repositoryConfiguration.setServerIdleTime(commandWaitTimeoutInSeconds);
                
                //If different mercurial is needed
                File customMercurialExecutableUnix = new File(Configuration.getBaseWorkDirectory() + fileSeparator + "mercurial" + fileSeparator + "hg");
                if(customMercurialExecutableUnix.exists()){
                    repositoryConfiguration.setHgBin(customMercurialExecutableUnix.getPath());
                }
                
                //If different mercurial is needed
                File customMercurialExecutableWindows = new File(Configuration.getBaseWorkDirectory() + fileSeparator + "mercurial" + fileSeparator + "hg.exe");
                if(customMercurialExecutableWindows.exists()){
                    repositoryConfiguration.setHgBin(customMercurialExecutableWindows.getPath());
                }
                
		if(!isRepositoryAlreadyDownloaded(repositoryContext)){
			getLatestVersion(repositoryContext);
		}
		BaseRepository returnedBaseRepository = null;
		try{
			returnedBaseRepository = BaseRepository.open(repositoryConfiguration, repositoryContext.getWorkDirectory());
		} catch (Exception ex){
			LOGGER.warning(ex.getMessage());
		}
		return returnedBaseRepository; 
	}

        @Override
	public Map<String, Map<String, Integer>> getLatestBranchMerges(Resource repositoryContext){
		return getLatestBranchMerges(getMercurialRepository(repositoryContext));
	}

        /**
         * See documentation: getLatestBranchMerges(Resource repositoryContext)
         * @param mercurialRepository
         * @return 
         */
	private Map<String, Map<String, Integer>> getLatestBranchMerges(BaseRepository mercurialRepository){

		List<Changeset> allChangesets = getAllChangesets(mercurialRepository);
		List<Changeset> headChangesets = getHeadChangesets(mercurialRepository);

		Map<String, Set<Changeset>> allBranchAncestorChangesets = getAllBranchAncestorChangesets(headChangesets, allChangesets);
		Map<String, Set<Changeset>> allBranchChangesets = getAllBranchChangesets(headChangesets, allChangesets);

		Map<String, Map<String, Integer>> latestBranchMerges = new HashMap<>();
		List<String> allBranches = getBranches(headChangesets);

		for(String currentBranch: allBranches){
			latestBranchMerges.put(currentBranch, new HashMap<String, Integer>());		

			List<String> allBranchesExceptCurrent = new ArrayList<>(allBranches);
			allBranchesExceptCurrent.remove(currentBranch);
			for(String otherBranch : allBranchesExceptCurrent){

				List<Changeset> joinedChangesets = new ArrayList<>(allBranchAncestorChangesets.get(currentBranch));
				joinedChangesets.retainAll(allBranchChangesets.get(otherBranch));

				int latestRevision = getLatestRevision(joinedChangesets);
				if(latestRevision > -1){
					latestBranchMerges.get(currentBranch).put(otherBranch, latestRevision);
				}
			}
		}
		return latestBranchMerges;
	}

        /**
         * Gets a Map containing all open branches and their change-set in the branch.
         * @param headChangesets
         * @param allChangesets
         * @return 
         */
	private Map<String, Set<Changeset>> getAllBranchChangesets(List<Changeset> headChangesets, List<Changeset> allChangesets){

		Map<String, Set<Changeset>> allBranchChangesets = new HashMap<>();
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

        /**
         * Gets a Map containing all open branches and their associated ancestor change-set in the repository tree.
         * @param headChangesets
         * @param allChangesets
         * @return 
         */
	private Map<String, Set<Changeset>> getAllBranchAncestorChangesets(List<Changeset> headChangesets, List<Changeset> allChangesets){
		Map<String, Set<Changeset>> allBranchAncestorChangesets = new HashMap<>(); 

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

        /**
         * Gets a set of all ancestor change-sets from a given change-set
         * @param startChangeset
         * @return 
         */
	private Set<Changeset> getAllAncestorChangesets(Changeset startChangeset){
		Set<Changeset> allAncestorChangesets = new HashSet<>();
		getAllAncestorChangesets(startChangeset, allAncestorChangesets);
		return allAncestorChangesets;
	}

        /**
         * Recursion method for getAllAncestorChangesets(Changeset startChangeset)
         * @param currentChangeset
         * @param allAncestorChangesets 
         */
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
        /**
         * Gets a list of head change-sets from all open repository branches.
         * @param mercurialRepository
         * @return 
         */
	private List<Changeset> getHeadChangesets(BaseRepository mercurialRepository){
		return mercurialRepository.heads();
	}

        /**
         * Gets a list of names from all open repository branches.
         * @param changesets
         * @return 
         */
	private List<String> getBranches(List<Changeset> changesets){
		List<String> branches = new ArrayList<>();
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

        /**
         * Gets the revision of the latest change-set in the repository
         * @param changesets
         * @return 
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

        /**
         * Deprecated method for interim testing
         * @param repositoryContext 
         */
	@Override
	public void testme(Resource repositoryContext){
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
