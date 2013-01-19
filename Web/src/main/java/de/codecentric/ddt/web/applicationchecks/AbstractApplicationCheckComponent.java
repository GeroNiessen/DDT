package de.codecentric.ddt.web.applicationchecks;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;

import de.codecentric.ddt.web.configuration.ConfigurationContainerProvider;

public abstract class AbstractApplicationCheckComponent extends CustomComponent {

	private static final long serialVersionUID = 9024500901710088791L;
	
	protected ComboBox applicationsComboBox;
	protected final String containerItemCaptionPropertyId = "caption";

	ConfigurationContainerProvider configurationContainerProvider;
	protected HierarchicalContainer configurationContainer;
	protected Container applicationsContainer;

	public AbstractApplicationCheckComponent(){
		super();
	}

	public abstract void init();
	public abstract String getCheckName();
	
	protected void init(Class<?>[] requiredResourceStrategies){
		this.configurationContainerProvider = new ConfigurationContainerProvider();
		this.configurationContainer = configurationContainerProvider.getConfigurationAsHierarchicalContainer();
		
		this.applicationsComboBox = new ComboBox("Application");
		this.applicationsComboBox.setNullSelectionAllowed(false);
		this.applicationsComboBox.setImmediate(true);
		this.applicationsComboBox.setItemCaptionPropertyId(containerItemCaptionPropertyId);

		applicationsContainer = configurationContainerProvider.createApplicationContainer(requiredResourceStrategies);
		applicationsComboBox.setContainerDataSource(applicationsContainer);
		selectFirstItem(applicationsComboBox, applicationsContainer);
	}

	@SuppressWarnings("unchecked")
	protected BeanItem<de.codecentric.ddt.configuration.Application> getSelectedApplicationBeanItem(){
		BeanItem<de.codecentric.ddt.configuration.Application> selectedApplicationBeanItem = null;
		if(applicationsComboBox.getValue() !=null){			
			selectedApplicationBeanItem = (BeanItem<de.codecentric.ddt.configuration.Application>) applicationsComboBox.getValue();
		}
		return selectedApplicationBeanItem;
	}
	
	protected ComboBox generateResourceComboBox(String name, Class<?> resourceStrategy){
		final ComboBox resourceComboBox = new ComboBox(name);
		resourceComboBox.setNullSelectionAllowed(false);
		resourceComboBox.setImmediate(true);
		final Container resourceContainer = generateResourceContainer(resourceStrategy);
		resourceComboBox.setContainerDataSource(resourceContainer);
		resourceComboBox.setItemCaptionPropertyId(containerItemCaptionPropertyId);
		selectFirstItem(resourceComboBox, resourceContainer);
		applicationsComboBox.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = -7238732779296446832L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				selectFirstItem(resourceComboBox, resourceContainer);
			}
		});
		return resourceComboBox;
	}

	protected Container generateResourceContainer(final Class<?> resourceStrategy){

		final Container resourceContainer = configurationContainerProvider.createResourceContainer(getSelectedApplicationBeanItem(), resourceStrategy);
		applicationsComboBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -5893266547401238457L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				configurationContainerProvider.fillResourceContainer(getSelectedApplicationBeanItem(), resourceContainer, resourceStrategy);
			}
		});
		configurationContainerProvider.getConfigurationAsHierarchicalContainer().addListener(new Container.ItemSetChangeListener() {
			private static final long serialVersionUID = 67080574771191633L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				configurationContainerProvider.fillResourceContainer(getSelectedApplicationBeanItem(), resourceContainer, resourceStrategy);
			}
		});
		return resourceContainer;
	}
	
	protected boolean isContainerFilled(Container container){
		return (container.getItemIds() != null && container.getItemIds().size() > 0);
	}
	
	protected void selectFirstItem(ComboBox combobox, Container container){
		if(isContainerFilled(container)){
			if(!container.containsId(combobox.getValue())){
				combobox.select(getFirstItem(container));
			}
		}
/*
		if(combobox.getValue() == null 
				&& ){
			
		}
		*/
	}
	
	protected Object getFirstItem(Container container){
		Object firstItem = null;
		if(applicationsContainer.getItemIds().iterator().hasNext()){
			firstItem = container.getItemIds().iterator().next();
		}
		return firstItem;		
	}
	
	protected void selectFirstItem(ComboBox comboBox, Collection<?> collection){
		if(collection == null) {
			return;
		}
		if(collection.iterator().hasNext()){
			comboBox.select(collection.iterator().next());
		}
	}

	protected void fillComboBox(ComboBox comboBox, Collection<?> collection){
		if(collection == null) {
			return;
		}
		comboBox.removeAllItems();
		for(Object currentItem: collection){
			comboBox.addItem(currentItem);
		}
	}
}
