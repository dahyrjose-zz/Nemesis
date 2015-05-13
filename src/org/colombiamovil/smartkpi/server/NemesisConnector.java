package org.colombiamovil.smartkpi.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class decides whether to use the connection pool or direct database connections.
 * 
 * @author Dahyr.Vergara
 */
public class NemesisConnector {

	public static final String HOST_NMS = "10.65.136.17";

	/**
	 * Get a new connection for the nemesis database using either container's connection pooling
	 * or standard JDBC connection according to availability.
	 * 
	 * It's the same as using <code>getConnection("nemesis")</code>
	 * */
	public static Connection getConnection() throws SQLException {
		if(NemesisDB.isEnabled()) {
//			System.out.println("Data source enabled: using NEW JNDI pool connection");
			return NemesisDB.getConnection();
		} else {
			System.out.println("Data source disabled: using NEW direct JDBC connection");
			return newConnection(HOST_NMS, "nemesis", "collector", "16novnew");
		}
	}

	/**
	 * Loads the new connection the same way as <code>getConnection()</code> according
	 * to the specified database.
	 * */
	public static Connection getConnection(String db) throws SQLException {
		if(db.startsWith("nemesis")) return getConnection();
		if(db.equals("NEMESIS_TRENDS")) return NemesisDB.getConnection(NemesisDB.ORACLE_POOL);
		if(CollectorDB.isEnabled()) {
			System.out.println("Collector data source enabled: using NEW JNDI pool connection");
			return CollectorDB.getConnection();
		} else {
			System.out.println("Collector data source disabled: using NEW direct JDBC connection");
			return newConnection("10.65.136.17:3307", db, "collector", "16novnew");
		}
	}

	/*public static Statement getNemesisStatement() throws SQLException {
		if(NemesisDB.isEnabled()) {
			System.out.println("Data source enabled: using NEW JNDI pool connection");
			return NemesisDB.getConnection().createStatement();
		} else {
			System.out.println("Data source disabled: using NEW direct JDBC connection");
			return newConnection(HOST_NMS, "nemesis", "collector", "16novnew").createStatement();
		}
	}*/

	/*public static PreparedStatement getNemesisPreparedStatement(String query) throws SQLException {
		if(NemesisDB.isEnabled()) {
			System.out.println("Data source enabled: using NEW JNDI pool connection");
			return NemesisDB.getConnection().prepareStatement(query);
		} else {
			System.out.println("Data source disabled: using NEW direct JDBC connection");
			return newConnection(HOST_NMS, "nemesis", "collector", "16novnew").prepareStatement(query);
		}
	}*/

	public static void closeConnection(ResultSet rs) {
		if(rs == null) return;
		System.out.println("Closing connection");
		try {
			closeConnection(rs.getStatement());			
			rs = null;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public static void closeConnection(PreparedStatement ps) {
		if(ps == null) return;
		System.out.println("Closing connection");
		try {
			ps.getConnection().close();
			ps = null;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public static void closeConnection(Statement s) {
		if(s == null) return;
		System.out.println("Closing connection");
		try {
			s.getConnection().close();
			s = null;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	private static Connection newConnection(String dbHost, String dbName, String dbUser, String dbPass) {
		Connection conn = null;
		String strConnect;
		int retryCount = 3;
		boolean transactionCompleted = false;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		do {
			try {
				strConnect = "jdbc:mysql://"+dbHost+"/"+dbName+"?user="+dbUser+"&password="+dbPass+"&zeroDateTimeBehavior=convertToNull";
				conn = DriverManager.getConnection(strConnect);
				transactionCompleted = true;
			} catch (SQLException sqlEx) {
				System.out.println("Error en conexión, intento ("+retryCount+") mensaje "+sqlEx.getMessage());
				sqlEx.printStackTrace();				
				String sqlState = sqlEx.getSQLState();
				if ("08S01".equals(sqlState) || "41000".equals(sqlState)) {
					retryCount--;
				} else {
					retryCount = 0;					
				}
			}catch(Exception e) {
				System.out.println("Error en conexión, intento ("+retryCount+") mensaje "+e.getMessage());
				e.printStackTrace();
			}
		} while (!transactionCompleted && (retryCount > 0));
		return conn;
	}
}
