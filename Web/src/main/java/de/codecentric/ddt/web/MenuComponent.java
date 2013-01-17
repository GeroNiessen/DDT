package de.codecentric.ddt.web;

import java.util.HashMap;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;

import de.codecentric.ddt.web.configuration.ConfigurationContainerProvider;

public class MenuComponent extends CustomComponent {
	
	private static final long serialVersionUID = 412955817621558989L;
	private HashMap<Button, Class<? extends CustomApplicationComponent>> buttonsAndComponentClasses;
	private HashMap<Button, Component> buttonsAndComponentInstances;
	
	private ComboBox applicationsComboBox;
	//private Component mainComponet;
	private Panel menuPanel;
	
	private Container applicationsContainer;
	
	public MenuComponent(Component mainComponent){
		this.buttonsAndComponentClasses		= new HashMap<Button, Class<? extends CustomApplicationComponent>>();
		this.buttonsAndComponentInstances	= new HashMap<Button, Component>();

		ConfigurationContainerProvider configurationContainerProvider = new ConfigurationContainerProvider();
		this.applicationsContainer = configurationContainerProvider.getApplicationsContainer();
				
		//this.mainComponet = mainComponet;

		menuPanel = new Panel();
		menuPanel.setContent(new HorizontalLayout());
		initApplicationsComboBox();
		menuPanel.addComponent(applicationsComboBox);
		setCompositionRoot(menuPanel);
	}
	
	private BeanItem<de.codecentric.ddt.configuration.Application> getSelectedApplicationBeanItem(){
		return (BeanItem<de.codecentric.ddt.configuration.Application>) this.applicationsComboBox.getValue();
	}
	
	private de.codecentric.ddt.configuration.Application getSelectedApplication(){
		return getSelectedApplicationBeanItem().getBean();
	}
	
	private BeanItem<de.codecentric.ddt.configuration.Application> getFirstApplicationBeanItem(){
		BeanItem<de.codecentric.ddt.configuration.Application> firstApplicationBeanItem = null;
		if(applicationsContainer.getItemIds().iterator().hasNext()){
			firstApplicationBeanItem = (BeanItem<de.codecentric.ddt.configuration.Application>)  applicationsContainer.getItemIds().iterator().next();
		}
		return firstApplicationBeanItem;
	}

	public void addMenuButtonAndComponent(String buttonText, Class<? extends CustomApplicationComponent> componentClass){
		Button newButton = new Button(buttonText);
		newButton.setImmediate(true);
		
		buttonsAndComponentClasses.put(newButton, componentClass);
		menuPanel.addComponent(newButton);
		
		newButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -1664422686221989871L;

			public void buttonClick(ClickEvent event) {
				Button clickedButton = event.getButton();
				if(!buttonsAndComponentInstances.containsKey(clickedButton)){
					CustomApplicationComponent newComponent;
					try {
						newComponent = buttonsAndComponentClasses.get(clickedButton).newInstance();
						newComponent.setApplication(getSelectedApplicationBeanItem());
						buttonsAndComponentInstances.put(clickedButton, newComponent);
						MyVaadinApplication.getInstance().setMainComponent(newComponent);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					//Component componentInstance = buttonsAndComponentInstances.get(clickedButton);
					MyVaadinApplication.getInstance().setMainComponent(new Label("Please select an action"));
					buttonsAndComponentInstances.remove(clickedButton);
				}
			}
		});
	}
	
	private void initApplicationsComboBox(){
		this.applicationsComboBox = new ComboBox();
		this.applicationsComboBox.setNullSelectionAllowed(false);
		this.applicationsComboBox.setImmediate(true);
		this.applicationsComboBox.setItemCaptionPropertyId("caption");
		//this.applicationsComboBox.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_ITEM);
		this.applicationsComboBox.setContainerDataSource(applicationsContainer);
		this.applicationsComboBox.select(getFirstApplicationBeanItem());
		
		this.applicationsComboBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -5893266547401238457L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				//removeAllComponentsFromDrawingPanel();
			}
		});
	}
	
	/*
	private void removeAllComponentsFromDrawingPanel(){
		for(Button currentButton: this.buttonsAndComponentInstances.keySet()){
			this.mainComponet.removeComponent(this.buttonsAndComponentInstances.get(currentButton));
			this.buttonsAndComponentInstances.remove(currentButton);
		}
	}
	*/
}
