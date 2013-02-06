package de.codecentric.ddt.configuration;

import java.io.File;
import java.io.PrintWriter;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.derby.impl.drda.NetworkServerControlImpl;


@Singleton
@Startup
/**
 * Controls the starting and stopping of a local Derby database
 * @author: Gero Niessen
 */
public class LocalDatabase {

	private static NetworkServerControlImpl networkServerControlImpl = null;

        /**
         * Starts a local derby database in case it is not started, yet.
         * @throws Exception 
         */
	@PostConstruct
	public static void init() throws Exception {
		System.out.println("########### LocalDatabseSingleton init ############");
		String fileSeparator = System.getProperty("file.separator");
	    String derbySystemPath = Configuration.getBaseWorkDirectory().getPath() + fileSeparator + ".DDT-DATABASE";
	    File derbySystemDirectory = new File(derbySystemPath);
	    if(!derbySystemDirectory.exists()){
	    	derbySystemDirectory.mkdirs();
	    }
	    // Set the db system directory.
	    System.setProperty("derby.system.home", derbySystemPath);
		
		networkServerControlImpl = new NetworkServerControlImpl();
		if(!networkServerControlImpl.isServerStarted()){
			networkServerControlImpl.start(new PrintWriter(System.out));
		}
	}
	
        /**
         * Shuts down local Derby database
         * (Nothing is done, as recommended by Apache Derby)
         * (The shutdown of the application will implicitly shut down the database started the application)
         * @throws Exception 
         */
	@PreDestroy
	public static void initShutdown() throws Exception {
		/* 
		System.out.println("########### LocalDatabseSingleton initShutdown ############");
		networkServerControlImpl = new NetworkServerControlImpl();
		if(networkServerControlImpl.isServerStarted()){
			networkServerControlImpl.shutdown();
		}
		*/
	}	
}
