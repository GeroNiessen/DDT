/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.codecentric.ddt;

import java.util.HashSet;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.awt.HorizBagLayout;

import com.vaadin.Application;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.codecentric.ddt.issuetrackers.JiraIssueTrackerStrategy;
import de.codecentric.ddt.repositories.MercurialRepositoryStrategy;



/**
 * The Application's "main" class
 */
//@SuppressWarnings("serial")
@SessionScoped
@DependsOn("LocalDatabase")
public class MyVaadinApplication extends Application implements HttpServletRequestListener
{
	private static ThreadLocal<MyVaadinApplication> threadLocal = new ThreadLocal<MyVaadinApplication>(); 
	private static final long serialVersionUID = 5079596946272499850L;

	private Window mainWindow;
	private CustomComponent logPanel;
	private Component mainComponent;
	private VerticalLayout superLayout;
	private Layout mainLayout;

	@EJB
	private JPAConfigurationDAO jpaConfigurationDAO;
	private ConfigurationDAO configurationDAO;

	@Override
	public void init()
	{	
		setTheme("DDT-style");
		
		Configuration testConfiguration = generateTestConfiguration();
		//configurationDAO = new XMLConfigurationDAO();
		//configurationDAO.save(testConfiguration);
		jpaConfigurationDAO.save(testConfiguration);
		
		//jpaConfigurationDAO = new JPAConfigurationDAO();
		//jpaConfigurationDAO.save(testConfiguration);

		mainWindow = new Window("Distributed Dependency Tracker (DDT)");
		mainWindow.setSizeFull();
		mainWindow.getContent().setSizeFull();
		setMainWindow(mainWindow);
		
		//Start Adding Test Data
		//ConfigurationContainerProvider configurationContainerProvider = new ConfigurationContainerProvider();
		//Configuration myConfiguration = configurationContainerProvider.getConfigurationFromContainer();
		//configurationDAO.save(myConfiguration);
		//String notification = myConfiguration.getApplications().iterator().next().getName();
		//notification = notification + myConfiguration.getApplications().iterator().next().getResources().iterator().next().getName();
		//mainWindow.showNotification(notification);
		//End Adding Test Data

		superLayout = new VerticalLayout();
		
		superLayout.setSizeFull();
		mainLayout = new HorizontalSplitPanel();
		mainLayout.setSizeFull();

		MenuComponent menuComponent = new MenuComponent(mainComponent);
		menuComponent.addMenuButtonAndComponent("Check Repository Merges", CheckRepositoryMergesComponent.class);
		menuComponent.addMenuButtonAndComponent("Check Generated Proxy Classes", CheckProxyClassesComponent.class);
		superLayout.addComponent(menuComponent);
		
		ConfigurationForm configForm = new ConfigurationForm();
		mainComponent = configForm;
		mainLayout.addComponent(mainComponent);
		
		logPanel = new LogPanel();
		mainLayout.addComponent(logPanel);
		
		superLayout.addComponent(mainLayout);
		superLayout.setExpandRatio(mainLayout, 1.0f);
		
		mainWindow.addComponent(superLayout);	
	}


	@Override
	public void terminalError(Terminal.ErrorEvent event) {
		// Call the default implementation.
		super.terminalError(event);

		// Some custom behaviour.
		if (getMainWindow() != null) {
			getMainWindow().showNotification(
					"An unchecked exception occured!",
					event.getThrowable().toString(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public Configuration generateTestConfiguration(){

		Configuration configuration = new Configuration();

		de.codecentric.ddt.Application icisPlusApplication = new de.codecentric.ddt.Application();
		icisPlusApplication.setName("ICIS_PLUS");

		//Repository
		Resource<MercurialRepositoryStrategy> icisPlusRepository = new Resource<MercurialRepositoryStrategy>(); 
		icisPlusRepository.setName("ICIS_Mercurial_Repository");
		icisPlusRepository.setUrl("http://wgvli39.swlabor.local:8282");
		icisPlusRepository.setStrategy(new MercurialRepositoryStrategy());

		Resource<JiraIssueTrackerStrategy> icisPlusIssueTracker = new Resource<JiraIssueTrackerStrategy>();
		icisPlusIssueTracker.setName("ICIS_Jira_IssueTracker");
		icisPlusIssueTracker.setUrl("http://wgvli39.swlabor.local:8181");
		icisPlusIssueTracker.setStrategy(new JiraIssueTrackerStrategy());

		Resource<OracleDatabaseStrategy> icisDatabase = new Resource<OracleDatabaseStrategy>();
		icisDatabase.setName("ICIS_Oracle_Database");
		icisDatabase.setUrl("jdbc:oracle:thin:@wgvli36.swlabor.local:1522:ICISPLUS");
		icisDatabase.setStrategy(new OracleDatabaseStrategy());

		//---------------------             
		icisPlusApplication.setResources(new HashSet<Resource<?>>());
		icisPlusApplication.getResources().add(icisPlusIssueTracker);
		icisPlusApplication.getResources().add(icisPlusRepository);
		icisPlusApplication.getResources().add(icisDatabase);

		//---------------------

		//---------------------
		de.codecentric.ddt.Application secondTestApplication = new de.codecentric.ddt.Application();
		secondTestApplication.setName("SecondTestApplication");
		//---------------------
		Resource<JiraIssueTrackerStrategy> firstIssueTracker = new Resource<JiraIssueTrackerStrategy>();
		firstIssueTracker.setName("FirstJiraIssueTrackerForFirstProject");
		firstIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		firstIssueTracker.setUrl("http://www.example.org");

		Resource<JiraIssueTrackerStrategy> secondIssueTracker = new Resource<JiraIssueTrackerStrategy>();
		secondIssueTracker.setName("FirstJiraIssueTrackerForSecondApplication");
		secondIssueTracker.setUrl("http://www.beispiel.de");
		secondIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		secondTestApplication.setResources(new HashSet<Resource<?>>());
		secondTestApplication.getResources().add(firstIssueTracker);
		secondTestApplication.getResources().add(secondIssueTracker);

		configuration.getApplications().add(secondTestApplication);
		configuration.getApplications().add(icisPlusApplication);
		return configuration;
	}

	// @return the current application instance	  	
	public static MyVaadinApplication getInstance() { 		
		return threadLocal.get(); 	
	}

	public static ConfigurationDAO getConfigurationDAO(){
		return threadLocal.get().configurationDAO;
	}

	public static Component getMainCompoment(){
		return threadLocal.get().mainComponent;
	}
	
	public static void setMainComponent(Component newMainComponent){
		threadLocal.get().mainLayout.replaceComponent(threadLocal.get().mainComponent, newMainComponent);
		threadLocal.get().mainComponent = newMainComponent;
	}
	
	public static Window getMainWindows(){
		return threadLocal.get().mainWindow;
	}
	
	public void showNotification(Notification notification){
		mainWindow.showNotification(notification);
	}

	// Set the current application instance
	public static void setInstance(MyVaadinApplication application) { 					
		threadLocal.set(application); 		
	}      

	@Override     
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		MyVaadinApplication.setInstance(this);     
	}      

	@Override     
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		threadLocal.remove();     
	}  


	@Override
	public void close() {
		super.close();
		WebApplicationContext webCtx = (WebApplicationContext) this.getContext();
		HttpSession session = webCtx.getHttpSession();
		session.invalidate();
	}
}
