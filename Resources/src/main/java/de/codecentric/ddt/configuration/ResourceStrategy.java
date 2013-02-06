package de.codecentric.ddt.configuration;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A ResourceStrategy implements the operations for all resources using the strategy
 * @author Gero Niessen
 */
//@MappedSuperclass
@XmlRootElement
@Entity
public abstract class ResourceStrategy implements Serializable{
	private static final long serialVersionUID = 1125593675301859839L;
        
	private String name;
	
        /**
         * Get the unique name (description) of a the resource strategy
         * @return 
         */
        @Id
	public String getName(){
		return this.name; 
	}

         /**
         * Sets the unique name (description) of a the resource strategy
         */
	public void setName(String newName){
		this.name = newName;
	}
	
        /**
         * Implements the check if a resource is reachable using the resource strategy
         * @param context
         * @return
         */
	public abstract boolean passesSmokeTest(Resource context);

	@Override
	public String toString(){
		return getName();
	}		

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
                }
		if (obj == this){
			return true;
                }
		if (obj.getClass() != getClass()){
			return false;
                }
		ResourceStrategy otherResourceStrategy = (ResourceStrategy) obj;
		if(getName().equals(otherResourceStrategy.getName())){
			return true;
		}
		return false;
	}
}
