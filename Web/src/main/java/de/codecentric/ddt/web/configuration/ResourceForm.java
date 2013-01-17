package de.codecentric.ddt.web.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.configuration.ResourceStrategy;
import de.codecentric.ddt.web.DDTException;
import de.codecentric.ddt.web.MyVaadinApplication;

public class ResourceForm extends Form {

	private static final long serialVersionUID = 300486871594883748L;
	private static final String[] visibleItemProperties = new String[]{"name","url","workDirectory","strategy"};
	private static final Set<String> requiredItemProperties = new HashSet<String>(Arrays.asList(new String[]{"name","url","workDirectory","strategy"}));

	private Button commitButton;
	private Button cancelButton;
	private Button testButton;
	
	private HierarchicalContainer configurationContainer;
	private Object parent;

	Layout layout;

	public ResourceForm(HierarchicalContainer configurationContainer, Object parent, BeanItem<Resource> resource){

		this.configurationContainer = configurationContainer;
		this.parent = parent;

		setWriteThrough(false);
		addStyleName("bordered");
		setCaption("Edit Resource");
		initFormFieldFactory();
		setItemDataSource(resource);
		setVisibleItemProperties(visibleItemProperties);
		setImmediate(true);
		setValidationVisible(true);
		setValidationVisibleOnCommit(true);
		
		layout = new VerticalLayout();
		layout.addComponent(new Label("Edit Resource"));
		layout.addComponent(new Hr());

		commitButton = new Button("Apply");
		initCommitButton();
		getFooter().addComponent(commitButton);
		
		testButton = new Button("Test");
		initTestButton();
		getFooter().addComponent(testButton);
		
		getFooter().addComponent(new Button("Discard", this, "discard"));
		
		cancelButton = new Button("Cancel");
		initCancelButton();
		
		setLayout(layout);
	}
	
	private void initTestButton(){
		this.testButton.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Resource resource = ((BeanItem<Resource>) getItemDataSource()).getBean();
				boolean resourcePassesSmokeTest = resource.passesSmokeTest();
				if(resourcePassesSmokeTest){
					MyVaadinApplication.getMainWindows().showNotification("Test Passed!", Notification.TYPE_HUMANIZED_MESSAGE);
				} else {
					MyVaadinApplication.getMainWindows().showNotification("Test Failed!", Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
	}

	private void initCommitButton(){
		this.commitButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 506182668303841648L;

			@Override
			public void buttonClick(ClickEvent event) {
				//try{
					commit();
					Item item = getItemDataSource();
					if(parent != null){
						configurationContainer.addItem(item);
						configurationContainer.setChildrenAllowed(parent, true);
						configurationContainer.setParent(item, parent);
						configurationContainer.setChildrenAllowed(item, false);
					}
					Item addedItem = configurationContainer.getItem(item);
					String name = (String) item.getItemProperty("name").getValue();
					addedItem.getItemProperty("caption").setValue(name);
				//} catch (Exception ex){
					//throw new DDTException("Please fill out the mandatory fields!");
				//}
			}
		});
	}

	private void initCancelButton(){
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -2487617398675699006L;

			@Override
			public void buttonClick(ClickEvent event) {
				discard();
			}
		});
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
				field.setWidth(90, UNITS_PERCENTAGE);
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

	private class Hr extends Label {
		private static final long serialVersionUID = 1L;

		Hr() {
			super("<hr/>", Label.CONTENT_XHTML);
		}
	}

}
