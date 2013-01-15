package de.codecentric.ddt.web;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

import de.codecentric.ddt.deprecated.LocalDatabase;

import javax.ejb.DependsOn;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Gero Niessen
 */

//@WebServlet(urlPatterns = "/*", loadOnStartup=2)
@DependsOn("LocalDatabase")
public class MyVaadinApplicationServlet extends AbstractApplicationServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2924939355386218671L;
	@Inject
    Instance<MyVaadinApplication> application;

    @Override
    protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {
    	try {
    		System.out.println("########### Vaadin Servlet ##############");
			LocalDatabase.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return MyVaadinApplication.class;
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request) throws ServletException {
    	try {
    		System.out.println("########### Vaadin Servlet ##############");
			LocalDatabase.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return application.get();
    }

    /*
    @Override
    protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {
        return MyApplication.class;
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request) throws ServletException {
        try {
            InitialContext ic = new InitialContext();
            return (Application) ic.lookup("java:module/MyApplication");
        } catch (NamingException e) {
            throw new ServletException("Could not access application", e);
        }
    }
    */
 }
 
