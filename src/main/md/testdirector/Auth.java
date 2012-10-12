/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md.testdirector;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ricardo
 */
public class Auth {
	public String HOST = "mkva82.pcc.int";
	public String PORT = "8080";
	public String USERNAME = "";
	public String PASSWORD = "";
	public String DOMAIN = "DEFAULT";
	public String PROJECT = "MoreDirect";
	public boolean VERSIONED = true;
	public Map<String, String> cookies = new HashMap<String, String>();

	public Auth()
	{
	}
	public Auth(String host, String port, String user, String pass, String domain, String project, boolean versioned)
	{
		HOST = host;
		PORT = port;
		USERNAME = user;
		PASSWORD = pass;
		DOMAIN = domain;
		PROJECT = project;
		VERSIONED = versioned;
	}

	@Override
	public String toString()
	{
		return "Entity:\n\tHOST: "+ HOST + "\n" +
		"\tPORT: "+ PORT + "\n" +
		"\tUSERNAME: "+ USERNAME + "\n" +
		"\tPASSWORD: "+ PASSWORD + "\n" +
		"\tDOMAIN: "+ DOMAIN + "\n" +
		"\tPROJECT: "+ PROJECT + "\n" +
		"\tVERSIONED: "+ VERSIONED + "\n";
	}
}
