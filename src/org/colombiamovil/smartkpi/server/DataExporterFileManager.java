package org.colombiamovil.smartkpi.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.colombiamovil.smartkpi.client.security.UserBean;
import org.colombiamovil.smartkpi.server.util.QueryUtils;

public class DataExporterFileManager {

	private static DataExporterFileManager instance;

	private DataExporterFileManager() {
	}

	public static DataExporterFileManager getInstance() {
		if(instance == null) {
			instance = new DataExporterFileManager();
		}
		return instance;
	}

	public String exportReport(String objectType, String iniDate, String endDate, String objects, String kpis, String tableType, UserBean user) {
		Map<String, String> paramsData = QueryUtils.getReportParametersData(objectType, tableType);
		String fileName = user.getUserLogin() + "_" + paramsData.get("dataTable") + "_" + iniDate + "-" + endDate + "_" + Calendar.getInstance().getTimeInMillis();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			String description = "Data Table: " + paramsData.get("dataTable") + "; Date Range: " + iniDate + " - " + endDate + "; Kpis: " + kpis;
			String query = QueryUtils.getReportQuery(paramsData, objects, kpis, iniDate, endDate, "", false, stmt);
			String insertQuery = "INSERT INTO cfg_web_filejobs VALUES " +
					"(DEFAULT, "+user.getUserUid()+", '"+description+"', 'nemesis', '"+query.replace("\'", "\\\'")+"', '"+fileName+"', " +
					" DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 2 DAY), 'Waiting', CURRENT_TIMESTAMP, '0000-00-00 00:00:00')";
			stmt.executeUpdate(insertQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}

	public Map<String, String[]> getQueuedJobs(UserBean user) {
		Map<String, String[]> result = new LinkedHashMap<String, String[]>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			String query = "SELECT * FROM cfg_web_filejobs WHERE user_uid = " + user.getUserUid() + " order by job_uid desc ";

			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				result.put(rs.getString("job_file_name"), new String[] {rs.getString("delivery_status"), rs.getString("job_description")});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String args[]) {
		UserBean user = new UserBean();
		user.setUserUid("1");
		user.setUserLogin("dahyr.vergara");
		(new DataExporterFileManager()).exportReport("msc", "20100101", "20100119", "'MSSBAR'", "ANSWER_CALLS", "bh", user);
	}
}
