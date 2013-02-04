package de.codecentric.ddt.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileComparison {

    private File referenceFile;
    private String[] referenceFileContent;
    private File otherFile;
    private String[] otherFileContent;
    private FileComparisonResult comparisonResult;
    private String diff;

    public FileComparison(String referenceFile, String otherFile) {
        this(new File(referenceFile), new File(otherFile));
    }

    public FileComparison(File referenceFile, File otherFile) {
        this.referenceFile = referenceFile;
        this.referenceFileContent = new String[]{};
        this.otherFile = otherFile;
        this.otherFileContent = new String[]{};
        this.comparisonResult = FileComparisonResult.UNKNOWN;
        this.diff = "";
        init();
    }

    private void init() {
        if (!referenceFile.exists() && !otherFile.exists()) {
            this.comparisonResult = FileComparisonResult.MISSING_BOTH_FILES;
            return;
        } else if (!referenceFile.exists()) {
            this.comparisonResult = FileComparisonResult.MISSING_REFERENCE_FILE;
            return;
        } else if (!otherFile.exists()) {
            this.comparisonResult = FileComparisonResult.MISSING_OTHER_FILE;
            return;
        }

        this.referenceFileContent = getFileContent(referenceFile);
        this.otherFileContent = getFileContent(otherFile);
        this.diff = getDiff(this.referenceFileContent, this.otherFileContent);

        if (!this.diff.equals("")) {
            this.comparisonResult = FileComparisonResult.DIFFERENT;
        } else {
            this.comparisonResult = FileComparisonResult.EQUAL;
        }
    }

    public String getDiff() {
        return this.diff;
    }

    public FileComparisonResult getComparisonResult() {
        return this.comparisonResult;
    }

    private String[] getFileContent(File file) {
        List<String> fileContent = new ArrayList<>();
        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (Exception e) {
            }
        }
        return fileContent.toArray(new String[fileContent.size()]);
    }

    private String getDiff(String[] referenceFileContent, String[] otherFileContent) {
        List<String> returnedDiff = new ArrayList<>();

        String[] x = referenceFileContent;
        String[] y = otherFileContent;

        // number of lines of each file
        int M = x.length;
        int N = y.length;

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M + 1][N + 1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (x[i].equals(y[j])) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }

        // recover LCS itself and print out non-matching lines to standard output
        int i = 0, j = 0;
        while (i < M && j < N) {
            if (x[i].equals(y[j])) {
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                returnedDiff.add("< " + x[i++]);
            } else {
                returnedDiff.add("> " + y[j++]);
            }
        }

        // dump out one remainder of one string if the other is exhausted
        while (i < M || j < N) {
            if (i == M) {
                returnedDiff.add("> " + y[j++]);
            } else if (j == N) {
                returnedDiff.add("< " + x[i++]);
            }
        }

        //Join Lines
        String diffString = "";
        for (String currentLine : returnedDiff) {
            if (!currentLine.equals("")) {
                diffString = diffString + currentLine + "\n";
            }
        }
        return diffString;
    }

    public File getReferenceFile() {
        return referenceFile;
    }

    public File getOtherFile() {
        return otherFile;
    }

    @Override
    public String toString() {
        String retunedValue = new String();
        retunedValue = retunedValue
                .concat("\n====================================================================================")
                .concat("\n->ReferenceFile:    ").concat(this.referenceFile.getPath())
                .concat("\n->OtherFile:        ").concat(this.otherFile.getPath())
                .concat("\n->Comparison Result:").concat(this.comparisonResult.toString())
                .concat("\n->Diff:\n").concat(this.diff);
        return retunedValue;
    }
}
