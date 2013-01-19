/*
package de.codecentric.ddt.web;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.ComboBox;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;

public class ResourceStrategiesComboBox extends ComboBox{

	private static final long serialVersionUID = 1552949929937075845L;

	public ResourceStrategiesComboBox(){
		setNullSelectionAllowed(false);
		
		Set<Class<?>> resourceStrategyClasses = Resource.getAllInstanciableRessourceStrategies();
		Set<ResourceStrategy> resourceStrategyInstances = new HashSet<ResourceStrategy>();
		for(Class<?> currentResourceStrategyClass: resourceStrategyClasses){
			ResourceStrategy currentResourceStategyInstance;
			try {
				currentResourceStategyInstance = (ResourceStrategy) currentResourceStrategyClass.newInstance();
				if(currentResourceStategyInstance != null){
					resourceStrategyInstances.add(currentResourceStategyInstance);
				}
			} catch (InstantiationException|IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to instanciate:" + currentResourceStrategyClass.getName());
			}
		}
		for(ResourceStrategy currentResourceStategyInstance: resourceStrategyInstances){
			addItem(currentResourceStategyInstance);
		}
		if(resourceStrategyInstances.iterator().hasNext()){
			select(resourceStrategyInstances.iterator().hasNext());
		}
	}
}
*/
