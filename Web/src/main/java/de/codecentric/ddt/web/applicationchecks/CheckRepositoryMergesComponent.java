package de.codecentric.ddt.web.applicationchecks;

import java.util.Map;

import javax.enterprise.context.SessionScoped;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.resourcestrategies.repositories.Repository;
import de.codecentric.ddt.resourcestrategies.repositories.RepositoryStrategy;

@SessionScoped
public class CheckRepositoryMergesComponent extends AbstractApplicationCheckComponent{

	private static final long serialVersionUID = 1L;
	
	private Button showMergesButton;
	private Button refreshRepositoryButton;
	
	HorizontalLayout         horizontalLayout;
	VerticalLayout 		  	mainLayout;
	
	private Table branchMergeTable;
	private ComboBox repositoriesComboBox;

	public CheckRepositoryMergesComponent(){
		super();
	}
	
	@Override
	public void init() {
		init(new Class<?>[]{RepositoryStrategy.class});
		
		horizontalLayout = new HorizontalLayout();
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		horizontalLayout.addComponent(applicationsComboBox);
		
		this.repositoriesComboBox = generateResourceComboBox("Repository", RepositoryStrategy.class);
		horizontalLayout.addComponent(repositoriesComboBox);
		
		initializeShowMergesButton();
		horizontalLayout.addComponent(showMergesButton);
		
		initializeRefreshRepositoryButton();
		horizontalLayout.addComponent(refreshRepositoryButton);

		horizontalLayout.setSizeUndefined();

		mainLayout.addComponent(horizontalLayout);
		
		setSizeFull();
		setCompositionRoot(mainLayout);		
	}


	@Override
	public String getCheckName() {
		return "Check Transitive Repository Merges";
	}
	
	private Repository getSelectedRepository(){
		Repository selectedRepository = null;
		if(repositoriesComboBox.getValue() != null){
			@SuppressWarnings("unchecked")
			BeanItem<Resource> selectedRepositortBeanItem = (BeanItem<Resource>) repositoriesComboBox.getValue();
			selectedRepository = new Repository(selectedRepositortBeanItem.getBean());
		}
		return selectedRepository;
	}
	
	/*	
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
	*/

	/*
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
	*/
	
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
				Repository selectedRepository = getSelectedRepository();
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
		Repository selectedRepository = getSelectedRepository();
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
