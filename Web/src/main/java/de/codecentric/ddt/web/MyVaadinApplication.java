package de.codecentric.ddt.web;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import de.codecentric.ddt.web.configuration.ConfigurationForm;
import javax.ejb.DependsOn;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The Application's "main" class
 */

@SessionScoped
@DependsOn("LocalDatabase")
public class MyVaadinApplication extends Application implements HttpServletRequestListener
{
	private static ThreadLocal<MyVaadinApplication> threadLocal = new ThreadLocal<>(); 
	private static final long serialVersionUID = 5079596946272499850L;

	private Window mainWindow;
	private CustomComponent logPanel;
	private VerticalLayout superLayout;
	private HorizontalSplitPanel horizontalSplitPanel;
	private VerticalSplitPanel verticalSplitPanel;

	@Override
	public void init()
	{	
		setTheme("DDT-style");

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
		tabSheet.setSizeFull();
		DDTMenuBar menuBar = new DDTMenuBar(tabSheet);
		superLayout.addComponent(menuBar);

		ConfigurationForm configForm = new ConfigurationForm();
		horizontalSplitPanel.addComponent(configForm);

		verticalSplitPanel = new VerticalSplitPanel();
		verticalSplitPanel.addComponent(tabSheet);
		
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

	// @return the current application instance	  	
	public static MyVaadinApplication getInstance() { 		
		return threadLocal.get(); 	
	}

	public static Window getMainWindows(){
		return threadLocal.get().mainWindow;
	}

	public void showNotification(Notification notification){
		mainWindow.showNotification(notification);
	}

	@Override     
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		threadLocal.set(this);     
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