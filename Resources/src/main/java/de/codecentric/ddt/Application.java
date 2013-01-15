package de.codecentric.ddt;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Application implements Serializable{
	
	private static final long serialVersionUID = 5725877857494839617L;

	@Id
	private String name;
	
	@ElementCollection
	private Set<Resource<? extends ResourceStrategy>> resources;
	
	public Set<Resource<? extends ResourceStrategy>> getResources() {
		return resources;
	}

	public void setResources(Set<Resource<? extends ResourceStrategy>> resources) {
		this.resources = resources;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	public Set<Resource<? extends ResourceStrategy>> getRessourcesExtending(Class<?> strategyInterface){
		Set<Resource<? extends ResourceStrategy>> resourcesImpementingStrategy = new HashSet<Resource<? extends ResourceStrategy>>();
		for(Resource<?> currentResource: resources){
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
