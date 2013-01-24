/*
package de.codecentric.ddt.deprecated;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.derby.impl.drda.NetworkServerControlImpl;

@WebServlet(loadOnStartup=1)
public class DerbyDatabaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		
		System.out.println("############### DerbyDatabaseServlet: INIT ################");
		String userHomeDirectoryPath = System.getProperty("user.home"); //"."
		String fileSeparator = System.getProperty("file.separator");  
	    String derbySystemPath = userHomeDirectoryPath + ".DDT-DATABASE";
	    File derbySystemDirectory = new File(derbySystemPath);
	    if(!derbySystemDirectory.exists()){
	    	derbySystemDirectory.mkdirs();
	    }
	    // Set the db system directory.
	    System.setProperty("derby.system.home", derbySystemPath);
		
	    NetworkServerControlImpl networkServerControlImpl = null;
	    
		try {
			networkServerControlImpl = new NetworkServerControlImpl();
			if(!networkServerControlImpl.isServerStarted()){
				networkServerControlImpl.start(new PrintWriter(System.out));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	public void destroy() {
		
	} 
}
*/
