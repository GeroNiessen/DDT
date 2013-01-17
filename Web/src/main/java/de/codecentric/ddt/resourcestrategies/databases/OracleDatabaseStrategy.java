package de.codecentric.ddt.resourcestrategies.databases;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class OracleDatabaseStrategy extends DatabaseStrategy{

	private static final long serialVersionUID = 2889106510128560500L;

	
	public OracleDatabaseStrategy(){
		setName("OracleDatabaseStrategy");
	}

	@Override
	public void generateProxyClasses(Database databaseContext, String packageName) {
		databaseContext.purgeWorkDirectory();
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
		for(File currentFile : jwspFiles){
			currentFile.delete();
		}
	}
	
	private String getConnectionString(Database databaseContext){
		String userName = databaseContext.getLoginCredential().getUsername();
		String password = databaseContext.getLoginCredential().getPassword();
		String connectionString = databaseContext.getUrl().replace("@", userName + "/" + password + "@");
		return connectionString;
	}
	
	private Connection getConnection(Database databaseContext){
		Connection returnedConnection = null; //"jdbc:oracle:thin:ICIS/pvcs@wgvli36.swlabor.local:1522:ICISPLUS"
				
		try {
			Class.forName("oracle.jdbc.OracleDriver") ;
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnedConnection;
	}
	
	private String getPackagesWithTranslation(Database databaseContext){
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
	
	private String[] getPackages(Database databaseContext){
		List<String> packages = new ArrayList<String>();
		try {
			Connection conn = getConnection(databaseContext);
			Statement stmt = conn.createStatement(); 
			//String storedProcedureQuery = "select OBJECT_NAME from user_objects where object_type='PACKAGE' and OBJECT_NAME like 'WSP!_%' ESCAPE '!'";
			String storedProcedureQuery = "select OBJECT_NAME, OWNER from SYS.ALL_OBJECTS where object_type='PACKAGE' and owner='ICIS' and OBJECT_NAME like 'WSP!_%' ESCAPE '!' ORDER BY OBJECT_NAME";

			ResultSet rs = stmt.executeQuery( storedProcedureQuery ) ;
			if (rs != null && rs.next()) {
				do {
					packages.add(rs.getString(1));
				} while (rs.next());
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch(SQLException se){
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
			//System.out.println( e ) ;
		}
		return packages.toArray(new String[]{});
	}
}