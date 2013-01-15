package de.codecentric.ddt;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.org.apache.bcel.internal.generic.RETURN;

@XmlRootElement
@Entity
public abstract class ResourceStrategy implements Serializable{
	private static final long serialVersionUID = 1125593675301859839L;

	@Id
	public abstract String getName();

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
