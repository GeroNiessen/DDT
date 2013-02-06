/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.codecentric.ddt.configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * FolderComparison compares the files of two folders with each other.
 * Does not traverse into sub-directories!
 * @author Gero Niessen
 */
public class FolderComparison {

    private Set<FileComparison> allComparedFiles;

    public FolderComparison(Set<FileComparison> comparedFiles) {
        this.allComparedFiles = comparedFiles;
    }

    /**
     * A list of all comparisons between all files in the reference folder to all files in the other folder
     * @return 
     */
    public Set<FileComparison> getAllFileComparisons() {
        return allComparedFiles;
    }

    /**
     * A list of a comparisons between all files in the reference folder to all files in the other folder with a certain outcome
     * @param comparisonResult
     * @return 
     */
    public Set<FileComparison> getFileComparisons(FileComparisonResult comparisonResult) {
        Set<FileComparison> comparisons = new HashSet<>();
        for (FileComparison currentFileComparison : allComparedFiles) {
            if (currentFileComparison.getComparisonResult().equals(comparisonResult)) {
                comparisons.add(currentFileComparison);
            }
        }
        return comparisons;
    }

    /**
     * Checks if every single file in the reference folder is equal to the file in the other folder
     * @return 
     */
    public boolean isEveryFileEqual() {
        return allComparedFiles.size() == getFileComparisons(FileComparisonResult.EQUAL).size();
    }

    /**
     * A concatenated string of the names of all reference files in a set of FileComparisons
     * @param fileComparisons
     * @return 
     */
    public String getFileNames(Set<FileComparison> fileComparisons) {
        String fileNames = "";
        for (FileComparison currentFileComparison : fileComparisons) {
            fileNames = fileNames + ", " + currentFileComparison.getReferenceFile().getName();
        }
        return fileNames;
    }

    @Override
    public String toString() {
        String returnedString = ""
                .concat("Folder Comparison Result:\n")
                .concat(String.valueOf(allComparedFiles.size()).concat("\tCompared files in total.\n")
                .concat(String.valueOf(getFileComparisons(FileComparisonResult.EQUAL).size())
                .concat("\tEqual Files:\t\n")
                .concat(String.valueOf(getFileComparisons(FileComparisonResult.MISSING_OTHER_FILE).size())
                .concat("\tMissing files:\t")
                .concat(getFileNames(getFileComparisons(FileComparisonResult.MISSING_OTHER_FILE))).concat("\n")
                .concat(String.valueOf(getFileComparisons(FileComparisonResult.DIFFERENT).size()))
                .concat("\tDifferent Files:\t")
                .concat(getFileNames(getFileComparisons(FileComparisonResult.DIFFERENT)))))).concat("\n");
        return returnedString;
    }
}
