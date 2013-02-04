package de.codecentric.ddt.commandlineclient;

import de.codecentric.ddt.configuration.ConnectionTestHelper;
import de.codecentric.ddt.configuration.FileComparison;
import de.codecentric.ddt.configuration.FileHelper;
import de.codecentric.ddt.mercurialrepositorystrategy.MercurialRepositoryStrategy;
import de.codecentric.ddt.resourcestrategies.databases.Database;
import de.codecentric.ddt.resourcestrategies.databases.oracle.OracleDatabaseStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.Repository;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 */
public class CheckRepositoryProxyClasses {

    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(CheckRepositoryProxyClasses.class.getName());
    private HashMap<String, Set<String>> modeParameters;
    private static final String updateNoneMode = "updatenone";
    private static final Set<String> updateNoneModeParameters = new HashSet<>(
            Arrays.asList(new String[]{"repositoryproxyclassesfolder", "databaseproxyclassesfolder"}));
    private static final String updateRepositoryMode = "updaterepository";
    private static final Set<String> updateRepositoryModeParameters = new HashSet<>(
            Arrays.asList(new String[]{"repositoryproxyclassesfolder", "databaseproxyclassesfolder", "repositoryfolder", "repositoryurl", "repositorybranch"}));
    private static final String updateDatabaseMode = "updatedatabase";
    private static final Set<String> updateDatabaseModeParameters = new HashSet<>(
            Arrays.asList(new String[]{"repositoryproxyclassesfolder", "databasefolder", "databaseproxyclassesfolder", "packagename", "databaseurl"}));
    private static final String updateAllMode = "updateall";
    private static final Set<String> updateAllModeParameters = new HashSet<>(
            Arrays.asList(new String[]{"repositoryproxyclassesfolder", "databasefolder", "databaseproxyclassesfolder", "packagename", "databaseurl", "repositoryfolder", "repositoryurl", "repositorybranch"}));

    public static void main(String[] args) {
        CheckRepositoryProxyClasses repositoryProxyClassesCheck = new CheckRepositoryProxyClasses(args);
    }

    public CheckRepositoryProxyClasses(String[] args) {
        modeParameters = new HashMap<>();
        modeParameters.put(updateNoneMode, updateNoneModeParameters);
        modeParameters.put(updateRepositoryMode, updateRepositoryModeParameters);
        modeParameters.put(updateDatabaseMode, updateDatabaseModeParameters);
        modeParameters.put(updateAllMode, updateAllModeParameters);
        int errorNumber = -1;

        System.out.print("\n\n");
        System.out.println("========= Configuration ===================================");
        Map<String, String> parsedArguments = new HashMap<>();
        boolean containsIllegalArguments = false;
        try {
            parsedArguments = parseArguments(args);
        } catch (IllegalArgumentException ex) {
            containsIllegalArguments = true;
        }
        if (!containsIllegalArguments) {

            for (String key : parsedArguments.keySet()) {
                System.out.println("Parameter:" + key + "\n\tValue:" + parsedArguments.get(key));
            }
            System.out.println("===========================================================\n\n");

            try {
                errorNumber = runTest(parsedArguments);
            } catch (Exception ex) {
                //ex.printStackTrace();
                System.out.println("ERROR: Test Failed!!!");
                errorNumber = -1;
            }

            if (parsedArguments.containsKey("jenkinsurl") && parsedArguments.get("jenkinsurl").length() > 0) {
                String jenkinsUrl = parsedArguments.get("jenkinsurl");
                if (errorNumber == 0) {
                    notifyJenkins(jenkinsUrl, "true");
                } else {
                    notifyJenkins(jenkinsUrl, "false");
                }
            }
        }
        System.exit(errorNumber);
    }

    private void notifyJenkins(String jenkinsUrl, String success) {
        try {
            ConnectionTestHelper.testURLConnection(jenkinsUrl + success, 3000);
        } catch (Exception ex) {
            System.out.println("Failed to notify Jenkins");
        }
    }

