/*
package de.codecentric.ddt;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import de.codecentric.ddt.repositories.MercurialRepositoryStrategy;
import de.codecentric.ddt.repositories.Repository;

public class App 
{
	private String jpubDirectoryInRepository;
	private String generatedDir;
	
	private Repository icisRepository;
	
	public App(){
		this.icisRepository = new Repository();
		this.icisRepository.setName("ICIS_Mercurial_Repository");
		this.icisRepository.setUrl("http://wgvli39.swlabor.local:8282");
		this.icisRepository.setStrategy(new MercurialRepositoryStrategy());
		
		this.jpubDirectoryInRepository = this.icisRepository.getWorkDirectory() + "/adapter.impl.icis.jpub/src/main/java/de/wgvi/icisplus/adapter/jpub/";
		this.generatedDir = "/Users/user/.DDT_WORK_DIR/ICIS_Oracle_Database/de/wgvi/icisplus/adapter/jpub/";
		init();
	}
	
	private void init(){
		System.out.println("All Java Packages:");
		for(String currentPackage: FileHelper.getAllJavaPackages(icisRepository.getWorkDirectory())){
			System.out.println(currentPackage);
		}
		
		this.icisRepository.getLatestVersion();
		this.icisRepository.setBranch("default");
		this.icisRepository.getLatestVersion();
		
		//DirectoryTest
		for(File currentDirectory: FileHelper.getPackageDirectories(this.icisRepository.getWorkDirectory(), "de.wgvi.icisplus.adapter.jpub")){
			System.out.println(currentDirectory.getAbsolutePath());
		}
		
		for(FileComparison currentComparison: getDifferences(this.jpubDirectoryInRepository, this.generatedDir)){
			if(!currentComparison.getComparisonResult().equals(FileComparisonResult.EQUAL)){
				System.out.println(currentComparison.toString());
			}
		}
	}
			
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
		
		for(File currentFile: referenceDiretoryFiles){
			String otherFilePath = otherDirectory.getPath() + "/" + currentFile.getName();
			File otherFile = new File(otherFilePath);
			differences.add(new FileComparison(currentFile, otherFile));
		}
		return differences;
	}
		
	
		//Shorter
		//Longer
		//Equal
		//Different
	
				
    public static void main( String[] args )
    {
        new App();
    }
}
*/
