package de.codecentric.ddt;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Database extends Resource<DatabaseStrategy>{
	private static final long serialVersionUID = -8505911767674295696L;

	public Database(){
		super();
	}

	public Database(Resource<?> otherResource){
		setName(otherResource.getName());
		setUrl(otherResource.getUrl());
		setWorkDirectory(otherResource.getWorkDirectory());
		setStrategy((DatabaseStrategy) otherResource.getStrategy());
	}

	public void generateProxyClasses(String packageName){
		getStrategy().generateProxyClasses(this, packageName);
	}
}
