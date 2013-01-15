package de.codecentric.ddt;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

/*
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
*/

import de.codecentric.ddt.ApplicationDAO;

public class AbstractDAOTest {

    protected static EJBContainer   ejbContainer    = null;
    
    protected static ApplicationDAO applicationDAO  = null;
        
    protected static void initializeContainer(){
        Map<String, Object> p = new HashMap<String, Object>();
        p.put(EJBContainer.APP_NAME, "DDT");
        //p.put("web.http.port", "9797");
        ejbContainer = EJBContainer.createEJBContainer(p);
        assertNotNull("PLEASE STOPP THE GLASSFISH SERVER BEFORE RUNNING THESE TESTS!", ejbContainer);
    }
    
    protected static void initializeApplicationDAO(){
        try {
            //applicationDAO = (ApplicationDAO) ejbContainer.getContext().lookup("java:global/Web/ApplicationDAO!de.codecentric.ddt.ApplicationDAO");
        	applicationDAO = (ApplicationDAO) ejbContainer.getContext().lookup("java:global/DDT/classes/ApplicationDAO!de.codecentric.ddt.ApplicationDAO");
        	
        } catch (NamingException ex) {
            Logger.getLogger(AbstractDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(applicationDAO);
        assertTrue(applicationDAO instanceof ApplicationDAO);       
    }
}
