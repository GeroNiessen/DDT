package de.codecentric.ddt.web.applicationchecks;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import de.codecentric.ddt.resourcestrategies.databases.DatabaseStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.RepositoryStrategy;

public class TestCheckComponent extends AbstractApplicationCheckComponent {
	
	private static final long serialVersionUID = 8649477464727498267L;
	private VerticalLayout verticalLayout;
	
	public TestCheckComponent(){
		super();
	}
	
	@Override
	public void init(){
		verticalLayout = new VerticalLayout();
		
		init(new Class<?>[]{RepositoryStrategy.class,DatabaseStrategy.class});
		
		verticalLayout.addComponent(applicationsComboBox);
		
		ComboBox repositoryComboBox = generateResourceComboBox("Repository", RepositoryStrategy.class);
		verticalLayout.addComponent(repositoryComboBox);
		
		ComboBox databaseComboBox = generateResourceComboBox("Database", DatabaseStrategy.class);
		verticalLayout.addComponent(databaseComboBox);
		
		setCompositionRoot(verticalLayout);		
	}
	
	@Override
	public String getCheckName() {
		return "ApplicationCheck";
	}
}
