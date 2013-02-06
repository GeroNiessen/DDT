package de.codecentric.ddt.commandlineclient;

import de.codecentric.ddt.configuration.FileHelper;
import de.codecentric.ddt.configuration.FolderComparison;
import de.codecentric.ddt.mercurialrepositorystrategy.MercurialRepositoryStrategy;
import de.codecentric.ddt.resourcestrategies.databases.Database;
import de.codecentric.ddt.resourcestrategies.databases.oracle.OracleDatabaseStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.Repository;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 */
public class CheckRepositoryProxyClasses {

    //private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(CheckRepositoryProxyClasses.class.getName());
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
    private static final Set<String> optionalParameters = new HashSet<>(Arrays.asList(new String[]{"jenkinsurl"}));
    private String errorMessage;
    Map<String, String> parsedArguments;
    private String mode;

    public static void main(String[] args) {
        CheckRepositoryProxyClasses repositoryProxyClassesCheck = new CheckRepositoryProxyClasses(args);
    }

    public CheckRepositoryProxyClasses(String[] args) {
        this.errorMessage = "";
        this.parsedArguments = new HashMap<>();

        modeParameters = new HashMap<>();
        modeParameters.put(updateNoneMode, updateNoneModeParameters);
        modeParameters.put(updateRepositoryMode, updateRepositoryModeParameters);
        modeParameters.put(updateDatabaseMode, updateDatabaseModeParameters);
        modeParameters.put(updateAllMode, updateAllModeParameters);

        int errorNumber = -1;

        try {
            parseArguments(args);
            mode = parsedArguments.get("mode").toLowerCase();
            switch (this.mode) {
                case updateNoneMode:
                    break;
                case updateRepositoryMode:
                    updateRepository();
                    break;
                case updateDatabaseMode:
                    updateDatabase();
                    break;
                case updateAllMode:
                    updateRepository();
                    updateDatabase();
                    break;
            }
            FolderComparison folderComparison = compareFolders();
            if(folderComparison.isEveryFileEqual() && !folderComparison.getAllFileComparisons().isEmpty()){
                errorNumber = 0;
            }
            this.errorMessage = folderComparison.toString();
            System.out.println("\n\n =============== TEST RESULTS =================\n" + this.errorMessage);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            this.errorMessage = ex.getMessage();
            showUsage();
        } catch (RepositoryUpdateException|DatabaseUpdateException|FolderComparisonException ex) {
            this.errorMessage = ex.getMessage();
            System.err.println(ex.getMessage());
        } finally {            
            if(parsedArguments.containsKey("jenkinsurl")){
                String jenkinsurl = parsedArguments.get("jenkinsurl");
                if(errorNumber == 0){
                    notifyJenkins(jenkinsurl, "true", errorMessage);
                } else {
                    notifyJenkins(jenkinsurl, "false", errorMessage);
                }
            }
        }

        System.exit(errorNumber);
    }

    
    private void updateRepository() throws RepositoryUpdateException {
        System.out.println("\n\n========= START: Updating Repository ==================");

        String repositoryFolder = parsedArguments.get("repositoryfolder");
        String repositoryUrl = parsedArguments.get("repositoryurl");
        String repositoryBranch = parsedArguments.get("repositorybranch");

        Repository repository = new Repository();
        repository.setUrl(repositoryUrl);
        repository.setWorkDirectory(new File(repositoryFolder));
        repository.setStrategy(new MercurialRepositoryStrategy());

        System.out.println("\n\n========= START: Testing Repository Connection ============");
        if (!repository.passesSmokeTest()) {
            throw new RepositoryUpdateException("Failed to connect to repository: " + repositoryUrl);
        }
        System.out.println("========= FINISHED: Testing Repository Connection =========");

        System.out.println("\n\n========= START: Updating Repository ======================");
        try {
            repository.getLatestVersion();
        } catch (Exception ex) {
            throw new RepositoryUpdateException("ERROR: Failed to update repository in " + repositoryFolder + "from URL " + repositoryUrl);
        }
        System.out.println("========= FINISHED: Updating Repository ===================");

        System.out.println("\n\n========= START: Switching Repository Branch ==============");
        repository.setBranch(repositoryBranch);
        if (!repository.getCurrentBranch().equals(repositoryBranch)) {
            throw new RepositoryUpdateException("ERROR: Failed to switch to branch: " + repositoryBranch);
        }
        System.out.println("========= FINISHED: Switching Repository Branch ===========");
    }

