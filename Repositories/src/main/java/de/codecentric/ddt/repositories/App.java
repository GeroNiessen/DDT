package de.codecentric.ddt.repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.codecentric.ddt.Resource;
public class App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Repository icisPlusRepository = new Repository();
		icisPlusRepository.setName("ICIS_Mercurial_Repository");
		icisPlusRepository.setUrl("http://wgvli39.swlabor.local:8282");
		icisPlusRepository.setStrategy(new MercurialRepositoryStrategy());
		
		
		try {
			
			Set<Class<?>> classes = Repository.getAllRessourceStrategies();
			classes.addAll(Resource.getAllRessources());
			JAXBContext context = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
			
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		    // Write to System.out
		    m.marshal(icisPlusRepository, System.out);
		    
		    m.marshal(icisPlusRepository, new File("DDT-Configuration.xml"));
		    
		    icisPlusRepository.setName("PlusPlus");
		    
		    m.marshal(icisPlusRepository, new File("DDT-Configuration.xml"));
		    //No Load ==============================================
		    
		    Unmarshaller um = context.createUnmarshaller();
		    Repository restoredRepository = (Repository) um.unmarshal(new FileReader("DDT-Configuration.xml"));
		    System.out.println("Repository Name: " + restoredRepository.getName());
		    Map<String, Integer> branchRevisions = restoredRepository.getLatestBranchRevisions();
		    for (String currentBranchName : branchRevisions.keySet()) {
		      System.out.println("Branch: " + currentBranchName + " Revision: "
		          + branchRevisions.get(currentBranchName));
		    }
		    
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	    // Write to File
	    /*
	    m.marshal(bookstore, new File(BOOKSTORE_XML));

	    // get variables from our xml file, created before
	    System.out.println();
	    System.out.println("Output from our XML File: ");
	    */
	}

}
