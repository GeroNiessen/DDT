package de.codecentric.ddt.resourcestrategies.databases.oracle;

import de.codecentric.ddt.configuration.Resource;
import de.codecentric.ddt.resourcestrategies.databases.DatabaseStrategy;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * OracleDatabaseStrategy implements the DatabaseStrategy for Oracle-Databases
 * @author Gero Niessen
 */
@XmlRootElement
@Entity
public class OracleDatabaseStrategy extends DatabaseStrategy{

	private static final long serialVersionUID = 2889106510128560500L;
	private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(OracleDatabaseStrategy.class .getName());

	public OracleDatabaseStrategy(){
		setName("OracleDatabaseStrategy");
	}

	@Override
	public boolean passesSmokeTest(Resource context) {
		return (getConnection(context) != null);
	}

	@Override
	public void generateProxyClasses(Resource databaseContext, String packageName) {

		String fileSeparator = System.getProperty("file.separator");

		String[] jpubArguments = new String[]{
				"-url=" + getConnectionString(databaseContext),
				"-numbertypes=oracle",
				"-usertypes=oracle",
				"-package=" + packageName,
				"-sql=" + getPackagesWithTranslation(databaseContext),
				"-compile=false",
				"-dir=" + databaseContext.getWorkDirectory().getPath()
		};
		LOGGER.info("Running JPUB Command: \n" + Arrays.asList(jpubArguments));
		oracle.jpub.Doit.main(jpubArguments);

		//Removing jwsp files

		String jwspPath = databaseContext.getWorkDirectory().getPath() + fileSeparator + packageName.replace(".", fileSeparator);
		File jwspDir = new File(jwspPath);

		File[] jwspFiles = jwspDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().startsWith("jwsp");
			}
		});

		if(jwspFiles != null){
			for(File currentFile : jwspFiles){
				currentFile.delete();
			}
		}
	}

	/**
	 * Gets the connection string (URL) from the context and injects username and password to it.
	 * @param databaseContext
	 * @return 
	 */
	private String getConnectionString(Resource databaseContext){
		String userName = databaseContext.getUsername();
		String password = databaseContext.getPassword();
		String connectionString;
		if(!("".equals(userName) || "".equals(password))){
			connectionString = databaseContext.getUrl().replace("@", userName + "/" + password + "@");
		} else {
			connectionString = databaseContext.getUrl();
		}
		return connectionString;
	}

	/**
	 * Gets the JDBC Connection objects to the database.
	 * @param databaseContext
	 * @return 
	 */
	private Connection getConnection(Resource databaseContext){
		Connection returnedConnection = null;

		try {
			Class.forName("oracle.jdbc.OracleDriver") ;
		} catch (ClassNotFoundException e1) {
			LOGGER.warning("Failed to load Oracle Driver: oracle.jdbc.OracleDriver");
			e1.printStackTrace();
		}
		try {
			returnedConnection = DriverManager.getConnection(getConnectionString(databaseContext));
			/*
			for( SQLWarning warn = returnedConnection.getWarnings(); warn != null; warn = warn.getNextWarning() ){
				System.out.println( "SQL Warning:" ) ;
				System.out.println( "State  : " + warn.getSQLState()  ) ;
				System.out.println( "Message: " + warn.getMessage()   ) ;
				System.out.println( "Error  : " + warn.getErrorCode() ) ;
			}
			 */
		} catch (SQLException e) {
			LOGGER.warning("Failed to get connection to database: " + databaseContext.getName());
			e.printStackTrace();
		}
		return returnedConnection;
	}

	/**
	 * Transforms all Oracle packages and stored procedures in the database
	 * to an Oracle JPublisher suited syntax
	 * e.g. "STORED_PROCEDURE1:JSTORED_PROCEDURE1,STORED_PROCEDURE2:STORED_PROCEDURE2"
	 * @param databaseContext
	 * @return 
	 */
	private String getPackagesWithTranslation(Resource databaseContext){
		String returnedString = "";
		String[] packages = getPackages(databaseContext);
		int lastPackage = packages.length-1;
		for(int i=0; i < packages.length; i++){
			if(i != lastPackage){
				returnedString = returnedString
						.concat(packages[i])
						.concat(":J")
						.concat(packages[i])
						.concat(",");
			} else {
				returnedString = returnedString
						.concat(packages[i])
						.concat(":J")
						.concat(packages[i]);			
			}
		}
		return returnedString;
	}

	/**
	 * Reads the names of all stored procedures and oracle packages from the database schema
	 * @param databaseContext
	 * @return 
	 */
	private String[] getPackages(Resource databaseContext){
		List<String> packages = new ArrayList<>();
		try (Connection conn = getConnection(databaseContext); Statement stmt = conn.createStatement()) { 
			String storedProcedureQuery = "select OBJECT_NAME from user_objects where object_type='PACKAGE' and OBJECT_NAME like 'WSP!_%' ESCAPE '!'";
			//String storedProcedureQuery = "select OBJECT_NAME, OWNER from SYS.ALL_OBJECTS where object_type='PACKAGE' and owner='ICIS' and OBJECT_NAME like 'WSP!_%' ESCAPE '!' ORDER BY OBJECT_NAME";
			try (ResultSet rs = stmt.executeQuery( storedProcedureQuery )) {
				if (rs != null && rs.next()) {
					do {
						packages.add(rs.getString(1));
					} while (rs.next());
				}
			}
		} catch(SQLException se){
			LOGGER.warning("Failed to read stored procedures from database schema: " + databaseContext.getName());
			se.printStackTrace();
			/*
			while( se != null )
			{
				System.out.println( "State  : " + se.getSQLState()  ) ;
				System.out.println( "Message: " + se.getMessage()   ) ;
				System.out.println( "Error  : " + se.getErrorCode() ) ;
				se = se.getNextException() ;
			}
			 */
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return packages.toArray(new String[]{});
	}
}
