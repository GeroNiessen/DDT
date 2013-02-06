package de.codecentric.ddt.resourcestrategies.repositories;

import de.codecentric.ddt.configuration.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Repository is a Resource extended by the functionality to access repositories.
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public class Repository extends Resource {

    private static final long serialVersionUID = -1820059075191723303L;
    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Repository.class.getName());

    public Repository() {
    }

    public Repository(Resource otherResource) {
        super(otherResource);
    }

    /**
     * Gets/downloads the latest version of the repository from the server.
     */
    public void getLatestVersion() {
        try {
            getRepositoryStrategy().getLatestVersion(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get latest version of repository: ".concat(this.getName()));
        }
    }

    /**
     * Gets a list of all branches in the repository.
     * @return 
     */
    public List<String> getBranches() {
        try {
            return getRepositoryStrategy().getBranches(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get branches of repository:" + this.getName());
        }
        return new ArrayList<>();
    }

    /**
     * Sets (switches) the repository to a given branch, specified by name.
     * Logs a warning if the branch does not exist (any longer).
     * @param branchName 
     */
    public void setBranch(String branchName) {
        try {
            getRepositoryStrategy().setBranch(branchName, this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to switch to branch: " + branchName + " in repository: " + this.getName());
        }
    }

    /**
     * Gets the name of the current branch of the repository.
     * @return 
     */
    public String getCurrentBranch() {
        try {
            return getRepositoryStrategy().getCurrentBranch(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the current branch of repository: " + this.getName());
        }
        return "";
    }

    /**
     * Gets the default main branch of the repository.
     * E.G.: 
     * Subversion: HEAD
     * Mercurial: DEFAULT
     * Git: MASTER
     * @return
     */
    public String getMainBranch() {
        try {
            return getRepositoryStrategy().getMainBranch();
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the main branch of repository: " + this.getName());
        }
        return "";
    }

    /**
     * Gets a Map containing the latest revision-numbers of all branches in the repository
     * @return 
     */
    public Map<String, Integer> getLatestBranchRevisions() {
        try {
            return getRepositoryStrategy().getLatestBranchRevisions(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the latest branch revisisons of repository: " + this.getName());
        }
        return new HashMap<>();
    }

    /**
     * Gets a list of all transitive branch merges.
     * Map<String, Map<String, Integer>> ==
     * Map<BranchName>, Map<NameOfTheMergedBranch, RevisionOfTheMergedBranchWhenItWasMerged>
     * @return 
     */
    public Map<String, Map<String, Integer>> getLatestBranchMerges() {
        try {
            return getRepositoryStrategy().getLatestBranchMerges(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the latest branch merges of repository: " + this.getName());
        }
        return new HashMap<>();
    }

    /**
     * Gets the latest revision of the repository.
     * e.G. 8765
     * @return 
     */
    public int getLatestRepositoryRevision() {
        try {
            return getRepositoryStrategy().getLatestRepositoryRevision(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get latest repository revision!");
        }
        return -1;
    }

    /**
     * Gets the implementation of the repository-strategy used by the repository.
     * e.g. Subversion, Mercurial, Git, CVS, etc..
     * @return 
     */
    private RepositoryStrategy getRepositoryStrategy() {
        return (RepositoryStrategy) getStrategy();
    }
}
