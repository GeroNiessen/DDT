package de.codecentric.ddt.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectionHelper {

	public static Set<Class<?>> getAllInstanciableImplementations(String packageName, Class<?> superClass){
		Set<Class<?>> allInstantiableClasses = new HashSet<Class<?>>();
		for(Class<?> currentClass: getAllImplementations(packageName, superClass)){
			if(!(currentClass.isInterface() || isClassAbstract(currentClass))){
				allInstantiableClasses.add(currentClass);
			}
		}
		return allInstantiableClasses;
	}

	public static Set<Class<?>> getAllImplementations(String packageName, Class<?> superClass){
		Set<Class<?>> allImplementations = new HashSet<Class<?>>();
		Set<Class<?>> allClassesInPackage;
		try {
			allClassesInPackage = getClasses(packageName);
			getAllImplementations(packageName, superClass, allImplementations, allClassesInPackage);
		} catch (ClassNotFoundException|IOException|URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return allImplementations;
	}

	private static boolean isClassAbstract(Class<?> classToCheck){
		return Modifier.isAbstract(classToCheck.getModifiers());
	}

	private static void getAllImplementations(String packageName, Class<?> superClass, Set<Class<?>> allImplementations, Set<Class<?>> allClassesInPackage){
		allImplementations.add(superClass);

		for(Class<?> currentClassInPackage: allClassesInPackage){

			List<Class<?>> interfacesOfCurrentClassInPackage = Arrays.asList(currentClassInPackage.getInterfaces());
			for(Class<?> currentInterfaceOfCurrentClassInPackage : interfacesOfCurrentClassInPackage){
				if(currentInterfaceOfCurrentClassInPackage.equals(superClass)){
					if(!allImplementations.contains(currentClassInPackage)){
						getAllImplementations(packageName, currentClassInPackage, allImplementations, allClassesInPackage);
					}
				}
			}

			if(!currentClassInPackage.isInterface()){
				if(currentClassInPackage.getSuperclass().equals(superClass)){
					if(!allImplementations.contains(currentClassInPackage)){
						getAllImplementations(packageName, currentClassInPackage, allImplementations, allClassesInPackage);
					}
				}
			}				
		}
	}

	/*
	public static Set<Class<?>> getAllClassesImplementingInterface(String packageName, Class<?> interfaceClass){
		Set<Class<?>> returnedRepositoryStrategies = new HashSet<Class<?>>();

		Set<Class<?>> allClassesInPackage;
		try {
			allClassesInPackage = getClasses(packageName);
			for(Class<?> currentClass: allClassesInPackage){

				for(int j=0; j<currentClass.getInterfaces().length; j++){
				//for(int j=0; j<currentClass.getSuperclass().length; j++){
					Class<?> currentInterface = currentClass.getInterfaces()[j];
					if(currentInterface.equals(interfaceClass)){
						returnedRepositoryStrategies.add(currentClass);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnedRepositoryStrategies;
	}
	 */

	public static Set<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String fileSeparator = System.getProperty("file.separator");
		String path = packageName.replace(".", fileSeparator);
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile())); //Not Whitespace save
			
			//File reourcesFile = new File(Thread.currentThread().getContextClassLoader().getResource(resource.toString()).getFile());//new
			//dirs.add(reourcesFile);//new
			
			//dirs.add(new File(resource.toURI())); //Whitespace safe
		}
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	private static Set<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
