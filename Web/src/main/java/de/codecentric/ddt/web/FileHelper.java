/*
package de.codecentric.ddt.web;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import de.codecentric.ddt.configuration.FileHelper;

public class FileHelper {
	
	public static File[] getNestedSubDirectories(File directory){
		List<File> allSubDirectories = new ArrayList<File>();
		return getNestedSubfolders(directory, allSubDirectories).toArray(new File[]{});
	}

	private static List<File> getNestedSubfolders(File directory, List<File> allSubDirectories){
		allSubDirectories.add(directory);
		File[] subDirectories = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for(File currentDiretory: subDirectories){
			getNestedSubfolders(currentDiretory, allSubDirectories);
		}
		return allSubDirectories;
	}
	
	public static String[] getAllJavaPackages(File startFolder){
		Set<String> allJavaPackages = new HashSet<String>();
		File[] allSubDirectories = getNestedSubDirectories(startFolder);
		for(File currentFolder: allSubDirectories){
			File[] javaFilesInCurrentFolder = javaFilesInDirectory(currentFolder);
			for(File currentFile: javaFilesInCurrentFolder){
				String currentPackageName = readJavaPackageName(currentFile);
				if(!(currentPackageName.equals("") || currentPackageName == null)){	
					allJavaPackages.add(currentPackageName);
				}
			}
		}
		return allJavaPackages.toArray(new String[]{});
	}
	
	private static String readJavaPackageName(File javaFile){
		String javaPackageName = "";
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(javaFile);
	        while (fileScanner.hasNextLine()) {
	            String currentLine = fileScanner.nextLine();
	            if(currentLine.startsWith("package")){
	            	javaPackageName = currentLine
	            			.replace("package", "")
	            			.replace(";", "")
	            			.trim();
	            	break;
	            }
	        }
	        fileScanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return javaPackageName;
	}
	
	private static File[] javaFilesInDirectory(File currentFolder){
		File[] javaFilesInCurrentFolder = currentFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (!pathname.isDirectory() && pathname.getPath().endsWith(".java"));
			}
		});
		return javaFilesInCurrentFolder;
	}
		
	public static File[] getPackageDirectories(File startFolder, String packageName){
		String fileSeparator = System.getProperty("file.separator");
		String packagePath = packageName.replace(".", fileSeparator).toLowerCase();
		List<File> returnedFolders = new ArrayList<File>();
		for(File currentFolder: FileHelper.getNestedSubDirectories(startFolder)){
			if(currentFolder.getPath().toLowerCase().endsWith(packagePath) ){
				if(javaFilesInDirectory(currentFolder).length > 0){
					returnedFolders.add(currentFolder);
				}
			}
		}
		return returnedFolders.toArray(new File[]{});
	}
}
*/
