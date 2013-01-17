package de.codecentric.ddt.web;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.SessionScoped;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import de.codecentric.ddt.resourcestrategies.repositories.Repository;
import de.codecentric.ddt.resourcestrategies.repositories.RepositoryStrategy;

@SessionScoped
public class CheckRepositoryMergesComponent extends CustomComponent implements CustomApplicationComponent{

	private static final long serialVersionUID = 1L;
	
	private Button showMergesButton;
	private Button refreshRepositoryButton;
	
	HorizontalLayout         menuPanelLayout;
	VerticalLayout 		  	mainLayout;
	
	private Table branchMergeTable;
	private Repository selectedRepository;
	private ComboBox repositoriesComboBox;
	private de.codecentric.ddt.configuration.Application selectedApplication;

	public CheckRepositoryMergesComponent(){
		super();
	}
	
	public CheckRepositoryMergesComponent(BeanItem<de.codecentric.ddt.configuration.Application> selectedApplication){
		setApplication(selectedApplication);
	}
	
	@Override
	public void setApplication(BeanItem<de.codecentric.ddt.configuration.Application> selectedApplication) {
		this.selectedApplication = selectedApplication.getBean();
		initializeRepositoriesComboBox(this.selectedApplication); //To be removed
		
		menuPanelLayout = new HorizontalLayout();
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		//initializeRepositoriesComboBox();
		menuPanelLayout.addComponent(repositoriesComboBox);
		
		initializeShowMergesButton();
		menuPanelLayout.addComponent(showMergesButton);
		initializeRefreshRepositoryButton();
		menuPanelLayout.addComponent(refreshRepositoryButton);

		menuPanelLayout.setSizeUndefined();

		mainLayout.addComponent(menuPanelLayout);
		
		setSizeFull();
		setCompositionRoot(mainLayout);		
	}
	
	private Set<Repository> getRepositories(){
		Set<Repository> repositories = new HashSet<Repository>();
		for(de.codecentric.ddt.configuration.Resource currentResource: selectedApplication.getResources()){
			if(currentResource.isStrategyExtending(RepositoryStrategy.class)){
				Repository detectedRepository = new Repository(currentResource);
				repositories.add(detectedRepository);
			}
		}
		return repositories;
	}
	
	private void selectFirstItem(ComboBox comboBox, Collection<?> collection){
		if(collection.iterator().hasNext()){
			comboBox.select(collection.iterator().next());
		}
	}
	
	private void initializeRepositoriesComboBox(de.codecentric.ddt.configuration.Application selectedApplication){
		
		Set<Repository> repositories = getRepositories();
		repositoriesComboBox = new ComboBox("Repository",repositories);
		repositoriesComboBox.setNullSelectionAllowed(false);
		repositoriesComboBox.setImmediate(true);
		selectFirstItem(repositoriesComboBox, repositories);
		selectedRepository = (Repository) repositoriesComboBox.getValue();
		repositoriesComboBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -5893266547401238457L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				selectedRepository = (Repository) repositoriesComboBox.getValue();
			}
		});
	}
	
	private void initializeShowMergesButton(){
		showMergesButton = new Button("Show Merges");		
		showMergesButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 3426574344359999269L;

			public void buttonClick(ClickEvent event) {
				if(branchMergeTable != null){
					mainLayout.removeComponent(branchMergeTable);
				}
				initializeBranchMergeTable();
				fillBranchMergesTable();
				mainLayout.addComponent(branchMergeTable);
				mainLayout.setExpandRatio(branchMergeTable, 1.0f);
			}
		});
	}
	
	private void initializeRefreshRepositoryButton(){
		refreshRepositoryButton = new Button("Refresh Repository");	
		refreshRepositoryButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 2387899027450222221L;

			public void buttonClick(ClickEvent event) {
				if(selectedRepository != null){
					selectedRepository.getLatestVersion();
				}
			}
		});
	}
		
	private void initializeBranchMergeTable(){
		if(this.branchMergeTable != null){
			mainLayout.removeComponent(this.branchMergeTable);
		}
		this.branchMergeTable = new Table("All Branches");
		this.branchMergeTable.setSizeFull();
		this.branchMergeTable.addContainerProperty("Branch", String.class, null);
		this.branchMergeTable.addContainerProperty("Merged Branch", String.class, null);
		this.branchMergeTable.addContainerProperty("Merged Version", Integer.class, null);
		this.branchMergeTable.addContainerProperty("Latest Version", Integer.class, null);
		this.branchMergeTable.setDescription(
			    "<h2>THIS TABLE SHOWS THE (TRANSITIVE) BRANCH MERGES."+
			    	    "</h2>"+
			    	    "<ul>"+
			    	    "  <li>The first \"Branch\" colum shows the point of view</li>"+
			    	    "  <li>The second \"Merged Branch\" colum shows the branch, which has been einther directly or tarnsitive merged into the branch in the first colum</li>"+
			    	    "  <li>The thrid colum shows which version of the branch in the second colum has been merged to the branch in the first colum</li>"+
			    	    "  <li>The fourth colum shows the latest available version of the branch in the the second colum</li>"+
			    	    "</ul>" +
			    	    "<h2> Rows are marked in green, if the the branch in the branch in the first colum contains the very latest version of the branch in the second colum.</h2>"
			    	    );
		this.branchMergeTable.setCellStyleGenerator(new Table.CellStyleGenerator(){
			
			private static final long serialVersionUID = -8831755191981409841L;

			@Override
			public String getStyle(Object itemId, Object propertyId) {
				if (propertyId == null) {
					// Styling for row
					Item item = branchMergeTable.getItem(itemId);
					Integer mergedVersion = (Integer) item.getItemProperty("Merged Version").getValue();
					Integer latestVersion = (Integer) item.getItemProperty("Latest Version").getValue();
					if (mergedVersion.equals(latestVersion)) {
						return "highlight-green";
					} else if (!mergedVersion.equals(latestVersion)) {
						return "highlight-red";
					} else {
						return null;
					}
				} else {
					// styling for column propertyId
					return null;
				}
			}});
	}

	private void fillBranchMergesTable(){
		Map<String, Integer> latestBranchRevisions = selectedRepository.getLatestBranchRevisions();
		Map<String, Map<String, Integer>> latestBranchMerges = selectedRepository.getLatestBranchMerges();
		int i = 1;
		for(String currentBranch : latestBranchMerges.keySet()){
			Map<String, Integer> mergedBranchs = latestBranchMerges.get(currentBranch);
			for(String mergedBranch: mergedBranchs.keySet()){
				Integer mergedVersion = mergedBranchs.get(mergedBranch);
				Integer latestVersion = latestBranchRevisions.get(mergedBranch);
				this.branchMergeTable.addItem(new Object[]{currentBranch, mergedBranch, mergedVersion, latestVersion}, new Integer(i));
				i++;
			}
		}
	}
}
