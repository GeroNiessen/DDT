package de.codecentric.ddt.configuration;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlRootElement;


//@MappedSuperclass
@XmlRootElement
@Entity
public abstract class ResourceStrategy implements Serializable{
	private static final long serialVersionUID = 1125593675301859839L;
        
	private String name;
	
        @Id
	public String getName(){
		return this.name; 
	}
	
	public void setName(String newName){
		this.name = newName;
	}
	
	public abstract boolean passesSmokeTest(Resource context);

	@Override
	public String toString(){
		return getName();
	}		

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;
		ResourceStrategy otherResourceStrategy = (ResourceStrategy) obj;
		if(getName().equals(otherResourceStrategy.getName())){
			return true;
		}
		return false;
	}
}
