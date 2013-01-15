package de.codecentric.ddt;

import java.util.Collection;
import java.util.HashMap;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;

public class ConfigurationContainerProvider {
	
	private static HierarchicalContainer configurationContainer;
	private static Container applicationsContainer;
	private static HashMap<BeanItem<de.codecentric.ddt.Application>, Container> resourcesContainers;
	
	public ConfigurationContainerProvider(){
		if(applicationsContainer == null){
			applicationsContainer = new IndexedContainer();
			applicationsContainer.addContainerProperty("caption", String.class, "");
		}
		
		if(configurationContainer == null){
			ConfigurationDAO configurationDAO = new XMLConfigurationDAO();
			Configuration configuration = configurationDAO.load();
			configurationContainer = getConfigurationContainer(configuration);
			resourcesContainers = new HashMap<BeanItem<de.codecentric.ddt.Application>, Container>();
			fillSecondaryContainers();
		}
	}
	
	private void initConfigurationContainer(){
		configurationContainer.addListener(new Container.ItemSetChangeListener() {
			private static final long serialVersionUID = -6654514039519839190L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				fillSecondaryContainers();
			}
		});
	}
	
	private void fillSecondaryContainers(){
		Collection<?> configurationBeanItems = configurationContainer.rootItemIds();
		for(Object currentConfigurationBeanItem: configurationBeanItems){
			
			Collection<?> applicationBeanItems = configurationContainer.getChildren(currentConfigurationBeanItem);
			for(Object currentApplicationBeanItem: applicationBeanItems){
				if(!applicationsContainer.containsId(currentApplicationBeanItem)){
					Item addedApplicationBeanItem = applicationsContainer.addItem(currentApplicationBeanItem);
					String currentApplicationName = ((BeanItem<de.codecentric.ddt.Application>)currentApplicationBeanItem).getBean().getName();
					addedApplicationBeanItem.getItemProperty("caption").setValue(currentApplicationName);
				}
				
				Collection<?> currentApplicationResourceBeanItems = configurationContainer.getChildren(currentApplicationBeanItem);
				//ToDo: remove old
				//for (Iterator i = table.getItemIds().iterator(); i.hasNext();) {
				
				//ToDo: fill other containers
				/*
				if(){
					
				}
				*/
			}
		}
	}
	
	public Container getApplicationsContainer(){
		return applicationsContainer;
	}
	
	public Configuration getConfigurationFromContainer(){
		Configuration configuration = null;
		
		Collection<?> configurationBeanItems = configurationContainer.rootItemIds();
		for(Object currentConfigurationBeanItem: configurationBeanItems){
			configuration = ((BeanItem<Configuration>) currentConfigurationBeanItem).getBean();
			
			Collection<?> applicationBeanItems = configurationContainer.getChildren(currentConfigurationBeanItem);
			for(Object currentApplicationBeanItem: applicationBeanItems){
				de.codecentric.ddt.Application currentApplication = ((BeanItem<de.codecentric.ddt.Application>) currentApplicationBeanItem).getBean();
				configuration.getApplications().add(currentApplication);
				
				Collection<?> resourceBeanItems = configurationContainer.getChildren(currentApplicationBeanItem);
				for(Object currentResourceBeanItem: resourceBeanItems){
					Resource<?> currentResource = ((BeanItem<Resource<?>>) currentResourceBeanItem).getBean();
					currentApplication.getResources().add(currentResource);
				}
			}
		}
		return configuration;
	}
	
	private HierarchicalContainer getConfigurationContainer(Configuration configuration){
		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty("caption", String.class, "");

		BeanItem<Configuration> configurationBeanItem = new BeanItem<Configuration>(configuration);
		Item addedConfigurationBeanItem = container.addItem(configurationBeanItem);
		addedConfigurationBeanItem.getItemProperty("caption").setValue("Applications");

		for(de.codecentric.ddt.Application currentApplication: configuration.getApplications()){
			BeanItem<de.codecentric.ddt.Application> currentApplicationBeanItem = new BeanItem<de.codecentric.ddt.Application>(currentApplication);
			Item addedApplicationBeanItem = container.addItem(currentApplicationBeanItem);
			addedApplicationBeanItem.getItemProperty("caption").setValue(currentApplication.getName());
			container.setParent(currentApplicationBeanItem, configurationBeanItem);
			for(Resource<?> currentResource: currentApplication.getResources()){
				BeanItem<Resource<?>> currentResourceBeanItem = new BeanItem<Resource<?>>(currentResource);
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
	
	/*
	public Container getApplicationsAsContainer(){
		
	}
	
	public Container getRessourceAsContainer(BeanItem<de.codecentric.ddt.Application> Application){
		
	}
	*/
}