    private int runTest(Map<String, String> parsedArguments) {
        int returnedErrorCode = 0;

        String mode = parsedArguments.get("mode").toLowerCase();
        String repositoryProxyClassesFolder = parsedArguments.get("repositoryproxyclassesfolder");
        String databaseProxyClassesFolder = parsedArguments.get("databaseproxyclassesfolder");

        if (updateRepositoryMode.equals(mode) || updateAllMode.equals(mode)) {

            String repositoryFolder = parsedArguments.get("repositoryfolder");
            String repositoryUrl = parsedArguments.get("repositoryurl");
            String repositoryBranch = parsedArguments.get("repositorybranch");

            Repository repository = new Repository();
            repository.setUrl(repositoryUrl);
            repository.setWorkDirectory(new File(repositoryFolder));
            repository.setStrategy(new MercurialRepositoryStrategy());

            System.out.print("\n\n");
            System.out.println("========= START: Testing Repository Connection ============");
            if (!repository.passesSmokeTest()) {
                System.out.println("Failed to connect to repository: " + repositoryUrl);
                return -1000;
            }
            System.out.println("========= FINISHED: Testing Repository Connection =========");

            //Getting latest version
            System.out.print("\n\n");
            System.out.println("========= START: Updating Repository ======================");

            repository.getLatestVersion();
            System.out.println("========= FINISHED: Updating Repository ===================");

            //Ensuring right branch

            System.out.print("\n\n");
            System.out.println("========= START: Switching Repository Branch ==============");
            repository.setBranch(repositoryBranch);
            if (!repository.getCurrentBranch().equals(repositoryBranch)) {
                System.out.println("ERROR: Failed to switch to branch: " + "develop");
                return -1000;
            }
            System.out.println("========= FINISHED: Switching Repository Branch ===========");
        }

        if (updateDatabaseMode.equals(mode) || updateAllMode.equals(mode)) {
            String databaseUrl = parsedArguments.get("databaseurl");
            String packageName = parsedArguments.get("packagename");
            String databasefolder = parsedArguments.get("databasefolder");

            Database dataBase = new Database();
            dataBase.setUrl(databaseUrl);
            dataBase.setWorkDirectory(new File(databasefolder));
            dataBase.setStrategy(new OracleDatabaseStrategy());

            System.out.print("\n\n");
            System.out.println("========= START: Testing Database Connection ==============");

            if (!dataBase.passesSmokeTest()) {
                System.out.println("Failed to connect to database: " + databaseUrl);
                return -1000;
            }
            System.out.println("========= FINISHED: Testing Database Connection ===========");

            System.out.print("\n\n");
            System.out.println("========= START: Deleting existing Database Proxy Classes =");
            dataBase.purgeWorkDirectory();
            System.out.println("========= FINISHED: Deleting existing Database Proxy Classes");

            System.out.print("\n\n");
            System.out.println("========= START: Generating Proxy Classes =================");
            try {
                dataBase.generateProxyClasses(packageName);
            } catch (java.lang.NullPointerException | java.lang.SecurityException ex) {
                //Known bug, if more than one jdbc driver is used in client application
            }
            System.out.println("========= FINISHED: Generating Proxy Classes ==============");
        }

        System.out.print("\n\n");
        System.out.println("========= START: Ensure Proxy Classes exist in repository  ====");
        //Check if repository proxy classes really exist
        if (isFolderEmpty(repositoryProxyClassesFolder)) {
            System.out.println("ERROR: No proxy classes found in folder: " + repositoryProxyClassesFolder);
            return -1000;
        }
        System.out.println("========= FINISHED: Ensure Proxy Classes exist in repository ");

        //Check if database proxy classes really exist
        System.out.print("\n\n");
        System.out.println("========= START: Ensure Proxy Classes exist in database  ====");
        if (isFolderEmpty(databaseProxyClassesFolder)) {
            System.out.println("ERROR: No proxy classes found in folder: " + databaseProxyClassesFolder);
            return -1000;
        }
        System.out.println("========= FINISHED: Ensure Proxy Classes exist in database  =");

        System.out.print("\n\n");
        System.out.println("========= START: Comparing repository proxy classes with database =");
        List<FileComparison> comparisonResults = FileHelper.getDifferences(repositoryProxyClassesFolder, databaseProxyClassesFolder);
        if (comparisonResults.isEmpty()) {
            System.out.println("ERROR: Failed to compare repository with databases");
            return -1000;
        }

        Set<FileComparison> differences = new HashSet<>();
        Set<FileComparison> orphaned = new HashSet<>();
        Set<FileComparison> equal = new HashSet<>();

        for (FileComparison currentFileComparison : comparisonResults) {
            switch (currentFileComparison.getComparisonResult()) {
                case DIFFERENT:
                    differences.add(currentFileComparison);
                    break;
                case EQUAL:
                    equal.add(currentFileComparison);
                    break;
                case MISSING_OTHER_FILE:
                    orphaned.add(currentFileComparison);
                    break;
            }
        }

        if (differences.isEmpty()) {
            System.out.println("Compared       " + comparisonResults.size() + " files.");
            System.out.println("Equal Files:   " + equal.size());
            System.out.println("Orphaned Files:   " + orphaned.size());
            System.out.println("Orphaned files in repository:");
            for (FileComparison currentFileComparison : orphaned) {
                System.out.println(" >" + currentFileComparison.getReferenceFile());
            }
        } else {
            System.out.println("Compared       " + comparisonResults.size() + " files.");
            System.out.println("Different Files:   " + differences.size());
            System.out.println("Orphaned Repository Files:   " + orphaned.size());
            System.out.println("Differences:");
            for (FileComparison currentFileComparison : differences) {
                System.out.println("Difference in file:   " + currentFileComparison.getReferenceFile().getPath());
            }
            return -1000;
        }
        return returnedErrorCode;
    }

