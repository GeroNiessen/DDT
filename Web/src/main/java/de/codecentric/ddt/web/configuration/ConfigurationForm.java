package de.codecentric.ddt.web.configuration;

import java.util.Collection;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalSplitPanel;

import de.codecentric.ddt.configuration.Application;
import de.codecentric.ddt.configuration.Resource;

public class ConfigurationForm extends Form {

	private static final long serialVersionUID = -1138811604498909722L;

	private final Tree tree;
	private HierarchicalContainer configurationContainer;
	private ConfigurationContainerProvider configurationContainerProvider;
	private Form editorForm;
	private Button saveConfigurationButton;
	private VerticalSplitPanel verticalSplitPanel;
	
	public ConfigurationForm(){
		setHeight("100%");
		verticalSplitPanel = new VerticalSplitPanel();
		
		configurationContainerProvider = new ConfigurationContainerProvider();
		configurationContainer = configurationContainerProvider.getConfigurationAsHierarchicalContainer();
		tree = new Tree("Configuration");
		tree.setContainerDataSource(configurationContainer);
		initTree();
		verticalSplitPanel.addComponent(tree);

		editorForm = new Form();
		editorForm.setCaption("Edit Item");
		verticalSplitPanel.addComponent(editorForm);
		
		saveConfigurationButton = new Button("Save Configuration");
		initSaveConfigurationButton();
		getFooter().addComponent(saveConfigurationButton);
		
		setLayout(verticalSplitPanel);
	}
	
	private void initSaveConfigurationButton(){
		this.saveConfigurationButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 9108261812853871991L;

			public void buttonClick(ClickEvent event) {
				configurationContainerProvider.save();
			}
		});
	}
	
	private int countParents(Object itemId) {
		int parents = 0;
		Object parent = tree.getParent(itemId);
		while (parent != null) {
			parents++;
			parent = tree.getParent(parent);
		}
		return parents;
	}

	private void initTree(){
		
		tree.setItemCaptionPropertyId("caption");
		//tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_ITEM);
		
		tree.setSelectable(true);
		for (Object itemId: tree.getItemIds())
            tree.expandItem(itemId);
		tree.addListener(new ItemClickEvent.ItemClickListener() {

			private static final long serialVersionUID = 6420532484630259487L;

			@Override
			public void itemClick(ItemClickEvent event) {
				switch (event.getButton()) {
				case ItemClickEvent.BUTTON_LEFT:
					int parents = countParents(event.getItemId());
					Form nextEditForm = null;
					if(parents == 0){
						nextEditForm = new Form();
					} else if(parents == 1){
						@SuppressWarnings("unchecked")
						BeanItem<de.codecentric.ddt.configuration.Application> selectedApplicationBeanItem = (BeanItem<de.codecentric.ddt.configuration.Application>) event.getItemId();
						nextEditForm = new ApplicationForm(configurationContainer, null, selectedApplicationBeanItem);					
					} else if(parents == 2){
						@SuppressWarnings("unchecked")
						BeanItem<Resource> selectedResourceBeanItem = (BeanItem<Resource>) event.getItemId();
						nextEditForm = new ResourceForm(configurationContainer, null, selectedResourceBeanItem);
					}
					verticalSplitPanel.replaceComponent(editorForm, nextEditForm);
					editorForm = nextEditForm;						
					break;
				case ItemClickEvent.BUTTON_MIDDLE:
					//mainWindow.showNotification("Middle Click");
					break;
				case ItemClickEvent.BUTTON_RIGHT:
					//mainWindow.showNotification("Right Click");
					break;
				}
			}
		});


		tree.addActionHandler(new Action.Handler() {

			private static final long serialVersionUID = 4677483235441889074L;

			final Action CONFIGURATION_CREATE_APPLICATION = new Action("Create new application");  
			final Action[] CONFIGURATION_ACTIONS = new Action[] { CONFIGURATION_CREATE_APPLICATION};
			
			final Action APPLICATION_DELETE = new Action("Delete application"); 
			final Action APPLICATION_CREATE_RESOURCE = new Action("Create new resource");
			final Action[] APPLICATION_ACTIONS = new Action[] { APPLICATION_DELETE, APPLICATION_CREATE_RESOURCE};
			
			final Action RESOURCE_DELETE = new Action("Delete resource");
			final Action[] RESOURCE_ACTIONS = new Action[] { RESOURCE_DELETE};
			
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

					BeanItem<Resource> newResourceBeanItem = new BeanItem<Resource>(new Resource());
					ResourceForm resourceForm = new ResourceForm(configurationContainer, target, newResourceBeanItem);
					verticalSplitPanel.replaceComponent(editorForm, resourceForm);
					editorForm = resourceForm;
				
				} else if(APPLICATION_DELETE.equals(action)){
					
					configurationContainer.removeItemRecursively(target);
					verticalSplitPanel.replaceComponent(editorForm, new Form());

				} else if(CONFIGURATION_CREATE_APPLICATION.equals(action)){

					BeanItem<de.codecentric.ddt.configuration.Application> newApplicationBeanItem = new BeanItem<Application>(new de.codecentric.ddt.configuration.Application());
					ApplicationForm applicationForm = new ApplicationForm(configurationContainer, target, newApplicationBeanItem);
					verticalSplitPanel.replaceComponent(editorForm, applicationForm);
					editorForm = applicationForm;
					
				} else if(RESOURCE_DELETE.equals(action)){
					
					Object parentApplication = configurationContainer.getParent(target);
					configurationContainer.removeItemRecursively(target);
					
					Collection<?> children = configurationContainer.getChildren(parentApplication);
					if(children == null){
						configurationContainer.setChildrenAllowed(parentApplication, false);
					}
					verticalSplitPanel.replaceComponent(editorForm, new Form());
				}
			}
		});		
	}
}