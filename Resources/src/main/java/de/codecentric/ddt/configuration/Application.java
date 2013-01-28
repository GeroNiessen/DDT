package de.codecentric.ddt.configuration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Application implements Serializable{
	
	private static final long serialVersionUID = 5725877857494839617L;

	@Id
	private String name;
	
	@OneToMany
	private Set<Resource> resources;
	
	public Application(){
		resources = new HashSet<Resource>();
	}
	
	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	public Set<Resource> getRessourcesExtending(Class<?> strategyInterface){
		Set<Resource> resourcesImpementingStrategy = new HashSet<Resource>();
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
