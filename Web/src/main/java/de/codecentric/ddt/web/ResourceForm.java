package de.codecentric.ddt.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Component;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;


public class ResourceForm extends Form {

	private static final long serialVersionUID = 300486871594883748L;
	private static final String[] visibleItemProperties = new String[]{"name","url","workDirectory","strategy"};
	private static final Set<String> requiredItemProperties = new HashSet<String>(Arrays.asList(new String[]{"name","url","workDirectory","strategy"})); 
	Layout layout;

	public ResourceForm(BeanItem<Resource> resource){
		addStyleName("bordered");
		setCaption("Resource");
		
		setItemDataSource(resource);
		initFormFieldFactory();
		setVisibleItemProperties(visibleItemProperties);
		layout = new VerticalLayout();
		
		getFooter().addComponent(new Button("Commit", this, "commit"));
		getFooter().addComponent(new Button("Discard", this, "discard"));
		setLayout(layout);
	}

	private void initFormFieldFactory(){
		setFormFieldFactory(new DefaultFieldFactory(){
			private static final long serialVersionUID = -6325711492485750101L;

			@Override
			public Field createField(Item item, Object propertyId, Component uiContext) {
				Field field;
				String propertyIdString = (String) propertyId;
				
				if("strategy".equals(propertyIdString)){
					field = new ResourceStrategiesComboBox();
				} else {
	                field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
				}
				if(requiredItemProperties.contains(propertyIdString)){
					field.setRequired(true);
					field.setRequiredError("Missing Input");
				}
				field.setWidth("40%");
				return field;
			}
		});
	}
	
	private class ResourceStrategiesComboBox extends ComboBox{

		private static final long serialVersionUID = 1552949929937075845L;

		public ResourceStrategiesComboBox(){
			setNullSelectionAllowed(false);
			setCaption("Strategy");
			
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
}
