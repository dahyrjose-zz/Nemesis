package org.colombiamovil.smartkpi.server;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.colombiamovil.smartkpi.client.NemesisException;
import org.colombiamovil.smartkpi.client.NemesisService;
import org.colombiamovil.smartkpi.client.alarms.CfgAlarm;
import org.colombiamovil.smartkpi.client.alarms.HistoryAlarm;
import org.colombiamovil.smartkpi.client.screener.KpiObjectValues;
import org.colombiamovil.smartkpi.client.security.UserBean;
import org.colombiamovil.smartkpi.client.ui.charts.ControllerConfigBean;
import org.colombiamovil.smartkpi.client.ui.drill.CorrelationBean;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportCategory;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportItem;
import org.colombiamovil.smartkpi.client.ui.savedreports.SavedReportBean;
import org.colombiamovil.smartkpi.server.chart.ChartDataElement;
import org.colombiamovil.smartkpi.server.chart.ChartTimeLine;
import org.colombiamovil.smartkpi.server.util.QueryUtils;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.layout.CellSetFormatter;
import org.olap4j.layout.RectangularCellSetFormatter;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class NemesisServiceImpl extends RemoteServiceServlet implements NemesisService {

	private double[] kpiValues;
//	private static TreeMap<String, Integer> objectsMap = new TreeMap<String, Integer>();
	private static TreeMap<String, Integer> objectsCount = new TreeMap<String, Integer>();
//	private Vector<String[]> result;
//	private static SimpleDateFormat sdfDtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static SimpleDateFormat sdfAdf = new SimpleDateFormat("EEE MMM dd z yyyy");
	private static String dataMode = "OLAP";
	private static final boolean devMode = true;

	public String userLogin(String userName, String password) {
		ActiveDirectoryAuthenticator auth = new ActiveDirectoryAuthenticator();
		//auth.setUserDn("CN=Dahyr.Vergara,DC=COLOMBIAMOVIL,DC=BOGDVERGARA01");
		auth.setUserDn("COLOMBIAMOVIL\\" + userName);
		String answer = auth.authenticate(password);
		System.out.println(answer);
		return answer;
	}

	public UserBean initSession(String userName, String password, boolean withDomain) {
		UserBean userBean = new UserBean();
		userBean.setSessionId("no-session");
		String query = "select user_uid, user_login, user_id_number, user_msisdn, user_login_method, " +
				"MD5('"+password+"') = user_password pass_validation " +
				"from cfg_web_users where lower(user_login) = lower('"+userName+"')";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String userLoginMethod;
		boolean validated = false;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				userBean.setUserUid(rs.getString("user_uid"));
				userBean.setUserLogin(rs.getString("user_login"));
				userBean.setUserIdNumber(rs.getString("user_id_number"));
				userBean.setUserMsisdn(rs.getString("user_msisdn"));
				userLoginMethod = rs.getString("user_login_method");
				//userBean.setCurSessionStartTime(rs.getString("cur_session_start_time"));
				if(userLoginMethod.equalsIgnoreCase("DOMAIN") && withDomain) {
					validated = userLogin(userName, password).equals("OK");
					userLoginMethod = "Domain ";
				} else if(userLoginMethod.equalsIgnoreCase("LOCAL") || userLoginMethod.equalsIgnoreCase("DOMAIN")) {
					validated = rs.getBoolean("pass_validation");
					userLoginMethod = "Local User ";
				} else if(userLoginMethod.equalsIgnoreCase("ALWAYS-GRANTED")) {
					validated = true;
					userLoginMethod = "Always Granted ";
				} else {
					validated = false;
				}
				if(validated) {
					// Set the current session id and expiration time. Expiration is by default 24 hours after the current date
					stmt.execute("update cfg_web_users set cur_session_id = MD5(concat(user_login, user_id_number, current_timestamp)), " +
							"cur_session_start_time = current_timestamp, cur_session_expiration = timestampadd(hour, 24, current_timestamp) " +
							"where user_login = '"+userName+"'");
					query = "select cur_session_start_time, cur_session_id from cfg_web_users where user_login = '"+userName+"'";
					if(rs != null) rs.close();
					rs = stmt.executeQuery(query);
					if(rs.next()) {
						userBean.setSessionId(rs.getString("cur_session_id"));
						userBean.setCurSessionStartTime(rs.getString("cur_session_start_time"));
					}
					QueryUtils.registerUserAction(userBean.getUserLogin(), "Login", "initSession", userLoginMethod + "Session", "OK", Integer.toString(userBean.toString().length()));
				} else {
					userBean.setSessionId("no-pass");
					QueryUtils.registerUserAction(userBean.getUserLogin(), "Login", "initSession", userLoginMethod + "Session", "Wrong Password", Integer.toString(userBean.toString().length()));
				}
			} else {
				userBean.setSessionId("no-user");
				QueryUtils.registerUserAction(userBean.getUserLogin(), "Login", "initSession", "Session", "Wrong User Name", Integer.toString(userBean.toString().length()));
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
		return userBean;
	}

	public String checkSession(String sessionId) {
		System.out.println("Checking session id: " + sessionId);
		String query = "select cur_session_expiration > current_timestamp session_check " +
				"from cfg_web_users where cur_session_id = '"+sessionId+"'";
		String answer = "no-session";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				answer = rs.getBoolean("session_check") ? "session-ok" : "session-expired";
			}
			if(answer.equals("session-ok")) {
				stmt.executeUpdate("update cfg_web_users set cur_session_expiration = date_add(current_timestamp, interval 24 hour) " +
						"where cur_session_id = '"+sessionId+"'");
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
		return answer;
	}

	public String[] getScreenerKpis(String objectType) {
		ArrayList<String> kpis = new ArrayList<String>();
		if (objectType.contains("route")) {
			kpis.add("carried_calls");
			kpis.add("ic_carried_calls");
			kpis.add("og_carried_calls");
			kpis.add("carried_calls_with_answer");
		} else if (objectType.contains("linkset")) {
			kpis.add("transmitted_octets");
			kpis.add("received_octets");
			kpis.add("bytes_capacity");
			kpis.add("perc_availability");
		}
		return kpis.toArray(new String[0]);
	}

	/**
	 * Objects and values to fill the KpiScreener
	 * 
	 * @param dataTable The data table
	 * @param date The required date
	 * @param kpis Indicators list
	 * @param constraints List of constraints to filter data
	 * 
	 * @return The objects and it's values for the given kpis
	 * */
	public KpiObjectValues[] getScreenerObjects(String dataTable, String date,	String[] kpis, String[] constraints) {
		ArrayList<KpiObjectValues> objects = new ArrayList<KpiObjectValues>();
		String columns = "";
		String whereClause = "";
		String orderClause = "";
		String objectType = dataTable.split("_")[1];
		String objectTable = "nms_" + objectType;
		String objectIdField = "id";
		String dataIdField = "id";
		for (int i = 0; i < kpis.length; i++) {
			columns += ", " + kpis[i];
			orderClause += ", " + kpis[i] + " desc";
		}
		for (int i = 0; i < constraints.length; i++) {
			whereClause += " and " + constraints[i];
		}
		orderClause = "order by " + orderClause.substring(2);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"select * from cfg_web_objecttables ot inner join cfg_web_datatables dt using (object_table) " +
					"where data_table = '" + dataTable + "';");
			if(rs.next()) {
				objectTable = rs.getString("object_table");
				objectIdField = rs.getString("object_id_field");
				dataIdField = rs.getString("data_id_field");
				objectType = rs.getString("name_field");
			}
			String query = "select " + objectType + columns + " " +
					"from " + dataTable + " d inner join " + objectTable + " o on(o."+objectIdField+" = d."+dataIdField+") " + 
					"where x_date = '" + date + "' " + whereClause + " " + orderClause + " limit 20";
			System.out.println(Calendar.getInstance().getTime()	+ " - Executing: " + query);
			if(rs != null) rs.close();
			rs = stmt.executeQuery(query);
			System.out.println(Calendar.getInstance().getTime() + " - Executed!!!");
			while (rs.next()) {
				kpiValues = new double[kpis.length];
				for (int j = 0; j < kpis.length; j++) {
					kpiValues[j] = rs.getDouble(kpis[j]);
				}
				objects.add(new KpiObjectValues(rs.getString(1), kpiValues));
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
		return objects.toArray(new KpiObjectValues[0]);
	}


	public String[][] getChartData(String objType, String iniDate, String endDate, String kpis, String objects, UserBean user) throws NemesisException {
		return getChartData(objType, iniDate, endDate, kpis, objects, "bh", user);
	}

	public String[][] getChartData(String alarmId, UserBean user) throws NemesisException {
		String query = "select * from cfg_alarms_history h inner join cfg_alarms a on(h.id_alarm = a.id) " +
				"where history_id = '" + alarmId + "';";
		String alarmedDate, alarmedElement, object, indicator, period;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				object = rs.getString("object");
				period = rs.getString("period");
				indicator = rs.getString("indicator");
				alarmedDate = rs.getString("alarmed_date");
				alarmedElement = rs.getString("alarmed_element");
				if(period == null) {
					return getChartData(object, "date_sub('" + alarmedDate + "', interval 1 day)", alarmedDate, indicator, alarmedElement, "det", user);
				} else {
					return getChartData(object, "date_sub('" + alarmedDate + "', interval 1 month)", alarmedDate, indicator, alarmedElement, "bh", user);
				}
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
		return null;
	}


	/**
	 * Method to build the indicators tree for the report screen
	 * 
	 * @param objectType The object type
	 * 
	 * @return Map with the tree structure
	 * */
	public LinkedHashMap<String, ArrayList<String[]>> getKpis(String objectType) {
		if(devMode) System.out.println(new Date() + " KPI from: " + objectType);
		String[] objectParts = objectType.split("@@@");
		String cubeName = objectParts[0], 
				dimension = objectParts[1],
				dimValue = objectParts[2], 
				filterMdx = objectParts[3];
		LinkedHashMap<String, ArrayList<String[]>> families = new LinkedHashMap<String, ArrayList<String[]>>();
		Connection con = null;
		OlapConnection oc = null;
		String mdx = "WITH MEMBER [Measures].[UniqueName] AS [Indicator].[Indicator Agg].CURRENTMEMBER.UNIQUENAME "
				+ "SELECT [Measures].[UniqueName] ON 0, "
				+ "CrossJoin([Indicator].[Family].Children, [Indicator].[Indicator Agg].Children) ON 1 "
				+ "FROM [" + cubeName + "] ";
		try {
			if(devMode) System.out.println(new Date() + " Running KPI Fetch: " + mdx);
			con = NemesisOLAP.getConnection();
			oc = con.unwrap(OlapConnection.class);
			OlapStatement stmt = oc.createStatement();
			CellSet results = stmt.executeOlapQuery(mdx);
			String family, description, formula, units;
			Position axis0 = results.getAxes().get(0).getPositions().get(0);
			for (Position axis1 : results.getAxes().get(1).getPositions()) {
				if(devMode) System.out.println(new Date() + " New KPI: " + axis1.getMembers().get(0).getCaption());
				family = axis1.getMembers().get(0).getCaption();
				if(families.get(family) == null) {
					families.put(family, new ArrayList<String[]>());
				}
//				description = m.getCaption();
//				formula = "";
//				units = "";
//				description = description == null ? "" : description.length() == 0 ? "" : "<strong>Description:</strong> " + description;
//				formula = formula == null ? "" : formula.length() == 0 ? "" : (!description.equals("") ? "<br/>" : "") + 
//						"<strong>Formula:</strong> " + formula.replace("+", " + ")
//						.replace("-", " - ").replace("*", " * ").replace("/", " / ");
//				units = units == null ? "" : units.length() == 0 ? "" : (!description.equals("") || !formula.equals("") ? "<br/>" : "") + 
//						"<strong>Units:</strong> " + units;
				families.get(family).add(new String[] { axis1.getMembers().get(1).getCaption(), results.getCell(axis0, axis1).getValue().toString() });
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
		} finally {
			try {
				if(con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(new Date() + " Fetched KPI");
		return families;


//		if(devMode) System.out.println(new Date() + " KPI from: " + objectType);
//		LinkedHashMap<String, ArrayList<String[]>> families = new LinkedHashMap<String, ArrayList<String[]>>();
//		String query = "SELECT DISTINCT FAMILY_DS, INDICATOR_DS, INDICATOR_DS, 'FORMULE', 'UNITS' FROM INDICATOR_DIM WHERE ENABLE_BL = 1";
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			conn = NemesisConnector.getConnection("NEMESIS_TRENDS");
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(query);
//			String description;
//			String family;
//			String formula = "";
//			String units = "";
//			while(rs.next()) {
//				family = rs.getString(1);
//				if(families.get(family) == null) {
//					families.put(family, new ArrayList<String[]>());
//				}
//				description = rs.getString(3);
//				formula = rs.getString(4);
//				units = rs.getString(5);
//				description = description == null ? "" : description.length() == 0 ? "" : "<strong>Description:</strong> " + description;
//				formula = formula == null ? "" : formula.length() == 0 ? "" : (!description.equals("") ? "<br/>" : "") + 
//						"<strong>Formula:</strong> " + formula.replace("+", " + ")
//						.replace("-", " - ").replace("*", " * ").replace("/", " / ");
//				units = units == null ? "" : units.length() == 0 ? "" : (!description.equals("") || !formula.equals("") ? "<br/>" : "") + 
//						"<strong>Units:</strong> " + units;
//				families.get(family).add(new String[] {rs.getString(2), description + formula + units});
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if(rs != null) rs.close();
//				if(stmt != null) stmt.close();
//				if(conn != null) conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return families;

//		LinkedHashMap<String, ArrayList<String[]>> families = new LinkedHashMap<String, ArrayList<String[]>>();
//		String query = "select f.logic_name family, k.logic_name name, description_kpi description, formule_kpi formule, units " +
//				"from cfgmap_report_family_kpi m, cfg_web_reports r, cfg_family f, cfg_kpi_description k " +
//				"where m.reportid = r.id and m.familyid = f.id and m.kpiid = k.id and r.logic_name = 'nms_"+objectType+"' " +
//				"order by f.logic_name, k.logic_name";
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			conn = NemesisConnector.getConnection();
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(query);
//			String description;
//			String family;
//			String formula = "";
//			String units = "";
//			while(rs.next()) {
//				family = rs.getString(1);
//				if(families.get(family) == null) {
//					families.put(family, new ArrayList<String[]>());
//				}
//				description = rs.getString(3);
//				formula = rs.getString(4);
//				units = rs.getString(5);
//				description = description == null ? "" : description.length() == 0 ? "" : "<strong>Description:</strong> " + description;
//				formula = formula == null ? "" : formula.length() == 0 ? "" : (!description.equals("") ? "<br/>" : "") + 
//						"<strong>Formula:</strong> " + formula.replace("+", " + ")
//						.replace("-", " - ").replace("*", " * ").replace("/", " / ");
//				units = units == null ? "" : units.length() == 0 ? "" : (!description.equals("") || !formula.equals("") ? "<br/>" : "") + 
//						"<strong>Units:</strong> " + units;
//				families.get(family).add(new String[] {rs.getString(2), description + formula + units});
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if(rs != null) rs.close();
//				if(stmt != null) stmt.close();
//				if(conn != null) conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return families;
	}

	/**
	 * Method to get the objects
	 * 
	 * @deprecated It's faster to use {@link} getObjectsListBox(String objectType)
	 * */
	@Deprecated
	public String[] getObjects(String objectTable) {
		System.out.println(new Date() + " Fetching Objects");
		ArrayList<String> objects = new ArrayList<String>();
		String nameField = objectTable.split("_")[1];
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "select name_field from cfg_web_objecttables where object_table = '" + objectTable + "'";
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				nameField = rs.getString("name_field");
			}
			NemesisConnector.closeConnection(rs);
			query = "select " + nameField + " from " + objectTable + " order by 1";
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				objects.add(rs.getString(1));
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
		System.out.println(new Date() + " Fetched Objects");
		return objects.toArray(new String[0]);
	}

	/**
	 * Returns the report types of a given objectType
	 * 
	 * @param objectType The object type
	 * @return Mapped report types
	 * */
	public String[] getReportTypes(String objectType) throws NemesisException {
		System.out.println(new Date() + " Fetching ReportTypes");
		ArrayList<String> repTypes = new ArrayList<String>();
		Connection con = null;
		OlapConnection oc = null;
		try {
			con = NemesisOLAP.getConnection();
			oc = con.unwrap(OlapConnection.class);
			Cube ran = oc.getOlapSchema().getCubes().get("Ran");
			repTypes.add("Detail@@Detail");
			for(Member m : ran.getDimensions().get("Aggregation").getHierarchies().get("Type Agg").getDefaultMember().getChildMembers()) {
				if(!m.getCaption().equals("-1")) repTypes.add(m.getName() + "@@" + m.getCaption());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
		} finally {
			try {
				if(con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(new Date() + " Fetched Report Types");
		return repTypes.toArray(new String[0]);

//		System.out.println(new Date() + " Fetching ReportTypes");
//		ArrayList<String> repTypes = new ArrayList<String>();
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			String query = "select distinct table_type, type_description from cfg_web_datatables " +
//					"inner join cfg_web_tabletypes using(table_type) where object_type = '" + objectType + "'";
//			conn = NemesisConnector.getConnection();
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(query);
//			while(rs.next()) {
//				repTypes.add(rs.getString(1) + "@@" + rs.getString(2));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new NemesisException(e.getMessage());
//		} finally {
//			try {
//				if(rs != null) rs.close();
//				if(stmt != null) stmt.close();
//				if(conn != null) conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		System.out.println(new Date() + " Fetched Objects");
//		return repTypes.toArray(new String[0]);
	}

	/**
	 * Method to get the objects in HTML to embed directly in a Custom GWT Widget
	 * 
	 * @param objectType The object type
	 * @return The HTML code of the object's list box
	 * */
	public String getObjectsListBox(String objectType) throws NemesisException {
		return getObjectsListBox(objectType, null);
	}

	/**
	 * Method to get the objects in HTML to embed directly in a Custom GWT Widget
	 * 
	 * @param objectType The object type
	 * @return The HTML code of the object's list box
	 * */
	public String getObjectsListBox(String objectType, String filter) throws NemesisException {
		if(devMode) System.out.println(new Date() + " Fetching Objects: " + objectType);
		StringBuffer objects = new StringBuffer();
		String[] objectParts = objectType.split("@@@");
		String cubeName = objectParts[0], 
				dimension = objectParts[1],
				dimValue = objectParts[2], 
				filterMdx = objectParts[3];
		String mdx = "WITH MEMBER [Measures].[UniqueName] AS " + dimension + ".CURRENTMEMBER.UNIQUENAME "
				+ "SELECT [Measures].[UniqueName] ON 0, "
				+ "TOPCOUNT(" + (filterMdx.equals("NOFILTER") ? dimension + "." + dimValue : "Filter(" + dimension + "." + dimValue + ", " + filterMdx + ")") + ", 1000) ON 1 "
//				+ "TOPCOUNT(" + dimension + "." + dimValue + ", 1000) ON 1 "
//				+ "" + dimension + "." + dimValue + " ON 1 " 
				+ "FROM [" + cubeName + "] ";
		objects.append("<SELECT id='OLB' NAME='OLB' MULTIPLE SIZE='15' style='width: 100%; font-size: 11px'>");
		Connection con = null;
		OlapConnection oc = null;
		OlapStatement stmt = null;
		try {
			if(devMode) System.out.println(new Date() + " Running Objects ListBox: " + mdx);
			con = NemesisOLAP.getConnection();
			oc = con.unwrap(OlapConnection.class);
			stmt = oc.createStatement();
			CellSet results = stmt.executeOlapQuery(mdx);
//			String[] filters = filter == null ? null : filter.split(",");
			Position axis0 = results.getAxes().get(0).getPositions().get(0);
			for (Position axis1 : results.getAxes().get(1).getPositions()) {
				if(devMode) System.out.println(new Date() + " New Object: " + axis1.getMembers().get(0).getCaption() + " - " + results.getCell(axis0, axis1).getValue().toString());
				objects.append("\n <OPTION VALUE='"+results.getCell(axis0, axis1).getValue().toString()+"'>" + axis1.getMembers().get(0).getCaption() + "</OPTION>");
			}
//			for(Member m : d.getHierarchies().get(dimValue).getDefaultMember().getChildMembers()) {
//				if(filter != null) {
//					for(String f : filters) {
//						if(m.getName().toUpperCase().contains(f.replace("%", "").toUpperCase().trim())) {
//							objects.append("\n <OPTION VALUE='"+m.getName()+"'>" + m.getCaption() + "</OPTION>");
//							break;
//						}
//					}
//				} else {
//					objects.append("\n <OPTION VALUE='"+m.getName()+"'>" + m.getCaption() + "</OPTION>");
//				}
//			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(new Date() + " Fetched Objects");
		objects.append("\n</SELECT>");
		return objects.toString();

//		System.out.println(new Date() + " Fetching Objects");
//		StringBuffer objects = new StringBuffer();
//		objects.append("<SELECT id='OLB' NAME='OLB' MULTIPLE SIZE='15' style='width: 100%; font-size: 11px'>");
//		String tableName = "nms_" + objectType;
//		String nameField = objectType;
//		String nameAlias = nameField;
//		String idField = "id";
//		String whereClause = "";
//		String objectsConstraint = "";
//		String filterString = "";
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			String query = "select object_id_field, object_table, name_field, name_alias, objects_constraint " +
//					"from cfg_web_objecttables where object_type = '" + objectType + "'";
//			conn = NemesisConnector.getConnection();
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(query);
//			if(rs.next()) {
//				idField = rs.getString("object_id_field");
//				tableName = rs.getString("object_table");
//				nameField = rs.getString("name_field");
//				nameAlias = rs.getString("name_alias");
//				objectsConstraint = rs.getString("objects_constraint");
//				if(filter != null) {
//					for (String f : filter.split(",")) {
//						filterString += "or " + nameField + " like '" + f + "%' ";
//					}
//				}
//				whereClause = filterString.length() < 3 ? "" : 
//					"where " + filterString.substring(3);
//				if(objectsConstraint != null) {
//					whereClause += whereClause.startsWith("where") ? " and " + objectsConstraint : " where " + objectsConstraint;
//				}
//			}
//			query = "select " + idField + ", " + nameField + " " + nameAlias + " from " + tableName + " o " + whereClause + " order by 2";
//			System.out.println(query);
//			if(rs != null) rs.close();
//			rs = stmt.executeQuery(query);			
//			while(rs.next()) {
//				objects.append("\n <OPTION VALUE='"+rs.getString(idField)+"'>" + rs.getString(nameAlias) + "</OPTION>");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			System.out.println("ERROR: " + e.getMessage());
//			System.out.println("CAUSE: " + e.getCause());
//			throw new NemesisException(e.getMessage().contains("Table") ? "Unknown report, please contact system's administrator" : e.getMessage());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("ERROR: " + e.getMessage());
//			System.out.println("CAUSE: " + e.getCause());
//			throw new NemesisException(e.getMessage());
//		} finally {
//			try {
//				if(rs != null) rs.close();
//				if(stmt != null) stmt.close();
//				if(conn != null) conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		System.out.println(new Date() + " Fetched Objects");
//		objects.append("\n</SELECT>");
//		return objects.toString();
	}

	/**
	 * Get all configured alarms that are enabled
	 * 
	 * @return Map with the alarms and it's configuration values
	 * */
	public LinkedHashMap<String, CfgAlarm> getCfgAlarms(String msisdn) throws NemesisException {
		LinkedHashMap<String, CfgAlarm> alarms = new LinkedHashMap<String, CfgAlarm>();
		String query = "select * from cfg_alarms where state = 'E'";
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement stmthist = null;
		ResultSet rs = null, rshist = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			stmthist = conn.prepareStatement("select count(*) c from cfg_quickalarms " +
					"where delivery_status != 'C' and msg_destination like '%"+msisdn+"%' and msg_header like ?;");
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				stmthist.setString(1, "%"+rs.getString("view_out")+"%");
				rshist = stmthist.executeQuery();
				if(rshist.next()) if(rshist.getInt("c") > 0) 
					alarms.put(rs.getString("id"), new CfgAlarm(rs.getString("id"), rs.getString("view_out"), rs.getString("indicator"), 
							rs.getString("threshold"), rs.getString("obj_col_constraint"), rs.getString("period"), rs.getString("object")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return alarms;
	}

	/**
	 * Get the recent history for a given alarm.
	 * 
	 * @param alarmId the id of the given alarm
	 * 
	 * @return HistoryAlarm objects list that contains the recent history for the given alarmId
	 * */
	public LinkedHashMap<String, HistoryAlarm> getHistoryAlarms(String alarmId, String msisdn) throws NemesisException {
		LinkedHashMap<String, HistoryAlarm> history = new LinkedHashMap<String, HistoryAlarm>();
		String query = "select cfg_quickalarms_id history_id, msg_header alarmed_date, '' alarmed_time, msg_body alarmed_element, '' alarmed_element_value, msg_header object " +
				"from cfg_quickalarms where delivery_status != 'C' and msg_destination like '%"+msisdn+"%' and msg_header like '%"+alarmId+"%' order by max_delivery_time desc limit 50;"; 
				//"select * from cfg_alarms_history h inner join cfg_alarms a on(h.id_alarm = a.id) " +
				//"where id_alarm = "+alarmId+" order by alarmed_date desc, alarmed_time desc limit 20;";
		System.out.println(new Date() + " Alarms Query: " + query);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				history.put(rs.getString("history_id"), new HistoryAlarm(rs.getString("alarmed_date"), rs.getString("alarmed_time"), 
						rs.getString("alarmed_element").replace("\n", "</br>"), rs.getString("alarmed_element_value"), rs.getString("history_id"), rs.getString("object")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return history;
	}

	/**
	 * Save a custom chart
	 * 
	 * @param dataTable The data table
	 * @param iniDate Initial date of the chart
	 * @param endDate Finish date of the chart
	 * @param objects Comma separated object list
	 * @param kpis Comma-separated indicators
	 * @param owner The report owner
	 * */
	public String saveChart(String objectType, String tableType, String iniDate, String endDate, String objects, String kpis, String owner) throws NemesisException {
		String query = "insert into cfg_web_savedreports (rep_id, object_type, table_type, rep_owner, ini_date, end_date, objects, kpis, shared, creation_date) " +
				"values (default, '"+objectType+"', '"+tableType+"', '"+owner+"', "+iniDate+", "+endDate+", '"+objects+"', '"+kpis+"', false, default)";
		System.out.println("Saving: " + query);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * Builds the reports menu
	 * 
	 * @return Linked list with the reports menu
	 * */
	public List<ReportCategory> getReportMenu() throws NemesisException {
		List<ReportCategory> categories = new LinkedList<ReportCategory>();
		String query = "SELECT CAT_NAME, CAT_LABEL, CAT_TITLE, CAT_PARENT, CAT_CUBE FROM CFG_WEB_CATEGORIES WHERE CAT_PARENT IS NULL AND CAT_ENABLE_BL = 1 ORDER BY CAT_INDEX";
		Connection conn = null;
		PreparedStatement istmt = null;
		Statement stmt = null;
		ResultSet rs = null, rsi = null;
		try {
			conn = NemesisConnector.getConnection("NEMESIS_TRENDS");
			istmt = conn.prepareStatement("SELECT CAT_NAME, ITEM_NAME, ITEM_LABEL, ITEM_TITLE, ITEM_DIM_MDX, ITEM_DIMVALUES_MDX, ITEM_FILTER_MDX, ITEM_DRILL_LEVELS "
					+ "FROM CFG_WEB_CATEGORYITEMS WHERE CAT_NAME = ? AND ITEM_ENABLE_BL = 1 ORDER BY ITEM_INDEX");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			ReportCategory cat, subCat;
			while(rs.next()) {
				if(devMode) System.out.println(new Date() + ": Getting categories from " + rs.getString("CAT_CUBE"));
				cat = new ReportCategory(rs.getString("CAT_LABEL"), rs.getString("CAT_TITLE"), rs.getString("CAT_CUBE"), false);
				istmt.setString(1, rs.getString("CAT_NAME"));
				rsi = istmt.executeQuery();
				while(rsi.next()) {
					if(devMode) System.out.println(new Date() + ": Getting item " + rsi.getString("ITEM_LABEL") + " + " + rsi.getString("CAT_NAME") + "@@@" + rsi.getString("ITEM_NAME"));
					if(rsi.getInt("ITEM_DRILL_LEVELS") > 0) {
						subCat = new ReportCategory(rsi.getString("ITEM_LABEL"), rsi.getString("ITEM_TITLE"), 
								rsi.getString("CAT_NAME") + "@@@" + rsi.getString("ITEM_NAME"), true);
						subCat.addItems(new ReportItem("@wait", "Loading Items...", "Please wait for Items to load..."));
						cat.addSubCategories(subCat);
					} else {
						cat.addItems(new ReportItem(rs.getString("CAT_CUBE") + "@@@" + rsi.getString("ITEM_DIM_MDX") + "@@@" + rsi.getString("ITEM_DIMVALUES_MDX") + "@@@NOFILTER", 
								rsi.getString("ITEM_LABEL"), rsi.getString("ITEM_TITLE")));
					}
				}
				categories.add(cat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(rs != null) rs.close();
				if(rsi != null) rsi.close();
				if(stmt != null) stmt.close();
				if(istmt != null) istmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return categories;
	}

	public ReportCategory getReactiveCategory(ReportCategory category) throws NemesisException {
		if(devMode) System.out.println(new Date() + ": Loading MDX reactive category: " + category.getCode());
		String query = "SELECT ITEM_DIM_MDX, ITEM_DIMVALUES_MDX, CAT_CUBE, ITEM_LABEL, ITEM_FILTER_MDX, ITEM_FILTERVALUES_MDX "
				+ "FROM CFG_WEB_CATEGORIES NATURAL JOIN CFG_WEB_CATEGORYITEMS "
				+ "WHERE CAT_NAME = '" + category.getCode().split("@@@")[0] + "' AND ITEM_NAME = '" + category.getCode().split("@@@")[1] + "' AND CAT_ENABLE_BL = 1 ORDER BY CAT_INDEX";
		if(devMode) System.out.println(new Date() + ": Querying: " + query);
		category.getItems().clear();
		Connection conn = null, oconn = null;
		Statement stmt = null;
		OlapStatement os = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection("NEMESIS_TRENDS");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				String mdx = "WITH MEMBER [Measures].[UniqueName] AS " + rs.getString("ITEM_FILTER_MDX") + ".CURRENTMEMBER.UNIQUENAME "
						+ "SELECT [Measures].[UniqueName] ON 0, "
						+ rs.getString("ITEM_FILTER_MDX") + "." + rs.getString("ITEM_FILTERVALUES_MDX") + " ON 1 "
						+ "FROM [" + rs.getString("CAT_CUBE") + "] ";
				if(devMode) System.out.println(new Date() + " Getting OLAP category " + rs.getString("CAT_CUBE") + " - " + rs.getString("ITEM_FILTER_MDX"));
				oconn = NemesisOLAP.getConnection();
				OlapConnection oc = oconn.unwrap(OlapConnection.class);
				os = oc.createStatement();
				if(devMode) System.out.println(new Date() + " Getting Reactive Categories: " + mdx);
				CellSet results = os.executeOlapQuery(mdx);
				Position axis0 = results.getAxes().get(0).getPositions().get(0);
				for (Position axis1 : results.getAxes().get(1).getPositions()) {
					if(devMode) System.out.println(new Date() + " New Item: " + axis1.getMembers().get(0).getCaption() + " - " + results.getCell(axis0, axis1).getValue().toString());
					category.addItems(new ReportItem(rs.getString("CAT_CUBE") + "@@@" + rs.getString("ITEM_DIM_MDX") + "@@@" + 
							rs.getString("ITEM_DIMVALUES_MDX") + "@@@" + rs.getString("ITEM_FILTER_MDX") + ".[" + axis1.getMembers().get(0).getCaption() + "]", 
							axis1.getMembers().get(0).getCaption(), axis1.getMembers().get(0).getDescription()));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(conn != null) conn.close();
                if(stmt != null) stmt.close();
                if(os != null) os.close();
				if(oconn != null) oconn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return category;
	}

	/**
	 * Recursive building of menu categories
	 * 
	 * @param catName The category name
	 * @param catLabel The category label
	 * @param catTitme The category title
	 * @param catParent The category parent
	 * @return The category with it's subcategories and items
	 * @throws SQLException 
	 * */
	public ReportCategory buildCategory(String catName, String catLabel, String catTitle, String catParent, PreparedStatement pstmt, PreparedStatement istmt) throws NemesisException, SQLException {
		ReportCategory category = new ReportCategory(catLabel, catTitle);
		//String query = "select cat_name, cat_label, cat_title, cat_parent from cfg_web_categories where cat_parent = '"+catName+"' order by cat_index";
		ResultSet rs = null, rsi = null;
		Vector<String[]> data = new Vector<String[]>();
		Vector<String[]> items = new Vector<String[]>();
		try {
			pstmt.setString(1, catName);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				//category.addSubCategories(buildCategory(rs.getString("cat_name"), rs.getString("cat_label"), rs.getString("cat_title"), rs.getString("cat_parent"), pstmt, istmt));
				data.add(new String[] {rs.getString("cat_name"), rs.getString("cat_label"), rs.getString("cat_name"), rs.getString("cat_name")});
			}
			rs.close();
			istmt.setString(1, catName);
			rsi = istmt.executeQuery();
			while(rsi.next()) {
				//category.addItems(new ReportItem(rsi.getString("object_type"), rsi.getString("item_label"), rsi.getString("item_title")));
				items.add(new String[] {rsi.getString("object_type"), rsi.getString("item_label"), rsi.getString("item_title")});
			}
			rsi.close();
			for (String[] d : data) {
				category.addSubCategories(buildCategory(d[0], d[1], d[2], d[3], pstmt, istmt));
			}
			for (String[] d : items) {
				category.addItems(new ReportItem(d[0], d[1], d[2]));
			}
			//query = "select object_type, item_label, item_title from cfg_web_categoryitems where cat_name = '"+catName+"' order by item_index";
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		}/* finally {
			if(rs != null) {
				rs.close();
			}
		}*/
		return category;
	}

	public List<SavedReportBean> getSavedReports(UserBean user) throws NemesisException {
		List<SavedReportBean> savedReports = new LinkedList<SavedReportBean>();
		String query = "select item_label, type_description, rep_owner, object_type, table_type, ini_date, end_date, objects, " +
				" kpis, creation_date, date_add(ini_date, interval datediff(current_date, end_date) day) ini_fix_date, current_date end_fix_date " +
				"from cfg_web_savedreports inner join cfg_web_datatables using(object_type, table_type) " +
				"inner join cfg_web_categoryitems using(object_type) inner join cfg_web_tabletypes using(table_type) " +
				"where rep_owner = '"+user.getUserLogin()+"' and status = 'Active' order by creation_date desc limit 20;"; 
		System.out.println(new Date() + " Saved Reports Query: " + query);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				savedReports.add(new SavedReportBean(rs.getString("item_label"), rs.getString("type_description"), 
						rs.getString("rep_owner"), rs.getString("object_type"), rs.getString("table_type"), rs.getString("ini_date"), 
						rs.getString("end_date"), rs.getString("objects"), rs.getString("kpis"), rs.getString("creation_date"), 
						rs.getString("ini_fix_date"), rs.getString("end_fix_date"))); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		QueryUtils.registerUserAction(user.getUserLogin(), "Table List", "getSavedReports", "Java Map", "Saved Reports List", Integer.toString(savedReports.size()));
		return savedReports;
	}

	public String[][] getChartData(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user) throws NemesisException {
		ChartDataElement dataElement = getChartElement(objectType, iniDate, endDate, kpis, objects, tableType, user);
		String[][] result = dataElement.buildChartArrayData();
//		System.out.println(new Date() + " Chart Data (Array) Built: " + dataElement);
		return result;
	}

	public String getChartJson(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user) throws NemesisException {
		return getChartJson(objectType, iniDate, endDate, kpis, objects, tableType, false, user);
	}

	public String getChartJson(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, boolean toAppend, UserBean user) throws NemesisException {
		ChartDataElement dataElement = getChartElement(objectType, iniDate, endDate, kpis, objects, tableType, user);
		String result = "";
		if(toAppend) {
			result = dataElement.buildChartJsonDataToAppend();
//			System.out.println(new Date() + " Json to Append Built: " + result);
		} else {
			result = dataElement.buildChartJsonData();
//			System.out.println(new Date() + " Json for new Report Built: " + result);
		}
		return result;
	}
    public String getChartJson( String objectType, String dates, String kpis, String objects, String tableType, boolean b, UserBean user ) throws NemesisException {
        ChartDataElement dataElement = getChartElement(objectType, dates, kpis, objects, tableType, user);
        //System.out.println(dataElement.buildChartJsonDataComparative( ));
        return dataElement.buildChartJsonDataComparative( );
    }

    private ChartDataElement getChartElement( String objectType, String dates, String kpis, String objects, String tableType, UserBean user ) throws NemesisException {
        ChartDataElement dataElement = new ChartDataElement();
        if(objects.startsWith("!")) objects = objects.substring(1);
        if(objects.endsWith("!")) objects = objects.substring(0, objects.length() - 1);
        String[] objectArray = objects.split("!");
        String[] kpiArray = kpis.split(",");
        String mdxObjects = "";
        String mdxDates = "";
        String mdxKpis = "";
        int count = 1;
        for(String kpi : kpiArray) {
        	mdxKpis += ", [Indicator].[Indicator Agg].&[" + kpi + "]";
            for (String object : objectArray) {
                dataElement.getMappedTimeLines().add(kpi + "_" + object);
                count++;
            }
        }
        mdxKpis = mdxKpis.substring(2);
        for(String date : dates.split(",")) {
            mdxDates += ", [Date].[Date].&[" + date + "T00:00:00]";
        }
        mdxDates = mdxKpis.substring(2);
		
        if(devMode) System.out.println(new Date() + " Mapped " + count + " Objects: " + dataElement.getMappedTimeLines());
        count = 1;
        for (String object : objectArray) {
            mdxObjects += ", " + objectType + ".[" + object + "]";
            objectsCount.put(object, 0);
            count++;
        }
        mdxObjects = mdxObjects.substring(2);
		OlapConnection oc = null;
        OlapStatement stmt = null;
        ChartTimeLine chartTimeLine = null;
		Connection con = null;
		try {
			con = NemesisOLAP.getConnection();
			oc = con.unwrap(OlapConnection.class);
			stmt = oc.createStatement();

			String query = "SELECT "
    				+ "{ " + mdxDates + " } ON 0, " // Columns: Date - Time
    				+ "{ " + mdxObjects + " } ON 1, " // Rows: Visible element
    				+ "{ " + mdxKpis + " } ON 2, " // Pages: Indicators
    				+ "{ [Cell].[Ran Control].[All] } ON 3 " // Chapters: granularity
    				+ "FROM [Ran] "
    				+ "WHERE ( [Measures].[Value] )"; // Measurements

			if(devMode) System.out.println(new Date() + " Data Query: \n" + query);
    		CellSet results = stmt.executeOlapQuery(query);

    		if(devMode) System.out.println(new Date() + " Fetching ResultSet");
    		Cell currentCell;
    		for (Position granularityAxis : results.getAxes().get(3).getPositions()) { // Chapters: granularity
    			if(devMode) System.out.println("New Chapter: " + granularityAxis.getMembers().get(0).getCaption());
    			for (Position indicatorsAxis : results.getAxes().get(2).getPositions()) { // Pages: Indicators
    				if(devMode) System.out.println("\tNew Page: " + indicatorsAxis.getMembers().get(0).getCaption());
    				for (Position elementsAxis : results.getAxes().get(1).getPositions()) { // Rows: Elements
    					if(devMode) System.out.print("\t\t" + elementsAxis.getMembers().get(0).getCaption() + ":\t");
    					for (Position datesAxis : results.getAxes().get(0).getPositions()) { // Columns: Dates
    						chartTimeLine = new ChartTimeLine();
    						currentCell = results.getCell(datesAxis, elementsAxis, indicatorsAxis, granularityAxis);
    						Object value = currentCell.getValue();
                            chartTimeLine.setLineName(indicatorsAxis.getMembers().get(0).getCaption() + "_" + elementsAxis.getMembers().get(0).getCaption());
                            if(value != null) chartTimeLine.setValue((Double)value);
                            dataElement.put(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datesAxis.getMembers().get(0).getCaption()), chartTimeLine);
                            if(devMode) System.out.print(value == null ? "null" : value.toString());
    						if(devMode) System.out.print("\t");
    					}
    					if(devMode) System.out.println();
    				}
    			}
    		}
    		if(devMode) System.out.println(new Date() + " Full Map");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("CAUSE: " + e.getCause());
            throw new NemesisException(e.getMessage().contains("Unknown column") ? 
                            "One of the indicators is wrong, please review the given names" : 
                            e.getMessage().contains("Table") ? "Wrong Type, please select a different one" : e.getMessage());
        } catch (ParseException e) {
        	e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the parsing of dates, please report the following text [" + e.getMessage() + "]");
		} finally {
            try {
                if(stmt != null) stmt.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryUtils.registerUserAction(user.getUserLogin(), "Graph Data", "getChartElement", "Ajax Data", kpis, Integer.toString(dataElement.toString().length()));
        return dataElement;
		

//        ChartDataElement dataElement = new ChartDataElement();
//        String groupClause = "group by 1, 3, 4";
//        boolean grouping = false;
//        if(objects.startsWith("GROUP:") || kpis.toLowerCase( ).contains( "sum(" ) || kpis.toLowerCase( ).contains( "avg(" )|| kpis.toLowerCase( ).contains( "max(" )|| kpis.toLowerCase( ).contains( "min(" ) ) {
//            //objects = objects.substring(6);
//            groupClause = tableType.equals("det") ? "group by 3, 4" : "group by 3";
//            grouping = true;
//        }
//        if(objects.startsWith("!")) objects = objects.substring(1);
//        if(objects.endsWith("!")) objects = objects.substring(0, objects.length() - 1);
//        // Defining variables
//        String dataIdField = "id";
//        String objectIdField = "id";
//        String[] objectArray = objects.split("!");
//        String[] kpiArray = kpis.split(",");
//        String objectTable = "nms_" + objectType;
//        String nameField = objectType;
//        String nameAlias = objectType;
//        String dataTable = objectTable + "_" + tableType;
//
//        // Parsing Object Ids to fit into query
//        String objectIds = "";
//        int count = 1;
//        for(String kpi : kpiArray) {
//            if(grouping) {
//                dataElement.getMappedTimeLines().add(kpi + "_" + objects.replace("!", "_"));
//                count++;
//            } else {
//                for (String object : objectArray) {
//                    dataElement.getMappedTimeLines().add(kpi + "_" + object);
//                    count++;
//                }
//            }
//        }
//        System.out.println(new Date() + " Mapped " + count + " Objects: " + dataElement.getMappedTimeLines());
//        count = 1;
//        if(grouping) {
//            objectsCount.put(objects.replace("!", "_"), 0);
//            for (String object : objectArray) {
//                objectIds += ", '" + object + "'";
//            }
//        } else {
//            for (String object : objectArray) {
//                objectIds += ", '" + object + "'";
//                objectsCount.put(object, 0);
//                count++;
//            }
//        }
//        objectIds = objectIds.substring(2);
//        Connection conn = null;
//        Statement stmt = null;
//        PreparedStatement psannot = null;
//        ResultSet rs = null, rsannot = null, rsattr = null;
//        ChartTimeLine chartTimeLine = null;
//        try {
//            conn = NemesisConnector.getConnection();
//            stmt = conn.createStatement();
//            psannot = conn.prepareStatement((tableType.startsWith("bh") || tableType.startsWith("dt")) ? 
//                    "select annot_head, annot_body from log_annotations " +
//                    "where annot_date = ? and annot_indicator = ? and annot_element_type = ? " +
//                    "and annot_element_name = ?;" : 
//                    "select annot_head, annot_body from log_annotations " +
//                    "where annot_date = ? and annot_time between subtime(?, '00:59:59') and ? and annot_indicator = ? and annot_element_type = ? " +
//                    "and annot_element_name = ?;");
//            rsattr = stmt.executeQuery(QueryUtils.getReportParametersQuery(objectType, tableType));
//            if(rsattr.next()) {
//                dataTable = rsattr.getString("data_table");
//                objectTable = rsattr.getString("object_table");
//                objectIdField = rsattr.getString("object_id_field");
//                dataIdField = rsattr.getString("data_id_field");
//                nameField = rsattr.getString("name_field");
//                nameAlias = rsattr.getString("name_alias");
//            }
//            // Build query to get objects ids
//            String query = QueryUtils.getObjectsQuery(objectIdField, objectTable, nameField, objectIds);
//            System.out.println(new Date() + " Objects Query: " + query);
//            String objectTableIds = "";
//            rs = stmt.executeQuery(query);
//            if(rs.next()) {
//                objectTableIds = rs.getString("ids");
//            } else {
//                return dataElement;
//            }
//
//            // Set the select statement right for the case when there's need to group data
//            String toQuery = grouping ? "'"+objects.replace("!", "_")+"'" : nameAlias;
//            nameField = grouping ? toQuery: nameField;
//
//            // Build query to get chart data crossing information with primary key between object and data tables
//            query = QueryUtils.getReportGroupingQuery(dataIdField, nameField, toQuery, kpis, objectTable, dataTable, objectIdField, dates, objectTableIds, groupClause);
//
//            System.out.println(new Date() + " Data Query: " + query);
//            if(rs != null) rs.close();
//            rs = stmt.executeQuery(query);
//            System.out.println(new Date() + " Fetching ResultSet");
//            String obj, time, dateTime;
//            while (rs.next()) {
//                try {
//                    obj = rs.getString(2);
//                    time = rs.getString("x_time");
//                    dateTime = rs.getString("x_date") + " " + (time == null ? "23:59:59" : time);
//                    for (String kpi : kpiArray) {
//                        chartTimeLine = new ChartTimeLine();
//                        if(tableType.startsWith("bh") || tableType.startsWith("dt")) {
//                            psannot.setString(1, rs.getString("x_date"));
//                            psannot.setString(2, "*");//kpi
//                            psannot.setString(3, objectType);
//                            psannot.setString(4, obj);
//                        } else {
//                            psannot.setString(1, rs.getString("x_date"));
//                            psannot.setString(2, rs.getString("x_time"));
//                            psannot.setString(3, rs.getString("x_time"));
//                            psannot.setString(4, "*");//kpi
//                            psannot.setString(5, objectType);
//                            psannot.setString(6, obj);
//                        }
//                        chartTimeLine.setLineName(kpi + "_" + obj);
//                        chartTimeLine.setValue(rs.getDouble(kpi));
//                        rsannot = psannot.executeQuery();
//                        if(rsannot.next()) {
//                            chartTimeLine.setAnnotTitle(rsannot.getString(1));
//                            chartTimeLine.setAnnotBody(rsannot.getString(2));
//                        }
//                        psannot.clearParameters();
//                        dataElement.put(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime), chartTimeLine);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println(new Date() + " Full Map");
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("ERROR: " + e.getMessage());
//            System.out.println("CAUSE: " + e.getCause());
//            throw new NemesisException(e.getMessage().contains("Unknown column") ? 
//                            "One of the indicators is wrong, please review the given names" : 
//                            e.getMessage().contains("Table") ? "Wrong Type, please select a different one" : e.getMessage());
//        } finally {
//            try {
//                if(rs != null) rs.close();
//                if(rsattr != null) rsattr.close();
//                if(psannot != null) psannot.close();
//                if(stmt != null) stmt.close();
//                if(conn != null) conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        QueryUtils.registerUserAction(user.getUserLogin(), "Graph Data", "getChartElement", "Ajax Data", kpis, Integer.toString(dataElement.toString().length()));
//        return dataElement;
    }

    /**
	 * Fetches the chart data to send to the client application's AnnotatedTimeLine Chart.
	 * Delivers JSON code to fill the JsonDataSource proprietary implementation of Visualization API DataSource.
	 * Queries the NEMESIS OLAP Cube to fetch the information.
	 * 
	 * @param objectType The object type
	 * @param iniDate Chart's initial date
	 * @param endDate Chart's end date
	 * @param kpis Indicators list
	 * @param objects Object's list
	 * @param tableType The table type
	 * */
	/* (non-Javadoc)
	 * @see org.colombiamovil.smartkpi.client.NemesisService#getChartJson(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ChartDataElement getChartElement(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user) throws NemesisException {

        ChartDataElement dataElement = new ChartDataElement();
		String[] objectParts = objectType.split("@@@");
		String cubeName = objectParts[0], 
				dimension = objectParts[1];
        if(objects.startsWith("!")) objects = objects.substring(1);
        if(objects.endsWith("!")) objects = objects.substring(0, objects.length() - 1);
        boolean detail = tableType.equals("Detail");
        String[] objectArray = objects.split("!");
        String[] kpiArray = kpis.split(",");
        String mdxObjects = "";
        String mdxDates = iniDate.equals(endDate) ? "[Date].[Date].&[" + iniDate + "T00:00:00]" : 
        	"[Date].[Date].&[" + iniDate + "T00:00:00] : [Date].[Date].&[" + endDate + "T00:00:00]";
        String mdxKpis = "";
        String mdxAgg = !detail ? "[Aggregation].[Type Agg].[" + tableType + "]" : "[Hour].[Hour].Children";
        String cube = detail ? "[RanDetailed]" : "[Ran]";
        int count = 1;
        for(String kpi : kpiArray) {
        	mdxKpis += ", [Indicator].[Indicator Agg].&[" + kpi + "]";
            for (String object : objectArray) {
                dataElement.getMappedTimeLines().add(kpi + "_" + object);
                count++;
            }
        }
        mdxKpis = mdxKpis.substring(2);

        if(devMode) System.out.println(new Date() + " Mapped " + count + " Objects: " + dataElement.getMappedTimeLines());
        count = 1;
        for (String object : objectArray) {
            mdxObjects += ", " + dimension + ".&[" + object + "]";
            objectsCount.put(object, 0);
            count++;
        }
        mdxObjects = mdxObjects.substring(2);
		OlapConnection oc = null;
        OlapStatement stmt = null;
        ChartTimeLine chartTimeLine = null;
		Connection con = null;
		try {
			con = NemesisOLAP.getConnection();
			oc = con.unwrap(OlapConnection.class);
			stmt = oc.createStatement();

			String query = "SELECT [Measures].[Cell Value] ON 0, "
    				+ "NONEMPTY( CrossJoin ( "
    				+ "{ " + mdxKpis + " }, "
    				+ "{ " + mdxObjects + " }, "
    				+ "{ " + mdxDates + " }, "
    				+ "{ " + mdxAgg + " } ), [Measures].[Cell Value] ) ON 1 "
    				+ "FROM " + cubeName;
//			System.out.println("Real query:");
//			System.out.println(query);
//
//			query = "SELECT "
//    				+ "{ [Date].[Date].&[2014-11-22T00:00:00] : [Date].[Date].&[2014-11-24T00:00:00] } ON 0, " // Columns: Date - Time
//    				+ "{ [Geography].[Territory].[All], [Geography].[Territory].[COSTA], [Geography].[Territory].[NOROCCIDENTE] } ON 1, " // Rows: Visible element
//    				+ "{ [Indicator].[Indicator Agg].&[Total Calls], [Indicator].[Indicator Agg].&[Drop Calls - Total] } ON 2, " // Pages: Indicators
//    				+ "{ [Cell].[Ran Control].[All] } ON 3 " // Chapters: granularity
//    				+ "FROM [Ran] "
//    				+ "WHERE ( [Measures].[Value] )"; // Measurements

			if(devMode) System.out.println(new Date() + " Data Query: \n" + query);
    		CellSet results = stmt.executeOlapQuery(query);

    		if(devMode) System.out.println(new Date() + " Fetching ResultSet");
    		Cell currentCell;
//    		String hour;
    		for (Position indicatorAxis : results.getAxes().get(0).getPositions()) { // Columns: the value
    			if(devMode) System.out.println("Value: " + indicatorAxis.getMembers().get(0).getCaption());
    			for (Position dimensionsAxis : results.getAxes().get(1).getPositions()) { // Rows: the dimensions
    				if(devMode) System.out.println("\tNew Row: " + dimensionsAxis.getMembers().get(0).getCaption() + " - " +
    						dimensionsAxis.getMembers().get(1).getCaption() + " - " +
    						dimensionsAxis.getMembers().get(2).getCaption() + " - " +
    						dimensionsAxis.getMembers().get(3).getCaption());
					chartTimeLine = new ChartTimeLine();
					currentCell = results.getCell(indicatorAxis, dimensionsAxis);
					Object value = currentCell.getValue();
                    chartTimeLine.setLineName(dimensionsAxis.getMembers().get(0).getCaption() + "_" + dimensionsAxis.getMembers().get(1).getCaption()); // Kpis / Objects
                    if(value != null) chartTimeLine.setValue((Double)value);
//                    hour = detail ? dimensionsAxis.getMembers().get(3).getCaption() : " 00:00:";
                    dataElement.put(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dimensionsAxis.getMembers().get(2).getCaption()),chartTimeLine);
                    if(devMode) System.out.print(value == null ? "null" : value.toString());
                    if(devMode) System.out.print("\t");
    			}
    		}
    		if(devMode) System.out.println(new Date() + " Full Map");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("CAUSE: " + e.getCause());
            throw new NemesisException(e.getMessage().contains("Unknown column") ? 
                            "One of the indicators is wrong, please review the given names" : 
                            e.getMessage().contains("Table") ? "Wrong Type, please select a different one" : e.getMessage());
        } catch (ParseException e) {
        	e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the parsing of dates, please report the following text [" + e.getMessage() + "]");
		} finally {
            try {
                if(stmt != null) stmt.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryUtils.registerUserAction(user.getUserLogin(), "Graph Data", "getChartElement", "Ajax Data", kpis, Integer.toString(dataElement.toString().length()));
        return dataElement;

//        System.out.println("ObjectType("+objectType+")");		
//		System.out.println("endDate("+endDate+")");
//		System.out.println("kpis("+kpis+")");
//		System.out.println("objects("+objects+")");
//		System.out.println("tableType("+tableType+")");
//		
//		ChartDataElement dataElement = new ChartDataElement();
//		//String groupClause = "group by 1, 3, 4";
//		String groupClause = "";
//		boolean grouping = false;
//		if(objects.startsWith("GROUP:") || kpis.toLowerCase( ).contains( "sum(" ) || kpis.toLowerCase( ).contains( "avg(" )|| kpis.toLowerCase( ).contains( "max(" )|| kpis.toLowerCase( ).contains( "min(" ) ) {
//			//objects = objects.substring(6);
//			groupClause = tableType.equals("det") ? "group by 3, 4" : "group by 3";
//			grouping = true;
//		}
//		if(objects.startsWith("!")) objects = objects.substring(1);
//		if(objects.endsWith("!")) objects = objects.substring(0, objects.length() - 1);
//		// Defining variables
//		String inidate = "date_sub(current_date, interval 1 week)", enddate = "date_sub(current_date, interval 1 day)";
//		inidate = iniDate != null ? iniDate : inidate;
//		enddate = endDate != null ? endDate : enddate;
//		String dataIdField = "id";
//		String objectIdField = "id";
//		String[] objectArray = objects.split("!");
//		String[] kpiArray = kpis.split(",");
//		String objectTable = "nms_" + objectType;
//		String nameField = objectType;
//		String nameAlias = objectType;
//		String dataTable = objectTable + "_" + tableType;
//
//		// Parsing Object Ids to fit into query
//		String objectIds = "";
//		int count = 1;
//		for(String kpi : kpiArray) {
//			if(grouping) {
//				dataElement.getMappedTimeLines().add(kpi + "_" + objects.replace("!", "_"));
//				count++;
//			} else {
//				for (String object : objectArray) {
//					dataElement.getMappedTimeLines().add(kpi + "_" + object);
//					count++;
//				}
//			}
//		}
//		System.out.println(new Date() + " Mapped " + count + " Objects: " + dataElement.getMappedTimeLines());
//		count = 1;
//		if(grouping) {
//			objectsCount.put(objects.replace("!", "_"), 0);
//			for (String object : objectArray) {
//				objectIds += ", '" + object + "'";
//			}
//		} else {
//			for (String object : objectArray) {
//				objectIds += ", '" + object + "'";
//				objectsCount.put(object, 0);
//				count++;
//			}
//		}
//		objectIds = objectIds.substring(2);
//		Connection conn = null;
//		Statement stmt = null;
//		PreparedStatement psannot = null;
//		ResultSet rs = null, rsannot = null, rsattr = null;
//		ChartTimeLine chartTimeLine = null;
//		try {
//			conn = NemesisConnector.getConnection();
//			stmt = conn.createStatement();
//			psannot = conn.prepareStatement((tableType.startsWith("bh") || tableType.startsWith("dt")) ? 
//					"select annot_head, annot_body from log_annotations " +
//					"where annot_date = ? and annot_indicator = ? and annot_element_type = ? " +
//					"and (annot_element_name = ? or element_id = ?);" : 
//					"select annot_head, annot_body from log_annotations " +
//					"where annot_date = ? and annot_time between subtime(?, '00:59:59') and ? and annot_indicator = ? and annot_element_type = ? " +
//					"and (annot_element_name = ? or element_id = ?);");
//			rsattr = stmt.executeQuery(QueryUtils.getReportParametersQuery(objectType, tableType));
//			if(rsattr.next()) {
//				dataTable = rsattr.getString("data_table");
//				objectTable = rsattr.getString("object_table");
//				objectIdField = rsattr.getString("object_id_field");
//				dataIdField = rsattr.getString("data_id_field");
//				nameField = rsattr.getString("name_field");
//				nameAlias = rsattr.getString("name_alias");
//			}
//			// Build query to get objects ids
//			String query = QueryUtils.getObjectsQuery(objectIdField, objectTable, nameField, objectIds);
//			System.out.println(new Date() + " Objects Query: " + query);
//			String objectTableIds = "";
//			rs = stmt.executeQuery(query);
//			if(rs.next()) {
//				objectTableIds = rs.getString("ids");
//			} else {
//				return dataElement;
//			}
//
//			// Set the select statement right for the case when there's need to group data
//			String toQuery = grouping ? "'"+objects.replace("!", "_")+"'" : nameAlias;
//			nameField = grouping ? toQuery: nameField;
//			// Build query to get chart data crossing information with primary key between object and data tables
//			query = QueryUtils.getReportGroupingQuery(dataIdField, nameField, toQuery, kpis, objectTable, dataTable, objectIdField, inidate, enddate, objectTableIds, groupClause);
//
//			System.out.println(new Date() + " Data Query: " + query);
//			if(rs != null) rs.close();
//			rs = stmt.executeQuery(query);
//			System.out.println(new Date() + " Fetching ResultSet");
//			String id,obj, time, dateTime;
//			while (rs.next()) {
//				try {
//				    id = rs.getString(1); 
//					obj = rs.getString(2);
//					time = rs.getString("x_time");
//					dateTime = rs.getString("x_date") + " " + (time == null ? "23:59:59" : time);
//					for (String kpi : kpiArray) {
//						chartTimeLine = new ChartTimeLine();
//						if(tableType.startsWith("bh") || tableType.startsWith("dt")) {
//							psannot.setString(1, rs.getString("x_date"));
//							psannot.setString(2, "*");//kpi
//							psannot.setString(3, objectType);
//							psannot.setString(4, obj);
//							psannot.setString(5, id);
//						} else {
//							psannot.setString(1, rs.getString("x_date"));
//							psannot.setString(2, rs.getString("x_time"));
//							psannot.setString(3, rs.getString("x_time"));
//							psannot.setString(4, "*");//kpi
//							psannot.setString(5, objectType);
//							psannot.setString(6, obj);
//							psannot.setString(7, id);
//						}
//						chartTimeLine.setLineName(kpi + "_" + obj);
//						chartTimeLine.setValue(rs.getDouble(kpi));
//						rsannot = psannot.executeQuery();
//						if(rsannot.next()) {
//							chartTimeLine.setAnnotTitle(rsannot.getString(1));
//							chartTimeLine.setAnnotBody(rsannot.getString(2));
//						}
//						psannot.clearParameters();
//						dataElement.put(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime), chartTimeLine);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			System.out.println(new Date() + " Full Map");
//		} catch (SQLException e) {
//			e.printStackTrace();
//			System.out.println("ERROR: " + e.getMessage());
//			System.out.println("CAUSE: " + e.getCause());
//			throw new NemesisException(e.getMessage().contains("Unknown column") ? 
//							"One of the indicators is wrong, please review the given names" : 
//							e.getMessage().contains("Table") ? "Wrong Type, please select a different one" : e.getMessage());
//		} finally {
//			try {
//				if(rs != null) rs.close();
//				if(rsattr != null) rsattr.close();
//				if(psannot != null) psannot.close();
//				if(stmt != null) stmt.close();
//				if(conn != null) conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		QueryUtils.registerUserAction(user.getUserLogin(), "Graph Data", "getChartElement", "Ajax Data", kpis, Integer.toString(dataElement.toString().length()));
//		return dataElement;
	}
	/**
	 * Downloads menu services
	 * */
	public List<String> getExportableDbs(String tableType) throws NemesisException {
		List<String> dbs = new LinkedList<String>();
		String query = "select distinct db_name from cfg_web_exportables where table_type = '"+tableType+"'";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				dbs.add(rs.getString("db_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dbs;
	}

	public List<String> getExportableTables(String db) throws NemesisException {
		List<String> tables = new LinkedList<String>();
		String query = "select distinct table_name from cfg_web_exportables where db_name = '"+db+"'";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				tables.add(rs.getString("table_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tables;
	}

	public Map<String, String[]> reportExport(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user) throws NemesisException {
		String fileName = DataExporterFileManager.getInstance().exportReport(objectType, iniDate, endDate, objects, kpis, tableType, user);
		QueryUtils.registerUserAction(user.getUserLogin(), "Data Export", "reportExport", "TSV File", fileName, Integer.toString(fileName.length()));
		return DataExporterFileManager.getInstance().getQueuedJobs(user);
	}

	public Map<String, String[]> getFilejobs(UserBean user) throws NemesisException {
		Map<String, String[]> answer = DataExporterFileManager.getInstance().getQueuedJobs(user);
		QueryUtils.registerUserAction(user.getUserLogin(), "Table List", "getFilejobs", "Java Map", "Java Map containing all file jobs for a user", Integer.toString(answer.toString().length()));
		return answer;
	}


	public List<CorrelationBean> getCorrelationTable(String xObjectType, String yObjectType, String tableType, 
			String groupBy, String yObject, String iniDate, String endDate, String kpi, String xFilter, String yFilter) throws NemesisException {
		Map<String, String> px = QueryUtils.getReportParametersData(xObjectType, tableType);
		Map<String, String> py = QueryUtils.getReportParametersData(yObjectType, tableType);
		List<CorrelationBean> result = new ArrayList<CorrelationBean>();
		if(groupBy.equals("-1")) groupBy = px.get("nameField");
		String query = "SELECT lobj, robj, (COUNT(*)*SUM(XY) - SUM(X)*SUM(Y)) / (SQRT(COUNT(*)*SUM(X2)-POW(SUM(X), 2)) * SQRT(COUNT(*)*SUM(Y2)-POW(SUM(Y), 2))) correlation_ratio, " +
				" SUM(X)/SUM(Y) * 100 weight, COUNT(*), SUM(XY) sumXY, SUM(X) sumX, SUM(Y) sumY, SUM(X2) sumX2, POW(SUM(X), 2), SUM(Y2) sumY2, POW(SUM(Y), 2) FROM (" +
				"\nSELECT x_date, x_time, l.obj lobj, r.obj robj, l.ind X, r.ind Y, l.ind * r.ind XY, l.powind X2, r.powind Y2 FROM (" +
				"\n SELECT x_date, x_time, "+groupBy+" obj, "+kpi+" ind, POW("+kpi+", 2) powind " +
				"\n FROM "+px.get("objectTable")+" ot INNER JOIN "+px.get("dataTable")+" dt ON(ot."+px.get("objectIdField")+" = dt."+px.get("dataIdField")+") " +
				"\n WHERE x_date BETWEEN "+iniDate+" AND "+iniDate+" AND "+xFilter+" = '"+yObject+"' GROUP BY 1, 2, 3) l " +
				"\nINNER JOIN (" +
				"\n SELECT x_date, x_time, \""+yObject+"\" obj, "+kpi+" ind, POW("+kpi+", 2) powind " +
				"\n FROM "+py.get("objectTable")+" ot INNER JOIN "+py.get("dataTable")+" dt ON(ot."+py.get("objectIdField")+" = dt."+py.get("dataIdField")+") " +
				"\n WHERE x_date BETWEEN "+iniDate+" AND "+iniDate+" AND "+yFilter+" = '"+yObject+"' GROUP BY 1, 2, 3) r USING(x_date, x_time)) result " +
				"\nGROUP BY lobj ORDER BY weight DESC";
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				// correlationRatio, weight, sumX, sumY, sumXY, sumX2, sumY2
				result.add(new CorrelationBean(rs.getString("lobj"), rs.getString("robj"), rs.getDouble("correlation_ratio"), rs.getDouble("weight"), 
						rs.getDouble("sumX"), rs.getDouble("sumY"), rs.getDouble("sumXY"), rs.getDouble("sumX2"), rs.getDouble("sumY2")));
			}
			//System.out.println(result);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
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

	public List<ControllerConfigBean> getControllerConfig(int index, UserBean user) throws NemesisException {
		List<ControllerConfigBean> result = new ArrayList<ControllerConfigBean>();
		String query = "select * from cfg_web_controllerconfig " +
				"where conf_index = CASE WHEN "+index+" = 999999 THEN (select max(conf_index) from cfg_web_controllerconfig) " +
						"WHEN "+index+" > (SELECT MAX(conf_index) FROM cfg_web_controllerconfig) THEN 1 ELSE "+index+" END";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				result.add(new ControllerConfigBean(rs.getInt("conf_index"), rs.getInt("conf_order"), rs.getString("conf_header"), 
						rs.getString("object_type"), rs.getString("ini_date"), rs.getString("end_date"), rs.getString("kpis"),
						rs.getString("objects"), rs.getString("table_type")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("CAUSE: " + e.getCause());
			throw new NemesisException("There's a problem with the database, please report the following text [" + e.getMessage() + "]");
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


	public static void main(String[] args) throws NemesisException {

    	try {
			Date res = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2010-04-19 18:00:00-05:00");
			System.out.println(res);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/*
		ActiveDirectoryAuthenticator auth = new ActiveDirectoryAuthenticator();
		//auth.setUserDn("CN=Dahyr.Vergara,DC=COLOMBIAMOVIL,DC=BOGDVERGARA01");
		auth.setUserDn("COLOMBIAMOVIL\\Dahyr.Vergara");
		String answer = auth.authenticate("Dayhr2009");
		if(!answer.equals("OK")) {
			System.out.println(answer);
		} else {
			System.out.println("OK");
		}*/
//		System.out.println(new NemesisServiceImpl().getReportMenu());
		
		//System.out.println(sdfAdf.format(new Date()));
//		System.out.println(new NemesisServiceImpl().getChartJson("route", "20091015", "20091015", "carried_traffic", "CBSH02 (MSSBAR),DBSH03 (MSSMED)", "det", true, null));
//		new NemesisServiceImpl().getCorrelationTable("route", "route", "det", "traffic_type", "MSSBOG2", "20100114", "20100114", "SUM(carried_calls_with_answer)/SUM(carried_calls)", "msc", "msc");
//		new NemesisServiceImpl().getCorrelationTable("route", "route", "det", "-1", "BICC", "20100114", "20100114", "SUM(carried_calls_with_answer)/SUM(carried_calls)", "traffic_type", "traffic_type");
	}

    public String getDatesListBox( String objectTable ) throws NemesisException
    {
        SimpleDateFormat sdfHour1 = new SimpleDateFormat( "yyyy-MM-dd EE" , Locale.US );
        SimpleDateFormat sdfValues = new SimpleDateFormat( "yyyyMMdd" , Locale.US );
        Calendar today = Calendar.getInstance( );
        System.out.println(objectTable);
         String coso = "<SELECT id='OLB' NAME='OLB' MULTIPLE SIZE='15' style='width: 110px; font-size: 11px'>";
         for( int i = 0; i < 365 ; i++ )
         {             
             if(objectTable.trim( ).equals( "" ))
                 coso += "\n <OPTION VALUE='"+sdfValues.format( today.getTime( ) )+"'> "+sdfHour1.format( today.getTime( ) )+" </OPTION>";
             else
             {
                 if( sdfHour1.format( today.getTime( ) ).toLowerCase( ).contains( objectTable.toLowerCase( ) ) )
                 {
                     coso += "\n <OPTION VALUE='"+sdfValues.format( today.getTime( ) )+"'> "+sdfHour1.format( today.getTime( ) )+" </OPTION>";
                 }
             }
             today.add( Calendar.DATE, -1 );
         }
         coso += "\n</SELECT>";

        return coso;
    }

	public static String getDataMode() {
		return dataMode;
	}

	public static void setDataMode(String dataMode) {
		NemesisServiceImpl.dataMode = dataMode;
	}

}
