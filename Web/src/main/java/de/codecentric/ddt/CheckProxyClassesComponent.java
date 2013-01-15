package de.codecentric.ddt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.codecentric.ddt.repositories.Repository;
import de.codecentric.ddt.repositories.RepositoryStrategy;

public class CheckProxyClassesComponent extends CustomComponent implements CustomApplicationComponent {

	private static final long serialVersionUID = 909079633888501964L;
	private de.codecentric.ddt.Application selectedApplication;
	private static final String defaultPackageName =  "de.wgvi.icisplus.adapter.jpub";
	private static final boolean enableDisableComponentsFeature = true;

	private Button updateRepositoryButton;
	private Button updateDatabaseButton;
	private ProgressIndicator updateDatabaseIndicator;
	
	private ComboBox repositoriesComboBox;
	private ComboBox branchesComboBox;
	private ComboBox packagesComboBox;
	private ComboBox packagesDirectoriesComboBox;
	private ComboBox databasesComboBox;
	private Button generateFileComparisons;
	private Table fileComparisonTable;
	private Panel contentPanel;

	public CheckProxyClassesComponent(){
		super();
	}

	public CheckProxyClassesComponent(BeanItem<de.codecentric.ddt.Application> selectedApplicationBeanItem){
		setApplication(selectedApplicationBeanItem);
	}

	@Override
	public void setApplication(BeanItem<de.codecentric.ddt.Application> selectedApplicationBeanItem) {
		this.selectedApplication = selectedApplicationBeanItem.getBean();

		contentPanel = new Panel();
		contentPanel.setContent(new VerticalLayout());
		contentPanel.setSizeFull();
						
		this.repositoriesComboBox = new ComboBox("Repository");
		this.repositoriesComboBox.setNullSelectionAllowed(false);
		this.repositoriesComboBox.setImmediate(true);
		fillRepositoriesComboBox();
		initRepositoriesComboBoxListeners();
		
		this.updateRepositoryButton = new Button("Update Repository");
		initUpdateRepositoryButtonListeners();
		
		Panel repositoryPanel = new Panel();
		repositoryPanel.setContent(new HorizontalLayout());
		repositoryPanel.addComponent(this.repositoriesComboBox);
		repositoryPanel.addComponent(this.updateRepositoryButton);
		contentPanel.addComponent(repositoryPanel);

		this.branchesComboBox = new ComboBox("Branch");
		this.branchesComboBox.setNullSelectionAllowed(false);
		this.branchesComboBox.setImmediate(true);
		fillBranchesComboBox();
		initBranchesComboBoxListeners();
		contentPanel.addComponent(branchesComboBox);

		this.packagesComboBox = new ComboBox("Package");
		this.packagesComboBox.setNullSelectionAllowed(false);
		this.packagesComboBox.setImmediate(true);
		fillPackagesComboBox();
		initPackagesComboBoxListeners();
		contentPanel.addComponent(packagesComboBox);

		this.packagesDirectoriesComboBox = new ComboBox("Package Directory");
		this.packagesDirectoriesComboBox.setNullSelectionAllowed(false);
		fillPackagesDirectoriesComboBox();
		contentPanel.addComponent(packagesDirectoriesComboBox);

		Panel databasePanel = new Panel();
		databasePanel.setContent(new HorizontalLayout());
		this.databasesComboBox = new ComboBox("Database");
		this.databasesComboBox.setNullSelectionAllowed(false);
		fillDatabasesComboBox();
		databasePanel.addComponent(this.databasesComboBox);
		
		this.updateDatabaseButton = new Button("Generate Proxy Classes Database (Takes roughly a minute)");
		initUpdateDatabaseButtonListeners();
		databasePanel.addComponent(this.updateDatabaseButton);
		
		this.updateDatabaseIndicator = new ProgressIndicator(new Float(0.0));
		this.updateDatabaseIndicator.setPollingInterval(500);
		this.updateDatabaseIndicator.setVisible(false);
		databasePanel.addComponent(updateDatabaseIndicator);
		
		contentPanel.addComponent(databasePanel);

		initGenerateFileComparisonButton();
		contentPanel.addComponent(generateFileComparisons);

		initFileComparisonTable();
		contentPanel.addComponent(fileComparisonTable);

		setCompositionRoot(contentPanel);		
	}
	
	private Set<Repository> getRepositories(){
		Set<Repository> repositories = new HashSet<Repository>();
		for(de.codecentric.ddt.Resource<?> currentResource: selectedApplication.getResources()){
			if(currentResource.isStrategyExtending(RepositoryStrategy.class)){
				Repository detectedRepository = new Repository(currentResource);
				repositories.add(detectedRepository);
			}
		}
		return repositories;
	}
	
