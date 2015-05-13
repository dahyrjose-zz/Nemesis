package org.colombiamovil.smartkpi.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * The connection pool handler. The JNDI JDBC resource jdbc/NemesisDB must be
 * created at the container.
 * 
 * @author Dahyr.Vergara
 **/
public class CollectorDB {

	private static Context ctx;
	private static DataSource ds;
	private static boolean enabled;

	/**
	 * Static method that initializes the pool and set it's state
	 * */
	static {
		try {
			ctx = new InitialContext();
			if (ctx == null)
				throw new Exception("ERROR: No Initial Context");
			ds = (DataSource) ctx.lookup("jdbc/CollectorDB");
			if (ds == null)
				throw new Exception("ERROR: No Data Source");
			System.out.println("Pool enabled");
			setEnabled(true);
		} catch (Exception e) {
			System.out
					.println("There was an error initiating the Collector pool: turning it disabled");
			e.printStackTrace();
			setEnabled(false);
		}
	}

	public static void test() throws SQLException {
		// System.out.println("Foo Testing");
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
		// System.out.println("Getting Connection from Pool");
		return ds.getConnection();
	}

	/**
	 * Close the connection and make it available for the pool again
	 * */
	public static synchronized void closeConnection(ResultSet rs) {
		if (rs == null)
			return;
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
	 * @param enabled
	 *            The pool state
	 * */
	public static void setEnabled(boolean enabled) {
		CollectorDB.enabled = enabled;
	}

	/**
	 * Getting the pool's state
	 * */
	public static boolean isEnabled() {
		return enabled;
	}
}