    private void updateDatabase() throws DatabaseUpdateException {

        String databaseUrl = parsedArguments.get("databaseurl");
        String packageName = parsedArguments.get("packagename");
        String databasefolder = parsedArguments.get("databasefolder");

        Database dataBase = new Database();
        dataBase.setUrl(databaseUrl);
        dataBase.setWorkDirectory(new File(databasefolder));
        dataBase.setStrategy(new OracleDatabaseStrategy());

        System.out.println("\n\n========= START: Testing Database Connection ==============");
        if (!dataBase.passesSmokeTest()) {
            throw new DatabaseUpdateException("Failed to connect to database: " + databaseUrl);
        }
        System.out.println("========= FINISHED: Testing Database Connection ===========");

        System.out.println("\n\n========= START: Deleting existing Database Proxy Classes =");
        try {
            dataBase.purgeWorkDirectory();
        } catch (Exception ex) {
            throw new DatabaseUpdateException("ERROR: Failed to clean exiting proxy classes!");
        }
        System.out.println("========= FINISHED: Deleting existing Database Proxy Classes");

        System.out.println("\n\n========= START: Generating Proxy Classes =================");
        try {
            dataBase.generateProxyClasses(packageName);
        } catch (Exception ex) {
            //Known bug, if more than one jdbc driver is used in client application
            throw new DatabaseUpdateException("ERROR: Failed to generate proxy classes!" + ex.getMessage());
        }
        System.out.println("========= FINISHED: Generating Proxy Classes ==============");
    }

    private FolderComparison compareFolders() throws FolderComparisonException {

        String repositoryProxyClassesFolder = parsedArguments.get("repositoryproxyclassesfolder");
        String databaseProxyClassesFolder = parsedArguments.get("databaseproxyclassesfolder");

        System.out.println("\n\n========= START: Ensure Proxy Classes exist in repository  ====");
        if (isFolderEmpty(repositoryProxyClassesFolder)) {
            throw new FolderComparisonException(
                    "No proxy classes found in folder: "
                    + repositoryProxyClassesFolder);
        }
        System.out.println("========= FINISHED: Ensure Proxy Classes exist in repository ");

        System.out.println("\n\n========= START: Ensure Proxy Classes exist in database  ====");
        if (isFolderEmpty(databaseProxyClassesFolder)) {
            throw new FolderComparisonException(
                    "ERROR: No proxy classes found in folder: "
                    + databaseProxyClassesFolder);
        }
        System.out.println("========= FINISHED: Ensure Proxy Classes exist in database  =");

        System.out.println("\n\n========= START: Comparing repository proxy classes with database =");
        FolderComparison folderComparison = FileHelper.getFolderComparison(repositoryProxyClassesFolder, databaseProxyClassesFolder);
        if (folderComparison.getAllFileComparisons().isEmpty()) {
            throw new FolderComparisonException("ERROR: Failed to compare repository with databases!\n No Files found!");
        }

        return folderComparison;
    }

    private boolean isFolderEmpty(String folderName) {
        return isFolderEmpty(new File(folderName));
    }

    private boolean isFolderEmpty(File folder) {
        //folder.isDirectory()
        return (folder.listFiles().length == 0);
    }

    private String join(Collection<String> strings, String separator) {
        String joinedStrings = "";
        if (!strings.isEmpty()) {
            for (String currentString : strings) {
                joinedStrings = joinedStrings + currentString + separator;
            }
            joinedStrings = joinedStrings.substring(0, joinedStrings.length() - separator.length());
        }
        return joinedStrings;
    }