	private Set<Database> getDatabases(){
		Set<Database> repositories = new HashSet<Database>();
		for(de.codecentric.ddt.Resource<?> currentResource: selectedApplication.getResources()){
			if(currentResource.isStrategyExtending(DatabaseStrategy.class)){
				Database detectedRepository = new Database(currentResource);
				repositories.add(detectedRepository);
			}
		}
		return repositories;
	}


	private void fillDatabasesComboBox(){
		Set<Database> databaseResources = getDatabases();
		fillComboBox(databasesComboBox, databaseResources);
		selectFirstItem(databasesComboBox, databaseResources);
	}
	
	private void initUpdateDatabaseButtonListeners(){
		this.updateDatabaseButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -2838399923012330788L;

			@Override
			public void buttonClick(ClickEvent event) {
				updateDatabaseIndicator.setVisible(true);
				final GenerateProxyClassesThread thread = new GenerateProxyClassesThread();
		        thread.start();
		        
		        // The button hides until the work is done.
		        updateDatabaseButton.setVisible(false);
		        /*
				Database selectedDatabase = (Database) databasesComboBox.getValue();
				String selectedPackageName = (String) packagesComboBox.getValue();
				if(selectedDatabase != null && selectedPackageName !=null){
					selectedDatabase.generateProxyClasses(selectedPackageName);
				} else {
					showTrayNotification("Please select a database and a package name, first!");
				}
				*/
			}
		});
	}

	private void fillRepositoriesComboBox(){
		Set<Repository> repositories = getRepositories();
		fillComboBox(repositoriesComboBox, repositories);
		selectFirstItem(repositoriesComboBox, repositories);
		showTrayNotification("Detected " + repositories.size() + " repositories");
	}

	private void initRepositoriesComboBoxListeners(){
		repositoriesComboBox.addListener(new Property.ValueChangeListener() {			
			private static final long serialVersionUID = -7039986558899626184L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(repositoriesComboBox.getValue() != null){
					fillBranchesComboBox();
				}
			}
		});		
	}
	
	private void initUpdateRepositoryButtonListeners(){
		updateRepositoryButton.addListener(new Button.ClickListener() {		
			
			private static final long serialVersionUID = -2686145724959162535L;

			@Override
			public void buttonClick(ClickEvent event) {
				Repository selectedRepository = (Repository) repositoriesComboBox.getValue();
				if(selectedRepository != null){
					selectedRepository.getLatestVersion();
					fillUpdateRepositoryButton();
				}
			}
		});
	}
	
	private void fillUpdateRepositoryButton(){
		Repository selectedRepository = (Repository) repositoriesComboBox.getValue();
		if(selectedRepository != null){
			int repositoryVersion = selectedRepository.getLatestRepositoryRevision();
			updateRepositoryButton.setCaption("Update Repository (Current Version:" + repositoryVersion + ")");	
		}
	}

	private void fillBranchesComboBox(){
		List<String> allBranchesInSelectedRepository = new ArrayList<String>();
		Repository selectedRepository = (Repository) repositoriesComboBox.getValue();
		allBranchesInSelectedRepository.addAll(selectedRepository.getBranches());
		fillComboBox(branchesComboBox, allBranchesInSelectedRepository);
		String currentBranch = selectedRepository.getCurrentBranch();
		branchesComboBox.select(currentBranch);
		showTrayNotification("Detected " + allBranchesInSelectedRepository + " branches");
	}

	private void initBranchesComboBoxListeners(){
		branchesComboBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 2591893021336821682L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(branchesComboBox.getValue() != null){
					Repository selectedRepository = (Repository) repositoriesComboBox.getValue();
					String selectedBranch = (String) branchesComboBox.getValue();
					if (enableDisableComponentsFeature) packagesComboBox.setEnabled(false);
					selectedRepository.setBranch(selectedBranch);
					showTrayNotification("Switched repository to branch: " + selectedBranch);
					fillPackagesComboBox();
				}
			}
		});		
	}

	private void fillPackagesComboBox(){
		Repository selectedRepository = (Repository) repositoriesComboBox.getValue();
		List<String> allPackagesInBranch = Arrays.asList(FileHelper.getAllJavaPackages(selectedRepository.getWorkDirectory()));

		if (enableDisableComponentsFeature) packagesComboBox.setEnabled(true);
		fillComboBox(packagesComboBox, allPackagesInBranch);
		if(allPackagesInBranch.contains(defaultPackageName)){
			packagesComboBox.select(defaultPackageName);
		} else {
			selectFirstItem(packagesComboBox, allPackagesInBranch);
		}
		showTrayNotification("Detected " + allPackagesInBranch.size() + " Java Packages");
	}

	private void initPackagesComboBoxListeners(){
		packagesComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		packagesComboBox.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = -4677536056632327140L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (enableDisableComponentsFeature) packagesDirectoriesComboBox.setEnabled(false);
				if(packagesComboBox.getValue() != null){
					fillPackagesDirectoriesComboBox();
				}
			}
		});		
	}

	private void fillPackagesDirectoriesComboBox(){
		Repository selectedRepository = (Repository) repositoriesComboBox.getValue();
		String selectedPackage = (String) packagesComboBox.getValue();
		List<File> packageDirectories = Arrays.asList(FileHelper.getPackageDirectories(selectedRepository.getWorkDirectory(), selectedPackage));
		if (enableDisableComponentsFeature) packagesDirectoriesComboBox.setEnabled(true);
		fillComboBox(packagesDirectoriesComboBox, packageDirectories);
		selectFirstItem(packagesDirectoriesComboBox, packageDirectories);
		showTrayNotification("Detected " + packageDirectories.size() + " folder candidates for Java package: " + selectedPackage);
	}

	private void initGenerateFileComparisonButton(){
		this.generateFileComparisons = new Button("Gererate Diff");
		this.generateFileComparisons.addListener(new Button.ClickListener() {		

			private static final long serialVersionUID = -2838399923012330788L;

			@Override
			public void buttonClick(ClickEvent event) {
				File existingProxyClassesFolder = (File) packagesDirectoriesComboBox.getValue();
				String packageName =  (String) packagesComboBox.getValue();
				Database selectedDatabase = (Database) databasesComboBox.getValue();
				if(existingProxyClassesFolder != null && packageName != null && selectedDatabase != null){
					String fileSeparator = java.io.File.separator;
					String generatedProxyClassesPath = selectedDatabase.getWorkDirectory() + fileSeparator + packageName.replace(".", fileSeparator);
					List<FileComparison> comparedFiles = getDifferences(existingProxyClassesFolder.getPath(), generatedProxyClassesPath);
					fillFileComparisonTable(comparedFiles);
				}
			}
		});
	}

	private void initFileComparisonTable(){
		fileComparisonTable = new Table("Generated Diff:");
		fileComparisonTable.addContainerProperty("File", String.class, null);
		fileComparisonTable.addContainerProperty("Result", String.class, null);
		fileComparisonTable.addContainerProperty("Difference", FileComparisonComponent.class, null);
		fileComparisonTable.setColumnExpandRatio("Difference", 1);
		contentPanel.addComponent(fileComparisonTable);
	}

	private void fillFileComparisonTable(List<FileComparison> comparedFiles){
		fileComparisonTable.removeAllItems();
		
		int i = 1;
		for(FileComparison currentFileComparison: comparedFiles){
			FileComparisonComponent currentDiffWindowComponent = null;
			if(!currentFileComparison.getDiff().equals("")){
				currentDiffWindowComponent = new FileComparisonComponent(currentFileComparison);
			}
			fileComparisonTable.addItem(new Object[]{
					currentFileComparison.getReferenceFile().getName(), 
					currentFileComparison.getComparisonResult(),
					currentDiffWindowComponent}, new Integer(i));
			i++;
		}
		fileComparisonTable.sort(new String[] { "Result", "File" }, new boolean[] { true, true });
	}
	
	//===============================================
	
	private void showTrayNotification(String message){
		Notification notification = new Notification("", Notification.TYPE_HUMANIZED_MESSAGE);
		notification.setPosition(Notification.POSITION_BOTTOM_RIGHT);
		notification.setDescription(message);
		notification.setDelayMsec(200);
		MyVaadinApplication.getInstance().showNotification(notification);
	}

	private void selectFirstItem(ComboBox comboBox, Collection<?> collection){
		if(collection == null) {
			return;
		}
		if(collection.iterator().hasNext()){
			comboBox.select(collection.iterator().next());
		}
	}

	private void fillComboBox(ComboBox comboBox, Collection<?> collection){
		if(collection == null) {
			return;
		}
		comboBox.removeAllItems();
		for(Object currentItem: collection){
			comboBox.addItem(currentItem);
		}
	}


	//===============================================
	
	class GenerateProxyClassesThread extends Thread {
	    public void run () {
	        double current = 0.0;

	        Database selectedDatabase = (Database) databasesComboBox.getValue();
			String selectedPackageName = (String) packagesComboBox.getValue();
			if(selectedDatabase != null && selectedPackageName !=null){
				selectedDatabase.generateProxyClasses(selectedPackageName);
			} else {
				showTrayNotification("Please select a database and a package name, first!");
			}

	        while (true) {
	            // Do some "heavy work"
	            try {
	                sleep(50); // Sleep for 50 milliseconds
	            } catch (InterruptedException e) {}
	            
	            // Show that you have made some progress:
	            // grow the progress value until it reaches 1.0.
	            current += 0.01;
	            if (current>1.0)
	                updateDatabaseIndicator.setValue(new Float(1.0));
	            else 
	                updateDatabaseIndicator.setValue(new Float(current));
	            
	            // After all the "work" has been done for a while,
	            // take a break.
	            if (current > 1.2) {
	                // Restore the state to initial.
	                updateDatabaseIndicator.setValue(new Float(0.0));
	                updateDatabaseButton.setVisible(true);
	                updateDatabaseIndicator.setVisible(false);
	                break;
	            }
	        }
	    }
	}
	
	//===============================================
	

	private List<FileComparison> getDifferences(String referenceDirectoryPath, String otherDirectoryPath){
		List<FileComparison> differences = new ArrayList<FileComparison>();

		File referenceDirectory = new File(referenceDirectoryPath);
		File otherDirectory = new File(otherDirectoryPath);

		File[] referenceDiretoryFiles = referenceDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".java");
			}
		});

		String fileSeparator = System.getProperty("file.separator");
		for(File currentFile: referenceDiretoryFiles){
			String otherFilePath = otherDirectory.getPath() + fileSeparator + currentFile.getName();
			File otherFile = new File(otherFilePath);
			differences.add(new FileComparison(currentFile, otherFile));
		}
		return differences;
	}

	private class FileComparisonComponent extends CustomComponent {

		private static final long serialVersionUID = -690166502718121321L;

		private static final String repositorySytle = "diff-this";
		private static final String databaseSytle = "diff-other";

		private Panel buttonPanel;
		private Window diffWindow;
		private FileComparison fileComparison;

		public FileComparisonComponent(FileComparison fileComparison){
			this.fileComparison = fileComparison;
			init();
		}

		public void init() {

			buttonPanel = new Panel(new HorizontalLayout());    	
			buttonPanel.addComponent(new Button("Compare Files", new Button.ClickListener(){

				private static final long serialVersionUID = 3394122717506758778L;

				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					diffWindow = new Window("Diff: " + fileComparison.getReferenceFile().getName());
					diffWindow.setSizeFull();
					//diffWindow.setWidth("98%");
					//diffWindow.setHeight("98%");

					Label repositoryLabel = new Label("Repository");
					repositoryLabel.addStyleName(repositorySytle);
					diffWindow.addComponent(repositoryLabel);

					Label databaseLabel = new Label("Database");
					databaseLabel.addStyleName(databaseSytle);			    	
					diffWindow.addComponent(databaseLabel);  	

					diffWindow.addComponent(new Label("<<<<<<<<<< DIFF >>>>>>>>>>"));

					addDiffs();

					Layout completeFilesPanelLayout = new HorizontalLayout();
					//completeFilesPanelLayout.setWidth("98%");
					Panel completeFilesPanel = new Panel(completeFilesPanelLayout);

					Panel repositoryFilePanel = getFileContentPanel("<<<<<<<<<< REPOSITORY FILE >>>>>>>>>>", fileComparison.getReferenceFile(), repositorySytle);			    	
					completeFilesPanel.addComponent(repositoryFilePanel);

					Panel databaseFilePanel = getFileContentPanel("<<<<<<<<<< DATABASE FILE >>>>>>>>>>", fileComparison.getOtherFile(), databaseSytle);
					completeFilesPanel.addComponent(databaseFilePanel);

					diffWindow.addComponent(completeFilesPanel);

					MyVaadinApplication.getInstance().getMainWindow().addWindow(diffWindow);
				}

			}));    	
			setCompositionRoot(buttonPanel);
		}

		private Panel getFileContentPanel(String headLine, File inputFile, String style){
			Panel fileContentPanel = new Panel(new VerticalLayout());
			if(headLine != null){
				fileContentPanel.addComponent(new Label(headLine));
			}
			try {
				Scanner fileScanner = new Scanner(fileComparison.getOtherFile(), "UTF-8");
				while(fileScanner.hasNext()){
					Label databaseLineLabel = new Label(fileScanner.nextLine());
					databaseLineLabel.addStyleName(style);
					fileContentPanel.addComponent(databaseLineLabel);
				}
				fileScanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return fileContentPanel;
		}

		private void addDiffs(){
			String[] diffLines = fileComparison.getDiff().split("\n");
			for(String diffLine: diffLines){
				Label diffLineLabel = new Label(diffLine);
				if(diffLine.startsWith("<")){
					diffLineLabel.addStyleName(repositorySytle);
				} else if(diffLine.startsWith(">")){
					diffLineLabel.addStyleName(databaseSytle);
				}
				diffLineLabel.setSizeUndefined();
				diffWindow.addComponent(diffLineLabel);
			}
		}
	}
}