    private boolean isFolderEmpty(String folderName) {
        return isFolderEmpty(new File(folderName));
    }

    private boolean isFolderEmpty(File folder) {
        //folder.isDirectory()
        return (folder.listFiles().length == 0);
    }

    private void updateDatabase(String databaseFolder, String databaseURL, String packageName) {
        Database database = new Database();
        database.setStrategy(new OracleDatabaseStrategy());
        database.setWorkDirectory(new File(databaseFolder));
        database.generateProxyClasses(packageName);
    }

    private Map<String, String> parseArguments(String[] args) {
        Map<String, String> parsedArguments = new HashMap<>();

        //Checking argument format
        if (containsIllegalFormatedArguments(args)) {
            showUsage();
            throw new IllegalArgumentException("Illegal Arguments");
        }

        for (String argument : args) {
            parsedArguments.put(getArgumentKey(argument).toLowerCase(), getArgumentValue(argument));
        }

        //Checking for mode parameter existence
        if (!parsedArguments.containsKey("mode")) {
            System.out.println("No Mode has been specified!");
            showUsage();
            throw new IllegalArgumentException("Illegal Arguments");
        }

        //Checking for unknown parameters
        Set<String> validParameters = getValidParameters();
        Set<String> invalidParamters = new HashSet<>();
        for (String currentParameter : parsedArguments.keySet()) {
            if (!validParameters.contains(currentParameter)){ 
                if(! "jenkinsurl".equals(currentParameter)){
                    invalidParamters.add(currentParameter);
                }
            }
        }
        if (invalidParamters.size() > 0) {
            System.out.print("Unknown parameters: ");
            for (String currentInvalidParameter : invalidParamters) {
                System.out.print(currentInvalidParameter + " ");
            }
            System.out.print("\n");
            throw new IllegalArgumentException("Illegal Parameters");
        }

        //Checking valid for valid mode
        String mode = parsedArguments.get("mode");
        if (!modeParameters.keySet().contains(mode)) {
            System.out.print("Invalid Mode \"" + mode + "\" Specified! Valid modes are: ");
            for (String validMode : modeParameters.keySet()) {
                System.out.print(validMode + " ");
            }
            System.out.print("\n");
            throw new IllegalArgumentException("Illegal Arguments");
        }

        //Checking for missing parameters
        Set<String> missingParameters = new HashSet<>();
        for (String requiredParameter : modeParameters.get(mode)) {
            if (!parsedArguments.containsKey(requiredParameter)) {
                missingParameters.add(requiredParameter);
            }
        }
        if (missingParameters.size() > 0) {
            System.out.print("Missing parameters for mode:" + mode + "! Please add parameters: ");
            for (String missingParameter : missingParameters) {
                System.out.print(missingParameter + " ");
            }
            throw new IllegalArgumentException("Illegal Arguments");
        }

        return parsedArguments;
    }

