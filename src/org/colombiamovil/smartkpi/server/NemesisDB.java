package org.colombiamovil.smartkpi.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * The connection pool handler. The JNDI JDBC resource jdbc/NemesisDB must be created at the container.
 * 
 * @author Dahyr.Vergara
 **/
public class NemesisDB {

	private static Context ctx, ntCtx;
	private static DataSource ds, ntDs;
	private static boolean enabled;
	public static final int MYSQL_POOL = 1, ORACLE_POOL = 2;

	/**
	 * Static method that initializes the pool and set it's state
	 * */
	static {
		try {
			ctx = new InitialContext();
			if (ctx == null) throw new Exception("ERROR: No Initial Context");
			ds = (DataSource) ctx.lookup("jdbc/NemesisDB");
			if (ds == null) throw new Exception("ERROR: No Data Source");
			System.out.println("Pool enabled");
			setEnabled(true);
		} catch (Exception e) {
			System.out.println("There was an error initiating the pool: trying standalone pool MySQL");
			ds = new BasicDataSource();
			((BasicDataSource)ds).setDriverClassName("com.mysql.jdbc.Driver");
			((BasicDataSource)ds).setUrl("jdbc:mysql://"+NemesisConnector.HOST_NMS+"/nemesis?user=collector&password=16novnew&zeroDateTimeBehavior=convertToNull");
//			((BasicDataSource)ds).setUsername("nemesis");
//			((BasicDataSource)ds).setPassword("16novnew");
			((BasicDataSource)ds).setMaxActive(5);

			if (ds == null) {
				System.out.println("There was an error initiating the pool ORACLE: turning it disabled");
				e.printStackTrace();
				setEnabled(false);
			} else {
				System.out.println("Container's pool disabled, using standalone MySQL pool");
				setEnabled(true);
			}
		}
		try {
			ntCtx = new InitialContext();
			if (ntCtx == null) throw new Exception("ERROR: No Initial Context");
			ntDs = (DataSource) ntCtx.lookup("jdbc/NemesisTrendsDB");
			if (ntDs == null) throw new Exception("ERROR: No Data Source");
			System.out.println("Pool enabled");
			setEnabled(true);
		} catch (Exception e) {
			System.out.println("There was an error initiating the pool: trying standalone pool ORACLE");
			ntDs = new BasicDataSource();
			((BasicDataSource)ntDs).setDriverClassName("oracle.jdbc.driver.OracleDriver");
			((BasicDataSource)ntDs).setUrl("jdbc:oracle:thin:@10.65.32.44:4521:QATOOLS");
			((BasicDataSource)ntDs).setUsername("NEMESIS_WEB");
			((BasicDataSource)ntDs).setPassword("n3m3sis_w38");
			((BasicDataSource)ntDs).setMaxActive(10);

			if (ntDs == null) {
				System.out.println("There was an error initiating the pool ORACLE: turning it disabled");
				e.printStackTrace();
				setEnabled(false);
			} else {
				System.out.println("Container's pool disabled, using standalone ORACLE pool");
				setEnabled(true);
			}
		}
	}

	public static void test() throws SQLException {
		//System.out.println("Foo Testing");
		Connection conn = getConnection();
		if (conn != null) {
			Statement stmt = conn.createStatement();
			ResultSet rst = stmt.executeQuery("select * from wnms_categories");
			while (rst.next()) {
				System.out.println("Foo: " + rst.getString(2));
				System.out.println("Bar: " + rst.getInt(3));
			}
			conn.close();
		}
	}

	/**
	 * Get a connection from the pool
	 * */
	public static synchronized Connection getConnection() throws SQLException {
//		System.out.println("Getting Connection from Pool");
		return getConnection(MYSQL_POOL);
	}

	/**
	 * Get a connection from the pool
	 * */
	public static synchronized Connection getConnection(int poolName) throws SQLException {
//		System.out.println("Getting Connection from Pool");
		return poolName == MYSQL_POOL ? ds.getConnection() : ntDs.getConnection();
	}

	/**
	 * Close the connection and make it available for the pool again
	 * */
	public static synchronized void closeConnection(ResultSet rs) {
		if(rs == null) return;
		try {
			rs.getStatement().getConnection().close();
			rs = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setting the pool enabled / disabled
	 * 
	 * @param enabled The pool state
	 * */
	public static void setEnabled(boolean enabled) {
		NemesisDB.enabled = enabled;
	}

	/**
	 * Getting the pool's state
	 * */
	public static boolean isEnabled() {
		return enabled;
	}
}