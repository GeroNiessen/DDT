package de.codecentric.ddt.resourcestrategies.repositories;

import de.codecentric.ddt.configuration.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

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

    public void getLatestVersion() {
        try {
            getRepositoryStrategy().getLatestVersion(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get latest version of repository: ".concat(this.getName()));
        }
    }

    public List<String> getBranches() {
        try {
            return getRepositoryStrategy().getBranches(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get branches of repository:" + this.getName());
        }
        return new ArrayList<String>();
    }

    public void setBranch(String branchName) {
        try {
            getRepositoryStrategy().setBranch(branchName, this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to switch to branch: " + branchName + " in repository: " + this.getName());
        }
    }

    public String getCurrentBranch() {
        try {
            return getRepositoryStrategy().getCurrentBranch(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the current branch of repository: " + this.getName());
        }
        return "";
    }

    public String getMainBranch() {
        try {
            return getRepositoryStrategy().getMainBranch();
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the main branch of repository: " + this.getName());
        }
        return "";
    }

    public Map<String, Integer> getLatestBranchRevisions() {
        try {
            return getRepositoryStrategy().getLatestBranchRevisions(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the latest branch revisisons of repository: " + this.getName());
        }
        return new HashMap<String, Integer>();
    }

    public Map<String, Map<String, Integer>> getLatestBranchMerges() {
        try {
            return getRepositoryStrategy().getLatestBranchMerges(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get the latest branch merges of repository: " + this.getName());
        }
        return new HashMap<String, Map<String, Integer>>();
    }

    public int getLatestRepositoryRevision() {
        try {
            return getRepositoryStrategy().getLatestRepositoryRevision(this);
        } catch (Exception ex) {
            LOGGER.warning("Failed to get latest repository revision!");
        }
        return -1;
    }

    private RepositoryStrategy getRepositoryStrategy() {
        return (RepositoryStrategy) getStrategy();
    }
}
