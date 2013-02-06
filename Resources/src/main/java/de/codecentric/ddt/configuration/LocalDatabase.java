package de.codecentric.ddt.configuration;

import java.io.File;
import java.io.PrintWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.derby.impl.drda.NetworkServerControlImpl;

import de.codecentric.ddt.configuration.Configuration;


@Singleton
@Startup
public class LocalDatabase {

	private static NetworkServerControlImpl networkServerControlImpl = null;

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
