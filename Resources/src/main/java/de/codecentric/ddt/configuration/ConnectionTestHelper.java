package de.codecentric.ddt.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionTestHelper {
	
	private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ConnectionTestHelper.class .getName());
	
	public static boolean isSocketOpen(String url, int timeOutInMilliSeconds){
		boolean returnedValue;
		URL aURL;
		try {
			aURL = new URL(url);
			returnedValue = isSocketOpen(aURL.getHost(), aURL.getPort(), timeOutInMilliSeconds);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			LOGGER.info(e.getMessage());
			returnedValue = false;
		}	
		return returnedValue;
	}
	
	public static void main(String [] args){
		System.out.println(isSocketOpen("http://wgvli39.swlabor.local:8282", 2000));
		System.out.println(testURLConnection("http://www.google.com", 2000));
		System.out.println(isSocketOpen("http://www.google.com", 2000));
	}
	
	public static boolean isSocketOpen(String host, int port, int timeOutInMilliSeconds){
		
		boolean returnedValue = false;
		
        Socket socket = new Socket(); 
        InetSocketAddress endPoint = new InetSocketAddress( host, port);
        if ( endPoint.isUnresolved() ) {
        	LOGGER.warning("Endpoint is not resolveable!");
        	returnedValue = false;
        } else try { 
            socket.connect(  endPoint , timeOutInMilliSeconds );
            returnedValue = true;
        } catch( IOException ioe ) {
        	LOGGER.warning(ioe.getMessage());
        	returnedValue = false;
        } finally {
            if ( socket != null ) try {
                socket.close();
            } catch( IOException ioe ) {}
        }
        return returnedValue;
	}
	
	public static boolean testURLConnection(String url, int timeOutInMilliseconds){
		boolean returnedValue = false;
		try {
		    URL myURL = new URL(url);
		    URLConnection myURLConnection = myURL.openConnection();
		    myURLConnection.setConnectTimeout(timeOutInMilliseconds);
		    myURLConnection.connect();
		    returnedValue = true;
		} 
		catch (IOException e) { 
			LOGGER.warning(e.getMessage());
		}
		return returnedValue;
	}
	
	public static void readWebPage() throws Exception {

        URL oracle = new URL("http://www.oracle.com/");
        try (BufferedReader in = new BufferedReader(
             new InputStreamReader(oracle.openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                System.out.println(inputLine);
            }
        }
    }
}
