package de.codecentric.ddt.web;

import java.util.Collection;
import java.util.HashSet;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Select;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

import de.codecentric.ddt.configuration.Application;
import de.codecentric.ddt.configuration.Resource;

public class ConfigurationForm extends Form {

	private static final long serialVersionUID = -1138811604498909722L;

	private Layout layout;
	private final Tree tree;
	private HierarchicalContainer configurationContainer;
	private Form editorForm;

	public ConfigurationForm(){
		setHeight("100%");
		layout = new HorizontalLayout();
		layout.setHeight("100%");
		
		ConfigurationContainerProvider configurationContainerProvider = new ConfigurationContainerProvider();
		configurationContainer = configurationContainerProvider.getConfigurationAsHierarchicalContainer();
		tree = new Tree("Configuration");
		tree.setContainerDataSource(configurationContainer);
		initTree();
		layout.addComponent(tree);

		editorForm = new Form();
		editorForm.setCaption("Edit Item");

		setLayout(layout);
	}

	private void initTree(){
		
		tree.setItemCaptionPropertyId("caption");
		tree.setSelectable(true);
		for (Object itemId: tree.getItemIds())
            tree.expandItem(itemId);
		tree.addListener(new ItemClickEvent.ItemClickListener() {

			private static final long serialVersionUID = 6420532484630259487L;

			@Override
			public void itemClick(ItemClickEvent event) {
				switch (event.getButton()) {
				case ItemClickEvent.BUTTON_LEFT:
					//mainWindow.showNotification("Left Click");
					break;
				case ItemClickEvent.BUTTON_MIDDLE:
					//mainWindow.showNotification("Middle Click");
					break;
				case ItemClickEvent.BUTTON_RIGHT:
					//mainWindow.showNotification("Right Click");
					break;
				}
				BeanItem<Resource> selectedResourceBeanItem = (BeanItem<Resource>) event.getItemId();
				ResourceForm resourceForm = new ResourceForm(selectedResourceBeanItem);
				layout.replaceComponent(editorForm, resourceForm);
				editorForm = resourceForm;
			}
		});


		tree.addActionHandler(new Action.Handler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4677483235441889074L;
			final Action CONFIGURATION_CREATE_APPLICATION = new Action("Create new application");  
			final Action[] CONFIGURATION_ACTIONS = new Action[] { CONFIGURATION_CREATE_APPLICATION};
			
			final Action APPLICATION_DELETE = new Action("Delete application"); 
			final Action APPLICATION_CREATE_RESOURCE = new Action("Create new resource");
			final Action[] APPLICATION_ACTIONS = new Action[] { APPLICATION_DELETE, APPLICATION_CREATE_RESOURCE};
			
			final Action RESOURCE_DELETE = new Action("Delete resource");
			final Action[] RESOURCE_ACTIONS = new Action[] { RESOURCE_DELETE};
			
			private int countParents(Object itemId) {
				int parents = 0;
				Object parent = tree.getParent(itemId);
				while (parent != null) {
					parents++;
					parent = tree.getParent(parent);
				}
				return parents;
			}

			@Override
			public Action[] getActions(Object target, Object sender) {
				int parents = countParents(target);
				if(parents == 0){
					return CONFIGURATION_ACTIONS;
				}
				if(parents == 1){
					return APPLICATION_ACTIONS;
				}
				return RESOURCE_ACTIONS;
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				
				if(APPLICATION_CREATE_RESOURCE.equals(action)) {
					
					ResourceForm resourceForm = new ResourceForm(new BeanItem<Resource>(new Resource()));
					layout.replaceComponent(editorForm, resourceForm);
					editorForm = resourceForm;
				
				} else if(APPLICATION_DELETE.equals(action)){
					
					Collection<?> resources = new HashSet<Object>(configurationContainer.getChildren(target));
					for(Object currentResource: resources){
						configurationContainer.removeItem(currentResource);
					}				
					configurationContainer.removeItem(target);

				} else if(CONFIGURATION_CREATE_APPLICATION.equals(action)){

					BeanItem<de.codecentric.ddt.configuration.Application> newApplicationBeanItem = new BeanItem<Application>(new de.codecentric.ddt.configuration.Application());
					
					//ApplicationForm applicationForm = new ApplicationForm();
					//layout.replaceComponent(editorForm, applicationForm);
					//editorForm = applicationForm;
					
				} else if(RESOURCE_DELETE.equals(action)){
					
					tree.removeItem(target);
					
				}
			}


		});		
	}

	/*
	public HierarchicalContainer getConfigurationContainer(ConfigurationDAO configurationDAO){
		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty("caption", String.class, "");

		Configuration configuration = configurationDAO.load();
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
	*/
}


