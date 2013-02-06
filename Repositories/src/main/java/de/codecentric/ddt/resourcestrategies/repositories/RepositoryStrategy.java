package de.codecentric.ddt.resourcestrategies.repositories;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RepositoryStrategy is a ResourceStrategy definition for the extension of the functionality to gather informations and operate on Repositories.
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public abstract class RepositoryStrategy extends ResourceStrategy {

    private static final long serialVersionUID = -1828688334868764283L;

    /**
     * Gets the latest version of a repository defined by the context
     * e.g. 8753
     * @param repositoryContext 
     */
    public abstract void getLatestVersion(Resource repositoryContext);

    /**
     * Gets a list of names of all branches in a repository defined by the context
     * @param repositoryContext
     * @return 
     */
    public abstract List<String> getBranches(Resource repositoryContext);

    /**
     * Sets (switches) the branch of the repository defined by the context
     * @param branchName
     * @param repositoryContext 
     */
    public abstract void setBranch(String branchName, Resource repositoryContext);

    /**
     * Gets the name of the current branch of the repository defined by the context
     * @param repositoryContext
     * @return 
     */
    public abstract String getCurrentBranch(Resource repositoryContext);

    /**
     * Gets the name of the main branch of the repository defined by the context.
     * @return 
     */
    public abstract String getMainBranch();

    /**
     * Gets a Map of all open branches, which contain merged versions of other open branches, including the revisions of the merged branches.
     * Merges may be directly or transitively.
     * @param repositoryContext
     * @return 
     */
    public abstract Map<String, Map<String, Integer>> getLatestBranchMerges(Resource repositoryContext);

    /**
     * Gets a Map of all open branches and their current revisions from the repository specified by the context.
     * USES LOCAL COPY!
     * DOES NOT DOWNLOAD THE LATEST REVISIONS FROM THE SERVER!.
     * @param repositoryContext
     * @return 
     */
    public abstract Map<String, Integer> getLatestBranchRevisions(Resource repositoryContext);

    /**
     * Gets the latest revision of the repository defined by the context.
     * USES LOCAL COPY!
     * DOES NOT DOWNLOAD THE LATEST REVISIONS FROM THE SERVER!.
     * @param repositoryContext
     * @return 
     */
    public abstract int getLatestRepositoryRevision(Resource repositoryContext);

    /**
     * Deprecated method once used for testing.
     * @param repositoryContext 
     */
    public abstract void testme(Resource repositoryContext);
}