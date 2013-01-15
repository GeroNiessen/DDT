package de.codecentric.ddt.deprecated;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

import de.codecentric.ddt.configuration.Application;
import de.codecentric.ddt.configuration.Resource;

@SessionScoped
public class ApplicationConfigurationComponent extends CustomComponent{

	private static final long serialVersionUID = 5208551974320015755L;
		
	Panel 		  mainPanel  = new Panel();
	private Table allApplicationConfiguration;
	
	public ApplicationConfigurationComponent(List<de.codecentric.ddt.configuration.Application> allApplications){
		mainPanel.setContent(new HorizontalLayout());
		setSizeUndefined();
		getApplications(allApplications);
		mainPanel.addComponent(allApplicationConfiguration);
		setCompositionRoot(mainPanel);
	}
	
	private void getApplications(List<de.codecentric.ddt.configuration.Application> allApplications){
		this.allApplicationConfiguration = new Table("All Applications");
		this.allApplicationConfiguration.addContainerProperty("Application", String.class,  null);
		this.allApplicationConfiguration.addContainerProperty("Resource", String.class,  null);
		this.allApplicationConfiguration.addContainerProperty("Strategy", String.class,  null);
		int i = 0;
		for (de.codecentric.ddt.configuration.Application currentApplication: allApplications){
			for(Resource currentResource: currentApplication.getResources()){
				this.allApplicationConfiguration.addItem(new Object[]{currentApplication.getName(),currentResource.getName(), currentResource.getStrategyName()}, i);
				i++;
			}
			i++;
		}
	}

}