    private void notifyJenkins(String jenkinsUrl, String success, String message) {
        String urlEncodedMessage = "&nachricht=";

        try {
            urlEncodedMessage = urlEncodedMessage + URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            urlEncodedMessage = urlEncodedMessage + "FailedToEncodeErrorMessage";
        }

        try {
            URL jenkinsUrlConnection = new URL(jenkinsUrl + success + urlEncodedMessage);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(jenkinsUrlConnection.openStream()));
            while ((in.readLine()) != null) {
            }
        } catch (Exception ex) {
            System.out.println("Failed to notify Jenkins");
        }
    }

    private void parseArguments(String[] args) throws IllegalArgumentException {

        //Checking argument format
        Set<String> illegalFormatedArguments = getIllegalFormatedArguments(args);
        if (!illegalFormatedArguments.isEmpty()) {
            showUsage();
            throw new IllegalArgumentException(
                    "Illegal Formated Arguments Found: "
                    + join(illegalFormatedArguments, ","
                    + "\nExample for a proper argument: -folder:\\\"C:\\\\Windows\\\\temp\\\"\""));
        }

        for (String argument : args) {
            parsedArguments.put(getArgumentKey(argument).toLowerCase(), getArgumentValue(argument));
        }

        //Checking for mode parameter existence
        if (!parsedArguments.containsKey("mode")) {
            throw new IllegalArgumentException("No Mode has been specified! Valid Modes are " + join(modeParameters.keySet(), ","));
        }

        //Checking for unknown parameters
        Set<String> validParameters = getValidParameters();
        Set<String> unknownParameters = new HashSet<>();
        for (String currentParameter : parsedArguments.keySet()) {
            if (!validParameters.contains(currentParameter)) {
                unknownParameters.add(currentParameter);
            }
        }
        if (!unknownParameters.isEmpty()) {
            throw new IllegalArgumentException("Unknown Parameters: "
                    + join(unknownParameters, ","));
        }

        //Checking mode parameter
        mode = parsedArguments.get("mode");
        if (!modeParameters.keySet().contains(mode)) {
            throw new IllegalArgumentException(
                    "Invalid Mode \"" + mode + "\" Specified!\n"
                    + "Valid Modes are " + join(modeParameters.keySet(), ","));
        }

        //Checking for missing parameters
        Set<String> missingParameters = new HashSet<>();
        for (String requiredParameter : modeParameters.get(mode)) {
            if (!parsedArguments.containsKey(requiredParameter)) {
                missingParameters.add(requiredParameter);
            }
        }
        if (!missingParameters.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing parameters for mode:" + mode + "!\n"
                    + "Please add the parameters: "
                    + join(missingParameters, ","));
        }
    }

    private void showUsage() {
        String[] usage = new String[]{
            "\nUsage examples:",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updatenone",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyProjectRepository\\src\\main\\java\\com\\mycompany\\myproject\\proxyclasses\"",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\mycompany\\myproject\\proxyclasses\" ",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateRepository",
            "   -repositoryURL:http://MyUsername:MyPassword@MyRepository.MyDomain.com:8976",
            "   -repositoryFolder:\"C:\\Documents and Settings\\MyUser\\MyProjectRepository\" ",
            "   -repositoryBranch:default",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyProjectRepository\\src\\main\\java\\com\\mycompany\\myproject\\proxyclasses\" ",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\mycompany\\myproject\\proxyclasses\" ",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateDatabase",
            "   -databaseURL:jdbc:oracle:thin:MyUsername/MyPassword@MyDatabase.MyDomain.local:1522:MyDatabaseSchema",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyProjectRepository\\src\\main\\java\\com\\mycompany\\myproject\\proxyclasses\" ",
            "   -packageName:com.mycompany.myproject.proxyclasses",
            "   -databaseFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\"",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\mycompany\\myproject\\proxyclasses\" ",
            "===============================================",
            "java -jar CommandLineClient-1.0.jar",
            "   -mode:updateAll",
            "   -databaseURL:jdbc:oracle:thin:MyUsername/MyPassword@MyDatabase.MyDomain.local:1522:MyDatabaseSchema",
            "   -repositoryURL:http://MyUsername:MyPassword@MyRepository.MyDomain.com:8976",
            "   -repositoryFolder:\"C:\\Documents and Settings\\MyUser\\MyProjectRepository\" ",
            "   -repositoryBranch:default",
            "   -repositoryProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\MyProjectRepository\\src\\main\\java\\com\\mycompany\\myproject\\proxyclasses\" ",
            "   -packageName:com.mycompany.myproject.proxyclasses",
            "   -databaseFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\"",
            "   -databaseProxyClassesFolder:\"C:\\Documents and Settings\\MyUser\\JPUB\\com\\mycompany\\myproject\\proxyclasses\"\n\n",
            "----------------------------\n",
            "Optional parameters for all modes:\n",
            "   -jenkinsurl=\"http://MyJenkins.MyDomain.local:8080/job/jpub.check/buildWithParameters?delay=0sec&erfolgreich=\"\n"
        };
        for (String currentLine : usage) {
            System.out.println(currentLine);
        }
    }

    private Set<String> getValidParameters() {
        Set<String> validParameters = new HashSet<>();
        validParameters.add("mode");
        validParameters.add("jenkinsurl");
        for (String currentMode : modeParameters.keySet()) {
            validParameters.addAll(modeParameters.get(currentMode));
        }
        return validParameters;
    }

    private Set<String> getIllegalFormatedArguments(String[] args) {
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

        return illegalFormatedArguments;
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

    private class RepositoryUpdateException extends RuntimeException {

        public RepositoryUpdateException(String message) {
            super("Updating the repository has caused an unexpected exception:\n".concat(message));
        }
    }

    private class DatabaseUpdateException extends RuntimeException {

        public DatabaseUpdateException(String message) {
            super("Updating the database has caused an unexpected exception:\n".concat(message));
        }
    }

    private class FolderComparisonException extends RuntimeException {

        public FolderComparisonException(String message) {
            super("Comparing the proxy classes has caused an unexpected exception:\n".concat(message));
        }
    }
}
