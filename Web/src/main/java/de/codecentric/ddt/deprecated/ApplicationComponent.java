package de.codecentric.ddt.deprecated;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.codecentric.ddt.configuration.Application;
import de.codecentric.ddt.configuration.Resource;

public class ApplicationComponent extends CustomComponent {
	
	private de.codecentric.ddt.configuration.Application application;
	
	private TextField applicationNameTextField;
	private Button saveButton;
	private Button cancelButton;
	private Set<ResourceComponent> resourceComponents;
	
	private Panel contentPanel;
	private Panel applicationPanel;
	private Panel actionPanel;
	
	public ApplicationComponent(de.codecentric.ddt.configuration.Application application){
		
		this.application = application;
		
		applicationPanel = new Panel("Application");
		applicationPanel.setContent(new VerticalLayout());
		
		applicationNameTextField = new TextField("Application Name");
		applicationPanel.addComponent(applicationNameTextField);
		fillApplicationPanel();
		
		resourceComponents = new HashSet<ResourceComponent>();
		for(Resource currentResource : application.getResources()){
			ResourceComponent currentResourceComponent = new ResourceComponent(application, currentResource);
			resourceComponents.add(currentResourceComponent);
		}
		for(ResourceComponent currentResourceComponent : resourceComponents){
			applicationPanel.addComponent(currentResourceComponent);
		}
		
		//=========================
		actionPanel = new Panel();
		actionPanel.setContent(new HorizontalLayout());
		
		saveButton = new Button("Save");
		initSaveButton();
		actionPanel.addComponent(saveButton);
		
		cancelButton = new Button("Cancel");
		initCancelButton();
		actionPanel.addComponent(cancelButton);

		contentPanel = new Panel();
		contentPanel.setContent(new VerticalLayout());
		contentPanel.addComponent(applicationPanel);
		contentPanel.addComponent(actionPanel);
		
		setCompositionRoot(contentPanel);
	}
	
	private void fillApplicationPanel(){
		applicationNameTextField.setValue(application.getName());
	}
	
	private void initSaveButton(){
		//ToDo: 
	}
	
	private void initCancelButton(){
		
	}

}
