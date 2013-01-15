package de.codecentric.ddt;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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

	public static File[] getPackageDirectories(File startFolder, String packageName){
		String fileSeparator = System.getProperty("file.separator");
		String packagePath = packageName.replace(".", fileSeparator).toLowerCase();
		List<File> returnedFolders = new ArrayList<File>();
		for(File currentFolder: FileHelper.getNestedSubDirectories(startFolder)){
			if(currentFolder.getPath().toLowerCase().endsWith(packagePath) ){
				returnedFolders.add(currentFolder);
			}
		}
		return returnedFolders.toArray(new File[]{});
	}

	public void deleteFolder(File folder) {
		if(folder.exists()){
			File[] files = folder.listFiles();
			if(files!=null) {
				for(File f: files) {
					if(f.isDirectory()) {
						deleteFolder(f);
					} else {
						f.delete();
					}
				}
			}
			folder.delete();
		}
	}
}
