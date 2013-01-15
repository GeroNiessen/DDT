package de.codecentric.ddt;

import de.codecentric.ddt.resourcestrategies.databases.Database;
import de.codecentric.ddt.resourcestrategies.databases.OracleDatabaseStrategy;

/**
 * Hello world!
 */
public class App
{
	public static void main( String[] args )
	{
		Database oracleDatabase = new Database();
		oracleDatabase.setName("ICIS_Oracle_Database");
		oracleDatabase.setUrl("jdbc:oracle:thin:@wgvli36.swlabor.local:1522:ICISPLUS");
		oracleDatabase.setStrategy(new OracleDatabaseStrategy());
		oracleDatabase.generateProxyClasses("de.wgvi.icisplus.adapter.jpub");
		
		System.out.println("Finished!");
	}
}
