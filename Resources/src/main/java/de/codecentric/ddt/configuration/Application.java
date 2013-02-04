package de.codecentric.ddt.configuration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Application implements Serializable{
	
	private static final long serialVersionUID = 5725877857494839617L;

		private String name;
	
		private Set<Resource> resources;
	
	public Application(){
		resources = new HashSet<>();
	}
	
        @OneToMany(targetEntity=de.codecentric.ddt.configuration.Resource.class)
	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
	
        @Id
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Resource> getRessourcesExtending(Class<?> strategyInterface){
		Set<Resource> resourcesImpementingStrategy = new HashSet<>();
		for(Resource currentResource: resources){
			if(currentResource.isStrategyExtending(strategyInterface)){
				resourcesImpementingStrategy.add(currentResource);
			}
		}
		return resourcesImpementingStrategy;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
