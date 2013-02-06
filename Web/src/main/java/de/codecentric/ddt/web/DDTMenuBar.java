package de.codecentric.ddt.web;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import de.codecentric.ddt.configuration.ReflectionHelper;
import de.codecentric.ddt.web.applicationchecks.AbstractApplicationCheckComponent;
import java.util.Set;

/**
 * DDTMenuBar searches the class-path for all classes implementing AbstractApplicationCheck and add them to the menu-bar
 * @author Gero Niessen
 */
public class DDTMenuBar extends MenuBar {
	
	private static final long serialVersionUID = 4836045333392767191L;

	private TabSheet tabSheet;
	
	public DDTMenuBar(TabSheet tabSheet){
		super();
		this.tabSheet = tabSheet;
		init();
	}
	
        /**
         * Gets all classes implementing AbstractApplicationChecks in order to add them to the menu-bar
         */
	private void init(){
		Set<Class<?>> applicationCheckClasses = ReflectionHelper.getAllInstanciableImplementations("de.codecentric.ddt", AbstractApplicationCheckComponent.class);
		for(final Class<?> currentApplicationCheckClass: applicationCheckClasses){
			try {
                                //System.out.println("Trying to load:" + currentApplicationCheckClass.getCanonicalName());
				AbstractApplicationCheckComponent currentApplicationCheck = (AbstractApplicationCheckComponent) currentApplicationCheckClass.newInstance();
				final String currentApplicationCheckName = currentApplicationCheck.getCheckName();
				MenuBar.Command command = new MenuBar.Command() {

					private static final long serialVersionUID = 2163981526677621291L;

					public void menuSelected(MenuItem selectedItem) {
				    	try {
							final AbstractApplicationCheckComponent currentApplicationCheckComponent = (AbstractApplicationCheckComponent) currentApplicationCheckClass.newInstance();
							currentApplicationCheckComponent.init();
							final Tab newTab = tabSheet.addTab(currentApplicationCheckComponent, currentApplicationCheckName, null);
							newTab.setClosable(true);
						} catch (InstantiationException|IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				};
				addItem(currentApplicationCheckName, command);
			} catch (InstantiationException|IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
