package de.codecentric.ddt.deprecated;

import java.io.File;
import java.io.PrintWriter;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.apache.derby.impl.drda.NetworkServerControlImpl;

public class DatabaseResourceAdapter implements ResourceAdapter{

	@Override
	public void endpointActivation(MessageEndpointFactory arg0,
			ActivationSpec arg1) throws ResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endpointDeactivation(MessageEndpointFactory arg0,
			ActivationSpec arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public XAResource[] getXAResources(ActivationSpec[] arg0)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start(BootstrapContext arg0)
			throws ResourceAdapterInternalException {
		
		System.out.println("############ DatabaseResourceAdapter start ####################");
		String userHomeDirectoryPath = System.getProperty("user.home"); //"."
		String fileSeparator = System.getProperty("file.separator");  
		String derbySystemPath = userHomeDirectoryPath + ".DDT-DATABASE";
		File derbySystemDirectory = new File(derbySystemPath);
		if(!derbySystemDirectory.exists()){
			derbySystemDirectory.mkdirs();
		}
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

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}