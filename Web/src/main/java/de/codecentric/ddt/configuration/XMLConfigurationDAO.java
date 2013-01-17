package de.codecentric.ddt.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//@Stateless
//@LocalBean
@Alternative
public class XMLConfigurationDAO implements ConfigurationDAO{

	private JAXBContext context;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private String configurationFile;

	public XMLConfigurationDAO(){
		String fileSeparator = System.getProperty("file.separator");
		this.configurationFile = Configuration.getBaseWorkDirectory().getPath() + fileSeparator + "DDT-Configuration.xml";

		Set<Class<?>> classes = Resource.getAllRessourceStrategies();
		classes.addAll(Resource.getAllRessources());
		classes.add(Configuration.class);
		classes.add(Application.class);
		try {
			context = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize JAXBContext in XMLConfigurationDAO");
		}
	}

	@Override
	public void save(Configuration configuration) {

		/*
		File oldConfigurationFile = new File(configurationFile);
		if(oldConfigurationFile.exists()){
			oldConfigurationFile.delete();
		}
		*/

		try{
			marshaller.marshal(configuration, new File(configurationFile));
		} catch (JAXBException e) {
			e.printStackTrace();
			//throw new RuntimeException("Failed to marshall");
		}
	}

	@Override
	public Configuration load() {
		Configuration restoredConfiguration = new Configuration();
		try {
			restoredConfiguration = (Configuration) unmarshaller.unmarshal(new FileReader(configurationFile));
		} catch (FileNotFoundException|JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to unmarshall");
		}
		return restoredConfiguration;
	}
}
