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
package de.codecentric.ddt.web;

import java.util.HashSet;

import javax.ejb.DependsOn;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.codecentric.ddt.configuration.*;
import de.codecentric.ddt.resourcestrategies.databases.OracleDatabaseStrategy;
import de.codecentric.ddt.resourcestrategies.issuetrackers.JiraIssueTrackerStrategy;
import de.codecentric.ddt.resourcestrategies.repositories.MercurialRepositoryStrategy;
import de.codecentric.ddt.web.configuration.ConfigurationForm;

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
	private HorizontalSplitPanel horizontalSplitPanel;
	private VerticalSplitPanel verticalSplitPanel;
	
	private ConfigurationDAO configurationDAO;

	@Override
	public void init()
	{	
		setTheme("DDT-style");
		
		Configuration testConfiguration = generateTestConfiguration();
		configurationDAO = new XMLConfigurationDAO();
		configurationDAO.save(testConfiguration);
				
		mainWindow = new Window("Distributed Dependency Tracker (DDT)");
		mainWindow.setSizeFull();
		mainWindow.getContent().setSizeFull();
		setMainWindow(mainWindow);
		
		superLayout = new VerticalLayout();
		superLayout.setSizeFull();
		
		horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setSizeFull();
		horizontalSplitPanel.setSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
		
		TabSheet tabSheet = new TabSheet();
		DDTMenuBar menuBar = new DDTMenuBar(tabSheet);
		superLayout.addComponent(menuBar);
	
		ConfigurationForm configForm = new ConfigurationForm();
		horizontalSplitPanel.addComponent(configForm);

		verticalSplitPanel = new VerticalSplitPanel();
		mainComponent = tabSheet;//;new Label("Ready");
		verticalSplitPanel.addComponent(mainComponent);
		
		logPanel = new LogComponent();
		verticalSplitPanel.addComponent(logPanel);
		verticalSplitPanel.setSplitPosition(80, Sizeable.UNITS_PERCENTAGE);		
		
		horizontalSplitPanel.addComponent(verticalSplitPanel);
		
		superLayout.addComponent(horizontalSplitPanel);
		superLayout.setExpandRatio(horizontalSplitPanel, 1.0f);
		
		mainWindow.addComponent(superLayout);	
	}


	@Override
	public void terminalError(Terminal.ErrorEvent event) {
		super.terminalError(event);

		if (getMainWindow() != null) {
			getMainWindow().showNotification(
					//event.getThrowable().getCause().getMessage(),
					"An unchecked exception occured!",
					event.getThrowable().toString(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public Configuration generateTestConfiguration(){

		Configuration configuration = new Configuration();

		de.codecentric.ddt.configuration.Application icisPlusApplication = new de.codecentric.ddt.configuration.Application();
		icisPlusApplication.setName("ICIS_PLUS");

		//Repository
		Resource icisPlusRepository = new Resource(); 
		icisPlusRepository.setName("ICIS_Mercurial_Repository");
		icisPlusRepository.setUrl("http://wgvli39.swlabor.local:8282");
		icisPlusRepository.setStrategy(new MercurialRepositoryStrategy());

		Resource icisPlusIssueTracker = new Resource();
		icisPlusIssueTracker.setName("ICIS_Jira_IssueTracker");
		icisPlusIssueTracker.setUrl("http://wgvli39.swlabor.local:8181");
		icisPlusIssueTracker.setStrategy(new JiraIssueTrackerStrategy());

		Resource icisDatabase = new Resource();
		icisDatabase.setName("ICIS_Oracle_Database");
		icisDatabase.setUrl("jdbc:oracle:thin:@wgvli36.swlabor.local:1522:ICISPLUS");
		icisDatabase.setStrategy(new OracleDatabaseStrategy());

		//---------------------             
		icisPlusApplication.setResources(new HashSet<Resource>());
		icisPlusApplication.getResources().add(icisPlusIssueTracker);
		icisPlusApplication.getResources().add(icisPlusRepository);
		icisPlusApplication.getResources().add(icisDatabase);

		//---------------------

		//---------------------
		de.codecentric.ddt.configuration.Application secondTestApplication = new de.codecentric.ddt.configuration.Application();
		secondTestApplication.setName("SecondTestApplication");
		//---------------------
		Resource firstIssueTracker = new Resource();
		firstIssueTracker.setName("FirstJiraIssueTrackerForFirstProject");
		firstIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		firstIssueTracker.setUrl("http://www.example.org");

		Resource secondIssueTracker = new Resource();
		secondIssueTracker.setName("FirstJiraIssueTrackerForSecondApplication");
		secondIssueTracker.setUrl("http://www.beispiel.de");
		secondIssueTracker.setStrategy(new JiraIssueTrackerStrategy());
		secondTestApplication.setResources(new HashSet<Resource>());
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
		threadLocal.get().verticalSplitPanel.replaceComponent(threadLocal.get().mainComponent, newMainComponent);
		threadLocal.get().mainComponent = newMainComponent;
	}
	
	public static Window getMainWindows(){
		return threadLocal.get().mainWindow;
	}
	
	public void showNotification(Notification notification){
		mainWindow.showNotification(notification);
	}

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