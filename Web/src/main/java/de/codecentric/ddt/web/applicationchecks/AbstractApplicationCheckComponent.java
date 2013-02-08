package de.codecentric.ddt.web.applicationchecks;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import de.codecentric.ddt.web.configuration.ConfigurationContainerProvider;
import java.util.Collection;

/**
 * The AbstractApplicationCheckComponent provides a big help for anyone, who wants to write a check for an application.
 * 
 * @author Gero Niessen
 */
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
        
        /**
         * Initializes the Application-check.
         * The initialization should not be done in the constructor,
         * because the DDT-MenuBar component creates an instance of the Application-check implementation
         * in order to get the check name (getCheckName()).
         * If initialization is done in the constructor, the component will be fully loaded, even if it
         * might not be used at all.
         */
	public abstract void init();
        
        /**
         * Gets the name of the Application-check.
         * The name will be displayed in the menu-bar.
         * @return 
         */
	public abstract String getCheckName();
	
        /**
         * Defines the required resource strategies for the application check.
         * The required resource strategies will automatically filter the applications 
         * to the ones, which contain resources that serve ALL requirements.
         * @param requiredResourceStrategies 
         */
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

        /**
         * Gets the application selected by the applicationCombobox.
         * The application is wrapped in a BeanItem to allow data-binding
         * @return 
         */
	@SuppressWarnings("unchecked")
	protected BeanItem<de.codecentric.ddt.configuration.Application> getSelectedApplicationBeanItem(){
		BeanItem<de.codecentric.ddt.configuration.Application> selectedApplicationBeanItem = null;
		if(applicationsComboBox.getValue() !=null){			
			selectedApplicationBeanItem = (BeanItem<de.codecentric.ddt.configuration.Application>) applicationsComboBox.getValue();
		}
		return selectedApplicationBeanItem;
	}
	
        /**
         * Returns a new Combobox.
         * The ComboBox will only contain resources, whose strategies implement the 
         * required ResourceStrategy. AND:
         * The ComboBox will only contain resources, which are part of the application selected 
         * in the applicationsComboBox.
         * The ComboBox will automatically switch the Ressources and update the contents, if the Application is changed.
         * @param name
         * @param resourceStrategy
         * @return 
         */
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

        /**
         * Returns a Vaadin container for resources, whose strategy implement a certain resourceStrategy.
         * The container may used a ComboBox or other Vaadin componenets.
         * The container automatically updates if the configuration changes.
         * The container automatically updates if the selected application changes.
         * @param resourceStrategy
         * @return 
         */
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
