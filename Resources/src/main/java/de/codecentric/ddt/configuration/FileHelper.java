package de.codecentric.ddt.configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * FileHelper helps to get files and folders with extra functionality for the Java world.
 * @author Gero Niessen
 */
public class FileHelper {

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FileHelper.class.getName());
    private static final String fileSeparator = System.getProperty("file.separator");

    /**
     * Gets all directories that nested below the given directory
     * @param directory
     * @return
     */
    public static File[] getNestedSubDirectories(File directory) {
        List<File> allSubDirectories = new ArrayList<>();
        return getNestedSubfolders(directory, allSubDirectories).toArray(new File[]{});
    }

    /**
     * Recursion method for getNestedSubDirectories(File directory)
     * @param directory
     * @param allSubDirectories
     * @return 
     */
    private static List<File> getNestedSubfolders(File directory, List<File> allSubDirectories) {
        allSubDirectories.add(directory);
        File[] subDirectories = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (File currentDiretory : subDirectories) {
            getNestedSubfolders(currentDiretory, allSubDirectories);
        }
        return allSubDirectories;
    }
    
    /**
     * Compares the content (files) of two folders with each other.
     * DOES NOT TRAVERSE INTO SUBDIRECTORIES!
     * @param referenceDirectoryPath
     * @param otherDirectoryPath
     * @return 
     */
    public static FolderComparison getFolderComparison(String referenceDirectoryPath, String otherDirectoryPath){
        return new FolderComparison(getDifferences(referenceDirectoryPath, otherDirectoryPath));
    }

    /**
     * Gets the difference between the files in two folders.
     * See: FileComparison
     * @param referenceDirectoryPath
     * @param otherDirectoryPath
     * @return 
     */
    public static Set<FileComparison> getDifferences(String referenceDirectoryPath, String otherDirectoryPath) {
        LOGGER.info("Comparing files in directory:\n" + referenceDirectoryPath + "\n with files in directory:\n" + otherDirectoryPath);

        Set<FileComparison> differences = new HashSet<>();

        File referenceDirectory = new File(referenceDirectoryPath);
        File otherDirectory = new File(otherDirectoryPath);

        File[] referenceDiretoryFiles = referenceDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".java");
            }
        });

        for (File currentFile : referenceDiretoryFiles) {
            String otherFilePath = otherDirectory.getPath() + fileSeparator + currentFile.getName();
            File otherFile = new File(otherFilePath);
            differences.add(new FileComparison(currentFile, otherFile));
        }
        return differences;
    }

    /**
     * Gets a list of all java packages in all files nested in all sub-folders below the give startFolder
     * @param startFolder
     * @return 
     */
    public static String[] getAllJavaPackages(File startFolder) {
        Set<String> allJavaPackages = new HashSet<>();
        File[] allSubDirectories = getNestedSubDirectories(startFolder);
        for (File currentFolder : allSubDirectories) {
            File[] javaFilesInCurrentFolder = javaFilesInDirectory(currentFolder);
            for (File currentFile : javaFilesInCurrentFolder) {
                String currentPackageName = readJavaPackageName(currentFile);
                if (!(currentPackageName.equals("") || currentPackageName == null)) {
                    allJavaPackages.add(currentPackageName);
                }
            }
        }
        return allJavaPackages.toArray(new String[]{});
    }

    /**
     * Reads the java package of a .java file.
     * @param javaFile
     * @return 
     */
    private static String readJavaPackageName(File javaFile) {
        String javaPackageName = "";
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(javaFile);
            while (fileScanner.hasNextLine()) {
                String currentLine = fileScanner.nextLine();
                if (currentLine.startsWith("package")) {
                    javaPackageName = currentLine
                            .replace("package", "")
                            .replace(";", "")
                            .trim();
                    break;
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            LOGGER.warning("Could not find file! ".concat(javaFile.getPath()));
            e.printStackTrace();
        }
        return javaPackageName;
    }

    /**
     * Get all .java files within a given directory
     * @param currentFolder
     * @return 
     */
    private static File[] javaFilesInDirectory(File currentFolder) {
        File[] javaFilesInCurrentFolder = currentFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (!pathname.isDirectory() && pathname.getPath().endsWith(".java"));
            }
        });
        return javaFilesInCurrentFolder;
    }

    /**
     * Gets a list of all folders containing .java files in a specific java package below a given start-folder.
     * @param startFolder
     * @param packageName
     * @return 
     */
    public static File[] getPackageDirectories(File startFolder, String packageName) {

        String packagePath = packageName.replace(".", fileSeparator).toLowerCase();
        List<File> returnedFolders = new ArrayList<>();
        for (File currentFolder : FileHelper.getNestedSubDirectories(startFolder)) {
            if (currentFolder.getPath().toLowerCase().endsWith(packagePath)) {
                if (javaFilesInDirectory(currentFolder).length > 0) {
                    returnedFolders.add(currentFolder);
                }
            }
        }
        return returnedFolders.toArray(new File[]{});
    }

    /**
     * Purges a folder from the disk, including all subdirectories.
     * BE CAREFUL!
     * @param folder
     */
    public void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
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