    /*
     private Set<String> getMissingArgumentKeys(HashMap<String, String> arguments, Set<String> expectedArguments) {
     Set<String> missingArgumentKeys;
     missingArgumentKeys = arguments.keySet();
     missingArgumentKeys.retainAll(expectedArguments);
     return missingArgumentKeys;
     }
     */
    private void showUsage() {
        String[] usage = new String[]{
            "\nUsage examples:",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateNone",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyRepo\\src\\main\\java\\com\\company\\proxyclasses\"",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\company\\proxyclasses\" ",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateRepository",
            "   -repositoryURL:http://UserName:Password@www.myrepourl.com:8976",
            "   -repositoryFolder:\"C:\\Documents and Settings\\MyUser\\MyRepo\" ",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyRepo\\src\\main\\java\\com\\company\\proxyclasses\" ",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\company\\proxyclasses\" ",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateDatabase",
            "   -databaseURL:thin:oracle:Username/Password@DATABASE.COMPANY.COM:DATABASESCHEMA",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyRepo\\src\\main\\java\\com\\company\\proxyclasses\" ",
            "   -packageName:com.company.proxyclasses",
            "   -databaseFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\"",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\company\\proxyclasses\" ",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateAll",
            "   -databaseURL:thin:oracle:Username/Password@8976",
            "   -repositoryURL:http://UserName:Password@www.myrepourl.com:8976",
            "   -repositoryFolder:\"C:\\Documents and Settings\\MyUser\\MyRepo\" ",
            "   -repositoryPath:\"\\src\\main\\java\\com\\company\\proxyclasses\" ",
            "   -packageName:com.company.proxyclasses",
            "   -generatedProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\company\\proxyclasses\" "
        };
        for (String currentLine : usage) {
            System.out.println(currentLine);
        }
    }

    private Set<String> getValidParameters() {
        Set<String> validParameters = new HashSet<>();
        validParameters.add("mode");
        for (String currentMode : modeParameters.keySet()) {
            validParameters.addAll(modeParameters.get(currentMode));
        }
        return validParameters;
    }

    private boolean containsIllegalFormatedArguments(String[] args) {
        Set<String> illegalFormatedArguments = new HashSet<>();
        for (String currentArgument : args) {
            if (!currentArgument.startsWith("-")) {
                illegalFormatedArguments.add(currentArgument);
            } else if (!currentArgument.contains(":")) {
                illegalFormatedArguments.add(currentArgument);
            }
            if (getArgumentKey(currentArgument).equals("") || getArgumentValue(currentArgument).equals("")) {
                illegalFormatedArguments.add(currentArgument);
            }
        }
        for (String illegalArgument : illegalFormatedArguments) {
            System.out.println("Illegal Argument Found! " + illegalArgument);
        }
        if (illegalFormatedArguments.size() > 0) {
            System.out.println("Example for a proper argument: -folder:\"C:\\Windows\\temp\"");
        }
        return (illegalFormatedArguments.size() > 0);
    }

    private String getArgumentKey(String argument) {
        String argumentKey = "";
        if (argument.contains(":")) {
            int firstColon = argument.indexOf(':');
            argumentKey = argument.substring(1, firstColon);
        }
        return argumentKey;
    }

    private String getArgumentValue(String argument) {
        String argumentValue = "";
        if (argument.contains(":") && argument.startsWith("-")) {
            int firstColon = argument.indexOf(':');
            if (firstColon != argument.length()) {
                argumentValue = argument.substring(firstColon + 1, argument.length()).trim();
                if (argumentValue.startsWith("\"") && argumentValue.endsWith("\"")) {
                    argumentValue = argumentValue.substring(2, argumentValue.length() - 1);
                }
            }
        }
        return argumentValue;
    }
}
