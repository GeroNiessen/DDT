package de.codecentric.ddt.deprecated;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

//import org.apache.derby.impl.drda.NetworkServerControlImpl;

/*
public class DerbyDatabaseControl implements ServletContextListener{

	public static long applicationInitialized =	0L;


	// Application Startup Event

	public void	contextInitialized(ServletContextEvent ce) {
		System.out.println("############### Servlet Context Listener: contextInitialized ################");
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		NetworkServerControlImpl networkServerControlImpl = null;
		System.out.println("STAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAART DERBY");
		String userHomeDirectoryPath = System.getProperty("user.home"); //"."
		String fileSeparator = System.getProperty("file.separator");  
		String derbySystemPath = userHomeDirectoryPath + ".DDT-DATABASE";
		File derbySystemDirectory = new File(derbySystemPath);
		if(!derbySystemDirectory.exists()){
			derbySystemDirectory.mkdirs();
		}
		// Set the db system directory.
		System.setProperty("derby.system.home", derbySystemPath);


		try {
			networkServerControlImpl = new NetworkServerControlImpl();
			if(!networkServerControlImpl.isServerStarted()){
				networkServerControlImpl.start(new PrintWriter(System.out));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

	// Application Shutdown	Event
	public void	contextDestroyed(ServletContextEvent ce) {
		/*
		System.out.println("STOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOP DERBY");
		NetworkServerControlImpl networkServerControlImpl = null;
		try {
			networkServerControlImpl = new NetworkServerControlImpl();
			if(networkServerControlImpl.isServerStarted()){
				networkServerControlImpl.shutdown();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//DriverManager.getConnection("jdbc:derby:;shutdown=true");

	}

}
*/

