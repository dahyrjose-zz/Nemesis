package org.colombiamovil.smartkpi.server.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;

import org.colombiamovil.smartkpi.server.NemesisConnector;

public class QueryUtils {

	public static String getReportParametersQuery(String objectType, String tableType) {
		return "select * from cfg_web_objecttables ot inner join cfg_web_datatables dt using (object_table, object_type) " +
			"where object_type = '" + objectType + "' and table_type = '" + tableType + "'";
	}

	public static String getObjectsQuery(String objectIdField, String objectTable, String nameField, String objectIds) {
		String sql="select group_concat(distinct "+objectIdField+") as ids " +
				"from " + objectTable + " o where " + nameField + " in (" + objectIds + ")";		
		return sql;
	}

	public static String getObjectsQuery(String objectIdField, String objectTable, String nameField, String objectIds, String groupBy) {
		return "select group_concat(distinct "+objectIdField+") as ids " +
				"from " + objectTable + " o where " + nameField + " in (" + objectIds + ")";
	}

	public static String getSimpleReportQuery(String dataIdField, String nameField, String nameAlias, String kpis, String objectTable, 
			String dataTable, String objectIdField, String iniDate, String endDate, String objectTableIds) {
		return getSimpleReportQuery(dataIdField, nameField, nameAlias, kpis, objectTable, 
				dataTable, objectIdField, iniDate, endDate, objectTableIds, null);
	}

	public static String getSimpleReportQuery(String dataIdField, String nameField, String nameAlias, String kpis, String objectTable, 
			String dataTable, String objectIdField, String iniDate, String endDate, String objectTableIds, String groupBy) {
//		String objects = (groupBy != null ? groupBy : nameField + " as " + nameAlias);
		return "select x_date, x_time, " + nameField + " as " + nameAlias + ", " + kpis + " " +
			"from " + objectTable + " o inner join " + dataTable + " d on (o." + objectIdField + " = d." + dataIdField + ") " + 
			"where x_date between " + iniDate + " and " + endDate + " and d." + dataIdField + " in(" + objectTableIds + ") " +
			"group by 1, 2, 3";
	}

	public static String getReportGroupingQuery(String dataIdField, String nameField, String toQuery, String kpis, String objectTable, 
			String dataTable, String objectIdField, String inidate, String enddate, String objectTableIds, String groupClause) {
		return "select d." + dataIdField + ", " + nameField + " as " + toQuery + ", x_date, x_time, " + kpis + " " +
			"from " + objectTable + " o inner join " + dataTable + " d on (o." + objectIdField + " = d." + dataIdField + ") " + 
			"where x_date between " + inidate + " and " + enddate + " and d." + dataIdField + " in(" + objectTableIds + ")" +
			" " + groupClause + " order by 3, 4;";
	}
    public static String getReportGroupingQuery( String dataIdField, String nameField, String toQuery, String kpis, String objectTable, String dataTable, String objectIdField, String dates, String objectTableIds, String groupClause )
    {
        return "select d." + dataIdField + ", " + nameField + " as " + toQuery + ", x_date, x_time, " + kpis + " " +
        "from " + objectTable + " o inner join " + dataTable + " d on (o." + objectIdField + " = d." + dataIdField + ") " + 
        "where x_date in ("+dates+") and d." + dataIdField + " in(" + objectTableIds + ")" +
        " " + groupClause + " order by 3, 4;";
    }
	public static Map<String, String> getReportParametersData(String objectType, String tableType) {
		Map<String, String> result = new Hashtable<String, String>();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rsattr = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rsattr = stmt.executeQuery(getReportParametersQuery(objectType, tableType));

			if(rsattr.next()) {
				result.put("dataTable", rsattr.getString("data_table"));
				result.put("objectTable", rsattr.getString("object_table"));
				result.put("objectIdField", rsattr.getString("object_id_field"));
				result.put("dataIdField", rsattr.getString("data_id_field"));
				result.put("nameField", rsattr.getString("name_field"));
				result.put("nameAlias", rsattr.getString("name_alias"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rsattr != null) rsattr.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static String getObjectIds(Map<String, String> paramsData, String objects) {
		String result = "";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rsobj = null;
		String query = getObjectsQuery(paramsData.get("objectIdField"), paramsData.get("objectTable"), paramsData.get("nameField"), objects);
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rsobj = stmt.executeQuery(query);

			if(rsobj.next()) {
				result = rsobj.getString(1);
			}
		} catch (SQLException e) {
			//System.out.println("Error executing " + query);
			e.printStackTrace();
		} finally {
			try {
				if(rsobj != null) rsobj.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static String getReportQuery(Map<String, String> paramsData, String objects, String kpis, String iniDate, String endDate, 
			String groupClause, boolean grouping, Statement stmt) throws SQLException {
		String query = "";
		String objectIds = getObjectIds(paramsData, objects);
		if(grouping) {
			String toQuery = grouping ? "'"+objects.replace("!", "_")+"'" : paramsData.get("nameAlias");
			query = getReportGroupingQuery(paramsData.get("dataIdField"), paramsData.get("nameField"), toQuery, kpis, paramsData.get("objectTable"), 
					paramsData.get("dataTable"), paramsData.get("objectIdField"), iniDate, endDate, objectIds, groupClause);
		} else {
			query = getSimpleReportQuery(paramsData.get("dataIdField"), paramsData.get("nameField"), paramsData.get("nameAlias"), 
					kpis, paramsData.get("objectTable"), paramsData.get("dataTable"), paramsData.get("objectIdField"), 
					iniDate, endDate, objectIds);
		}
		return query;
	}

	public static String registerUserAction(String userLogin, String actionType, String moduleName, String contentType, String description, String contentLoad) {
		// INSERT INTO nms_netapp_raw VALUES (CURDATE(),CURTIME(),NEMESISID,'USUARIO','ACCION','MODULO','CONTENIDO','DESCRIPCION',CARGA)
		String result = "";

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO nms_netapp_raw (x_date, x_time, id, user_name, action_type, module_name, content_type, description, content_load) VALUES " +
					"(CURRENT_DATE, CURRENT_TIME, 2, '"+userLogin+"', '"+actionType+"', '"+moduleName+"', '"+contentType+"', '"+description+"', '"+contentLoad+"')");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}


}
