package de.codecentric.ddt.deprecated;


import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.codecentric.ddt.configuration.Application;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import de.codecentric.ddt.web.ResourceStrategiesComboBox;

public class ResourceComponent extends CustomComponent{
	
	private static final long serialVersionUID = -3995048585549816380L;

	private TextField nameTextField;
	private TextField urlTextField;
	private TextField userNameTextField;
	private TextField passwordTextField;
	private ResourceStrategiesComboBox resourceStrategy;
	
	private Panel contentPanel;
	private Panel resourcePanel;
	private Panel actionPanel;
	
	private Button saveButton;
	private Button cancelButton;
	
	private de.codecentric.ddt.configuration.Application application;
	
	public ResourceComponent(de.codecentric.ddt.configuration.Application application, Resource resource){
		this.application = application;
		
		this.resourcePanel = new Panel();
		this.resourcePanel.setContent(new VerticalLayout());
		
		this.nameTextField = new TextField("Name");
		this.resourcePanel.addComponent(this.nameTextField);
		
		this.urlTextField = new TextField("URL");
		this.resourcePanel.addComponent(this.urlTextField);
		
		this.userNameTextField = new TextField("UserName");
		this.resourcePanel.addComponent(this.userNameTextField);
		
		this.passwordTextField = new TextField("Password");
		this.resourcePanel.addComponent(this.passwordTextField);
		
		this.resourceStrategy = new ResourceStrategiesComboBox();
		this.resourceStrategy.setCaption("Strategy");
		fillResourcePanel(resource);
		this.resourcePanel.addComponent(this.resourceStrategy);
		
		this.actionPanel = new Panel();
		this.actionPanel.setContent(new HorizontalLayout());
		
		this.saveButton = new Button("Save");
		this.actionPanel.addComponent(this.saveButton);
		
		this.cancelButton =  new Button("Cancel");
		this.actionPanel.addComponent(this.cancelButton);
		
		this.contentPanel = new Panel();
		this.contentPanel.setContent(new VerticalLayout());
		this.contentPanel.addComponent(this.resourcePanel);
		this.contentPanel.addComponent(this.actionPanel);
		
		setCompositionRoot(this.contentPanel);
	}
	
	private void fillResourcePanel(Resource resource){
		this.nameTextField.setValue(resource.getName());
		this.urlTextField.setValue(resource.getUrl());
		this.userNameTextField.setValue("");//resource.getUserName();
		this.passwordTextField.setValue("");//resource.getPassword();
		this.resourceStrategy.setValue(resource.getStrategy());
	}
	
	private Resource getResource(){
		ResourceStrategy newResourceStrategy = (ResourceStrategy) this.resourceStrategy.getValue();
		Resource newResource = new Resource();
		newResource.setName((String) nameTextField.getValue());
		newResource.setUrl((String) urlTextField.getValue());
		//newResource.setUserName((String) userNameTextField.getValue());
		//newResource.setPassword((String) passwordTextField.getValue());
		newResource.setStrategy(newResourceStrategy);
		return newResource;
	}
}
