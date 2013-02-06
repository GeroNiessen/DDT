package de.codecentric.ddt.configuration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Application is a logical unit containing resources
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public class Application implements Serializable {

    private static final long serialVersionUID = 5725877857494839617L;
    private String name;
    private Set<Resource> resources;

    public Application() {
        resources = new HashSet<>();
    }

    /**
     * All resources of the application
     * @return A set containing all resources
     */
    @OneToMany(targetEntity = de.codecentric.ddt.configuration.Resource.class)
    public Set<Resource> getResources() {
        return resources;
    }

    /**
     * Sets the resources of the application
     * @param resources
     */
    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    /**
     * The name of the application.
     * The name must be unique for all applications in the configuration
     * @return
     */
    @Id
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the application
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * All resources of the application, whose strategy implements certain
     * strategy-interface; The strategy-interface may be either directly or
     * transitively extended/implemented.
     *
     * @param strategyInterface: A class, abstract class or interface
     * @return A set of matching resources
     */
    public Set<Resource> getRessourcesImplementing(Class<?> strategyInterface) {
        Set<Resource> resourcesImpementingStrategy = new HashSet<>();
        for (Resource currentResource : resources) {
            if (currentResource.isStrategyExtending(strategyInterface)) {
                resourcesImpementingStrategy.add(currentResource);
            }
        }
        return resourcesImpementingStrategy;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
