package de.codecentric.ddt.web.configuration;

import com.vaadin.data.Container;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import de.codecentric.ddt.configuration.*;
import java.util.Collection;

public class ConfigurationContainerProvider {

	private static HierarchicalContainer configurationContainer;
	private static Container applicationsContainer;

	private static Configuration configuration;
	private static ConfigurationDAO configurationDAO;

	public ConfigurationContainerProvider(){
		if(configurationDAO == null){
			configurationDAO = Configuration.getConfigurationDAO();	
		}
		
		if(configuration == null){
			configuration = configurationDAO.load();
		}

		if(applicationsContainer == null){
			applicationsContainer = new IndexedContainer();
			applicationsContainer.addContainerProperty("caption", String.class, "");
		}

		if(configurationContainer == null){
			configurationContainer = getConfigurationContainer(configuration);
		}
	}

	public void save(){
		configurationDAO.save(getConfigurationFromContainer());
	}

	public Container getApplicationsContainer(){
		return applicationsContainer;
	}
	
	private boolean isConfigurationContainerInValidState(){
		return (configurationContainer.rootItemIds() != null && configurationContainer.rootItemIds().size() == 1);
	}
	
	public Container createApplicationContainer(final Class<?>[] resourceStrategyFilters){
		final IndexedContainer filteredApplicationContainer =  new IndexedContainer();
		filteredApplicationContainer.addContainerProperty("caption", String.class, "");
		fillApplicationContainer(filteredApplicationContainer, resourceStrategyFilters);
		configurationContainer.addListener(new Container.PropertySetChangeListener() {
			
			private static final long serialVersionUID = -3522875724100999412L;

			@Override
			public void containerPropertySetChange(PropertySetChangeEvent event) {
				if(isConfigurationContainerInValidState()){ 
					fillApplicationContainer(filteredApplicationContainer, resourceStrategyFilters);
				}
			}
		});
		return filteredApplicationContainer;
	}
	
	public void fillApplicationContainer(Container referenceContainer, Class<?>[] resourceStrategyFilters){
		referenceContainer.removeAllItems();
		
		Collection<?> configurationBeanItems = configurationContainer.rootItemIds();
		for(Object currentConfigurationBeanItem: configurationBeanItems){
			if(configurationContainer.hasChildren(currentConfigurationBeanItem)){
				Collection<?> applicationBeanItems = configurationContainer.getChildren(currentConfigurationBeanItem);
				for(Object currentApplicationBeanItem: applicationBeanItems){
					@SuppressWarnings("unchecked")
					de.codecentric.ddt.configuration.Application currentApplication = ((BeanItem<de.codecentric.ddt.configuration.Application>) currentApplicationBeanItem).getBean(); 
					boolean foundAllRequiredResourceStrategies = true;
					for(Class<?> currentResourceStrategyFilter: resourceStrategyFilters){
						@SuppressWarnings("unchecked")
						Container currentResourceStrategyContainer = createResourceContainer((BeanItem<de.codecentric.ddt.configuration.Application>) currentApplicationBeanItem, currentResourceStrategyFilter);
						if(currentResourceStrategyContainer.size() == 0){
							foundAllRequiredResourceStrategies = false;
							break;
						}
					}
					if(foundAllRequiredResourceStrategies){
						Item addedApplicationBeanItem = referenceContainer.addItem(currentApplicationBeanItem);
						addedApplicationBeanItem.getItemProperty("caption").setValue(currentApplication.getName());
					}
				}
			}
		}
	}
	
	public Container createResourceContainer(BeanItem<de.codecentric.ddt.configuration.Application> applicationBeanItem, Class<?> resourceStrategyFilter){
		IndexedContainer resourceContainer = new IndexedContainer();
		resourceContainer.addContainerProperty("caption", String.class, "");
		if(applicationBeanItem != null){
			fillResourceContainer(applicationBeanItem, resourceContainer, resourceStrategyFilter);
		}
		return resourceContainer;
	}
	
	public void fillResourceContainer(BeanItem<de.codecentric.ddt.configuration.Application> applicationBeanItem, Container referenceContainer, Class<?> resourceStrategyFilter){
		
		referenceContainer.removeAllItems();
		if(configurationContainer.hasChildren(applicationBeanItem)){
			Collection<?> resourceBeanItems = configurationContainer.getChildren(applicationBeanItem);
			for(Object currentResourceBeanItem: resourceBeanItems){
				@SuppressWarnings("unchecked")
				Resource currentResource = ((BeanItem<Resource>) currentResourceBeanItem).getBean();
				if(resourceStrategyFilter == null || currentResource.isStrategyExtending(resourceStrategyFilter)){
					Item addedResourceBeanItem = referenceContainer.addItem(currentResourceBeanItem);
					addedResourceBeanItem.getItemProperty("caption").setValue(currentResource.getName());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Configuration getConfigurationFromContainer(){
		Configuration returnedConfiguration = null;

		Collection<?> configurationBeanItems = configurationContainer.rootItemIds();
		for(Object currentConfigurationBeanItem: configurationBeanItems){
			returnedConfiguration = ((BeanItem<Configuration>) currentConfigurationBeanItem).getBean();
			returnedConfiguration.getApplications().clear();

			if(configurationContainer.hasChildren(currentConfigurationBeanItem)){
				Collection<?> applicationBeanItems = configurationContainer.getChildren(currentConfigurationBeanItem);

				for(Object currentApplicationBeanItem: applicationBeanItems){
					de.codecentric.ddt.configuration.Application currentApplication = ((BeanItem<de.codecentric.ddt.configuration.Application>) currentApplicationBeanItem).getBean();
					currentApplication.getResources().clear();
					
					returnedConfiguration.getApplications().add(currentApplication);
					if(configurationContainer.hasChildren(currentApplicationBeanItem)){
						Collection<?> resourceBeanItems = configurationContainer.getChildren(currentApplicationBeanItem);

						for(Object currentResourceBeanItem: resourceBeanItems){
							Resource currentResource = ((BeanItem<Resource>) currentResourceBeanItem).getBean();
							currentApplication.getResources().add(currentResource);
						}
						
					}
				}
			}
		}
		return returnedConfiguration;
	}

	private HierarchicalContainer getConfigurationContainer(Configuration configuration){
		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty("caption", String.class, "");

		BeanItem<Configuration> configurationBeanItem = new BeanItem<>(configuration);
		Item addedConfigurationBeanItem = container.addItem(configurationBeanItem);
		addedConfigurationBeanItem.getItemProperty("caption").setValue("Applications");

		for(de.codecentric.ddt.configuration.Application currentApplication: configuration.getApplications()){
			BeanItem<de.codecentric.ddt.configuration.Application> currentApplicationBeanItem = new BeanItem<>(currentApplication);
			Item addedApplicationBeanItem = container.addItem(currentApplicationBeanItem);
			addedApplicationBeanItem.getItemProperty("caption").setValue(currentApplication.getName());
			container.setParent(currentApplicationBeanItem, configurationBeanItem);
			for(Resource currentResource: currentApplication.getResources()){
				BeanItem<Resource> currentResourceBeanItem = new BeanItem<>(currentResource);
				Item addedResourceBeanItem = container.addItem(currentResourceBeanItem);
				addedResourceBeanItem.getItemProperty("caption").setValue(currentResource.getName());
				container.setChildrenAllowed(currentResourceBeanItem, false);
				container.setParent(currentResourceBeanItem, currentApplicationBeanItem);
			}
		}
		return container;
	}

	public HierarchicalContainer getConfigurationAsHierarchicalContainer(){
		return configurationContainer;
	}
}