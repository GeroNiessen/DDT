package de.codecentric.ddt.web.applicationchecks;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import de.codecentric.ddt.configuration.FileHelper;
import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.resourcestrategies.databases.Database;
import de.codecentric.ddt.resourcestrategies.databases.DatabaseStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.Repository;
import de.codecentric.ddt.resourcestrategies.repositories.RepositoryStrategy;
import de.codecentric.ddt.web.MyVaadinApplication;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CheckProxyClassesComponent extends AbstractApplicationCheckComponent {

	private static final long serialVersionUID = 909079633888501964L;

	private static final String defaultPackageName =  "de.wgvi.icisplus.adapter.jpub";
	private static final boolean enableDisableComponentsFeature = true;
	private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(CheckProxyClassesComponent.class .getName());

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
	private VerticalLayout verticalLayout;

	public CheckProxyClassesComponent(){
		super();
	}

	@Override
	public void init(){
		init(new Class<?>[]{RepositoryStrategy.class, DatabaseStrategy.class});

		verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();

		verticalLayout.addComponent(applicationsComboBox);

		this.repositoriesComboBox = generateResourceComboBox("Repository", RepositoryStrategy.class);
		initRepositoriesComboBoxListeners();

		this.updateRepositoryButton = new Button("Update Repository");
		initUpdateRepositoryButtonListeners();

		HorizontalLayout horizontalRepositoryLayout = new HorizontalLayout();
		horizontalRepositoryLayout.addComponent(this.repositoriesComboBox);
		horizontalRepositoryLayout.addComponent(this.updateRepositoryButton);
		verticalLayout.addComponent(horizontalRepositoryLayout);

		this.branchesComboBox = new ComboBox("Branch");
		this.branchesComboBox.setNullSelectionAllowed(false);
		this.branchesComboBox.setImmediate(true);
		fillBranchesComboBox();
		initBranchesComboBoxListeners();
		verticalLayout.addComponent(branchesComboBox);

		this.packagesComboBox = new ComboBox("Package");
		this.packagesComboBox.setNullSelectionAllowed(false);
		this.packagesComboBox.setImmediate(true);
		fillPackagesComboBox();
		initPackagesComboBoxListeners();
		verticalLayout.addComponent(packagesComboBox);

		this.packagesDirectoriesComboBox = new ComboBox("Package Directory");
		this.packagesDirectoriesComboBox.setNullSelectionAllowed(false);
		fillPackagesDirectoriesComboBox();
		verticalLayout.addComponent(packagesDirectoriesComboBox);

		HorizontalLayout horizontalDatabaseLayout = new HorizontalLayout();
		this.databasesComboBox = generateResourceComboBox("Database", DatabaseStrategy.class); 
		horizontalDatabaseLayout.addComponent(this.databasesComboBox);

		this.updateDatabaseButton = new Button("Generate Proxy Classes Database (Takes roughly a minute)");
		initUpdateDatabaseButtonListeners();
		horizontalDatabaseLayout.addComponent(this.updateDatabaseButton);

		this.updateDatabaseIndicator = new ProgressIndicator(new Float(0.0));
		this.updateDatabaseIndicator.setPollingInterval(500);
		this.updateDatabaseIndicator.setVisible(false);
		horizontalDatabaseLayout.addComponent(updateDatabaseIndicator);

		verticalLayout.addComponent(horizontalDatabaseLayout);

		initGenerateFileComparisonButton();
		verticalLayout.addComponent(generateFileComparisons);

		initFileComparisonTable();
		verticalLayout.addComponent(fileComparisonTable);
		verticalLayout.setExpandRatio(fileComparisonTable, 1.0f);

		setCompositionRoot(verticalLayout);		
	}

	@Override
	public String getCheckName() {
		return "Check Repository against Database";
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

	private Database getSelectedDatabase(){
		Database selectedDatabase = null;
		if(databasesComboBox.getValue() != null){
			@SuppressWarnings("unchecked")
			BeanItem<Resource> selectedDatabaseBeanItem = (BeanItem<Resource>) databasesComboBox.getValue();
			selectedDatabase = new Database(selectedDatabaseBeanItem.getBean());
		}
		return selectedDatabase;		
	}

	private void initUpdateDatabaseButtonListeners(){
		this.updateDatabaseButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -2838399923012330788L;

			@Override
			public void buttonClick(ClickEvent event) {
				updateDatabaseIndicator.setVisible(true);
				final GenerateProxyClassesThread thread = new GenerateProxyClassesThread();
				thread.start();
				updateDatabaseButton.setVisible(false);
			}
		});
	}

	private void initRepositoriesComboBoxListeners(){
		repositoriesComboBox.addListener(new Property.ValueChangeListener() {			
			private static final long serialVersionUID = -7039986558899626184L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(repositoriesComboBox.getValue() != null){
					Repository selectedRepository = getSelectedRepository();
					if(selectedRepository.getLatestRepositoryRevision() == -1){
						//Ask to update repo 
					}
					if(selectedRepository.getLatestRepositoryRevision() != -1){
						fillBranchesComboBox();
					} else {
						branchesComboBox.select(null);
					}
				}
			}
		});		
	}

	private void initUpdateRepositoryButtonListeners(){
		updateRepositoryButton.addListener(new Button.ClickListener() {		

			private static final long serialVersionUID = -2686145724959162535L;

			@Override
			public void buttonClick(ClickEvent event) {
				Repository selectedRepository = getSelectedRepository();
				if(selectedRepository != null){
					selectedRepository.getLatestVersion();
					fillUpdateRepositoryButton();
				}
			}
		});
	}

	private void fillUpdateRepositoryButton(){
		Repository selectedRepository = getSelectedRepository();
		if(selectedRepository != null){
			int repositoryVersion = selectedRepository.getLatestRepositoryRevision();
			updateRepositoryButton.setCaption("Update Repository (Current Version:" + repositoryVersion + ")");	
		} else {
			LOGGER.warning("No repository has been selected!");
		}
	}

	private void fillBranchesComboBox(){
		List<String> allBranchesInSelectedRepository = new ArrayList<String>();
		Repository selectedRepository = getSelectedRepository();
		if(selectedRepository != null){
			allBranchesInSelectedRepository.addAll(selectedRepository.getBranches());
			fillComboBox(branchesComboBox, allBranchesInSelectedRepository);
			String currentBranch = selectedRepository.getCurrentBranch();
			branchesComboBox.select(currentBranch);
			LOGGER.info("Detected " + allBranchesInSelectedRepository + " branches");
		} else {
			branchesComboBox.removeAllItems();
		}
	}

	private void initBranchesComboBoxListeners(){
		branchesComboBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 2591893021336821682L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Repository selectedRepository = getSelectedRepository();
				if(branchesComboBox.getValue() != null && selectedRepository != null){
					String selectedBranch = (String) branchesComboBox.getValue();
					if (enableDisableComponentsFeature) packagesComboBox.setEnabled(false);
					selectedRepository.setBranch(selectedBranch);
					fillPackagesComboBox();
				}
			}
		});		
	}

	private void fillPackagesComboBox(){
		Repository selectedRepository = getSelectedRepository();
		if(selectedRepository != null){
		List<String> allPackagesInBranch = Arrays.asList(FileHelper.getAllJavaPackages(selectedRepository.getWorkDirectory()));

		if (enableDisableComponentsFeature) packagesComboBox.setEnabled(true);
		fillComboBox(packagesComboBox, allPackagesInBranch);
		if(allPackagesInBranch.contains(defaultPackageName)){
			packagesComboBox.select(defaultPackageName);
		} else {
			selectFirstItem(packagesComboBox, allPackagesInBranch);
		}
		LOGGER.info("Detected " + allPackagesInBranch.size() + " Java Packages");
		} else {
			LOGGER.warning("No Repository has been selected to read the packages from!");
		}
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
		Repository selectedRepository = getSelectedRepository();
		String selectedPackage = (String) packagesComboBox.getValue();
		if(selectedPackage != null){
			List<File> packageDirectories = Arrays.asList(FileHelper.getPackageDirectories(selectedRepository.getWorkDirectory(), selectedPackage));
			if (enableDisableComponentsFeature) packagesDirectoriesComboBox.setEnabled(true);
			fillComboBox(packagesDirectoriesComboBox, packageDirectories);
			selectFirstItem(packagesDirectoriesComboBox, packageDirectories);
			LOGGER.info("Detected " + packageDirectories.size() + " folder candidates for Java package: " + selectedPackage);
		} else {
			packagesDirectoriesComboBox.removeAllItems();
		}
	}

	private void initGenerateFileComparisonButton(){
		this.generateFileComparisons = new Button("Compare package folder with generated proxy classes");
		this.generateFileComparisons.addListener(new Button.ClickListener() {		

			private static final long serialVersionUID = -2838399923012330788L;

			@Override
			public void buttonClick(ClickEvent event) {
				File existingProxyClassesFolder = (File) packagesDirectoriesComboBox.getValue();
				String packageName =  (String) packagesComboBox.getValue();
				Database selectedDatabase = getSelectedDatabase();
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
		fileComparisonTable.setSizeFull();
		fileComparisonTable.addContainerProperty("File", String.class, null);
		fileComparisonTable.addContainerProperty("Result", String.class, null);
		fileComparisonTable.addContainerProperty("Difference", FileComparisonComponent.class, null);
		fileComparisonTable.setColumnExpandRatio("Difference", 1);
		verticalLayout.addComponent(fileComparisonTable);
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

	private class GenerateProxyClassesThread extends Thread {
		public void run () {
			double current = 0.0;

			Database selectedDatabase = getSelectedDatabase();
			String selectedPackageName = (String) packagesComboBox.getValue();
			if(selectedDatabase != null && selectedPackageName !=null){
				selectedDatabase.generateProxyClasses(selectedPackageName);
			} else {
				LOGGER.warning("Please select a database and a package name, first!");
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
		LOGGER.info("Comparing files in directory:\n" + referenceDirectoryPath + "\n with files in directory:\n" + otherDirectoryPath);

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

				@Override
				public void buttonClick(ClickEvent event) {
					diffWindow = new Window("Diff: " + fileComparison.getReferenceFile().getName());
					//diffWindow.setSizeFull();
					diffWindow.setWidth("70%");
					diffWindow.setHeight("70%");

					Label repositoryLabel = new Label("Repository");
					repositoryLabel.addStyleName(repositorySytle);
					diffWindow.addComponent(repositoryLabel);

					Label databaseLabel = new Label("Database");
					databaseLabel.addStyleName(databaseSytle);			    	
					diffWindow.addComponent(databaseLabel);  	

					diffWindow.addComponent(new Label("<<<<<<<<<< DIFF >>>>>>>>>>"));

					addDiffs();

					Layout completeFilesPanelLayout = new HorizontalLayout();
					completeFilesPanelLayout.setSizeFull();
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
			fileContentPanel.setSizeFull();
			if(headLine != null){
				fileContentPanel.addComponent(new Label(headLine));
			}
			try {
				//
				Scanner fileScanner = new Scanner(inputFile, "UTF-8");
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
