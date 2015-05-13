package org.colombiamovil.smartkpi.server;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Class that handles user authentication via Windows Active Directory LDAP connection.
 * 
 * @author Dahyr.Vergara
 * */
public class ActiveDirectoryAuthenticator {

	private String userDn;
	private String server;
	private String port;
	private String authExplanation;
	private static final String DEFAULT_PORT = "389", DEFAULT_SERVER = "10.65.52.6";
	private static final Map<String, String> errorsMap = new HashMap<String, String>();

	static {
		errorsMap.put("525", "User name not found");
		errorsMap.put("52e", "Invalid password, please try again");
		errorsMap.put("530", "Not permitted to logon at this time");
		errorsMap.put("531", "Not permitted to logon from this workstation");
		errorsMap.put("532", "Password has expired");
		errorsMap.put("533", "Account disabled");
		errorsMap.put("701", "Account expired");
		errorsMap.put("773", "User must reset password");
		errorsMap.put("775", "Account has been locked up");
	}

	public ActiveDirectoryAuthenticator() {
		setPort(DEFAULT_PORT);
		setServer(DEFAULT_SERVER);
	}

	public String authenticate(String password) {
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://" + this.getServer() + ":" + this.getPort());
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, this.getUserDn());
			env.put(Context.SECURITY_CREDENTIALS, password);
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
		} catch (CommunicationException comEx) {
			comEx.printStackTrace();
			return "ERROR (CommunicationException): " + comEx.getMessage();
		} catch (AuthenticationException authEx) {
			authEx.printStackTrace();
			authExplanation = authEx.getExplanation();
			int index = authExplanation.indexOf(", data ");
			authExplanation = errorsMap.get(authExplanation.substring(index + 7, index + 10));
			if(authExplanation == null) authExplanation = "Unknown cause, please contact system's administrator";
			return "ERROR (AuthenticationException): " + authExplanation;
		} catch (NamingException nameEx) {
			nameEx.printStackTrace();
			return "ERROR (NamingException): " + nameEx.getMessage();
		}
		return "OK";
	}

	public String getUserDn() {
		return userDn;
	}

	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}