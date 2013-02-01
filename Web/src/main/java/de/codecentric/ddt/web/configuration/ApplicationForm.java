package de.codecentric.ddt.web.configuration;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ApplicationForm extends Form {

	private static final long serialVersionUID = -3460160607133243773L;
	
	private static final String[] visibleItemProperties = new String[]{"name"};
	private static final Set<String> requiredItemProperties = new HashSet<>(Arrays.asList(new String[]{"name"}));
	
	private Button commitButton;
	private Button cancelButton;
	private HierarchicalContainer configurationContainer;
	private Object parent;

	Layout layout;

	public ApplicationForm(HierarchicalContainer configurationContainer, Object parent, BeanItem<de.codecentric.ddt.configuration.Application> application){
		this.configurationContainer = configurationContainer;
		this.parent = parent;

		setWriteThrough(false);
		addStyleName("bordered");
		setCaption("Edit Application");
		initFormFieldFactory();
		setItemDataSource(application);
		setVisibleItemProperties(visibleItemProperties);
		setImmediate(true);
		setValidationVisible(true);
		setValidationVisibleOnCommit(true);
		
		layout = new VerticalLayout();
		layout.addComponent(new Label("Edit Application"));
		layout.addComponent(new Hr());

		commitButton = new Button("Apply");
		initCommitButton();
		getFooter().addComponent(commitButton);
		
		getFooter().addComponent(new Button("Discard", this, "discard"));
		
		cancelButton = new Button("Cancel");
		initCancelButton();
		
		setLayout(layout);

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

				field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
				if(requiredItemProperties.contains(propertyIdString)){
					field.setRequired(true);
					field.setRequiredError("Missing Input");
				}
				field.setWidth(90, UNITS_PERCENTAGE);
				return field;
			}
		});
	}

	private class Hr extends Label {
		private static final long serialVersionUID = 1L;

		Hr() {
			super("<hr/>", Label.CONTENT_XHTML);
		}
	}	
}
