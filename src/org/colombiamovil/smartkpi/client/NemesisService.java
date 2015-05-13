package org.colombiamovil.smartkpi.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.colombiamovil.smartkpi.client.alarms.CfgAlarm;
import org.colombiamovil.smartkpi.client.alarms.HistoryAlarm;
import org.colombiamovil.smartkpi.client.screener.KpiObjectValues;
import org.colombiamovil.smartkpi.client.security.UserBean;
import org.colombiamovil.smartkpi.client.ui.charts.ControllerConfigBean;
import org.colombiamovil.smartkpi.client.ui.drill.CorrelationBean;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportCategory;
import org.colombiamovil.smartkpi.client.ui.savedreports.SavedReportBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("nemesis")
public interface NemesisService extends RemoteService {

	// Session initiation method
	public UserBean initSession(String userName, String password, boolean withDomain) throws NemesisException;
	public String checkSession(String sessionId) throws NemesisException;

	public String[] getScreenerKpis(String objectType);
	public KpiObjectValues[] getScreenerObjects(String dataTable, String date, String[] kpis, String[] constraints);
	public String[][] getChartData(String objType, String iniDate, String endDate, String kpis, String objects, UserBean user) throws NemesisException;
	public String[][] getChartData(String objType, String iniDate, String endDate, String kpis, String objects, String reportType, UserBean user) throws NemesisException;
	public String[][] getChartData(String alarmId, UserBean user) throws NemesisException;
	public String getChartJson(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user) throws NemesisException;
	public String getChartJson(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, boolean toAppend, UserBean user) throws NemesisException;
	public LinkedHashMap<String, ArrayList<String[]>> getKpis(String objectType);
	public String[] getObjects(String objectTable);
	public String getObjectsListBox(String objectTable) throws NemesisException;
	public String getDatesListBox(String objectTable) throws NemesisException;
	public String getObjectsListBox(String objectTable, String filter) throws NemesisException;
	public String[] getReportTypes(String objectTable) throws NemesisException;
	public String saveChart(String objectType, String tableType, String iniDate, String endDate, String objects, String kpis, String owner) throws NemesisException;

	public LinkedHashMap<String, CfgAlarm> getCfgAlarms(String msisdn) throws NemesisException;
	public LinkedHashMap<String, HistoryAlarm> getHistoryAlarms(String alarmId, String msisdn) throws NemesisException;
	public List<ReportCategory> getReportMenu() throws NemesisException;
	public ReportCategory getReactiveCategory(ReportCategory category) throws NemesisException;

	public List<SavedReportBean> getSavedReports(UserBean user) throws NemesisException;

	/**
	 * Downloads menu services
	 * */
	public List<String> getExportableDbs(String tableType) throws NemesisException;
	public List<String> getExportableTables(String db) throws NemesisException;

	public Map<String, String[]> reportExport(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user) throws NemesisException;
	public Map<String, String[]> getFilejobs(UserBean user) throws NemesisException;

	/**
	 * Drill methods
	 * */
	public List<CorrelationBean> getCorrelationTable(String xObjectType, String yObjectType, String tableType, String groupBy, String yObject, String iniDate, String endDate, String kpi, String xFilter, String yFilteri) throws NemesisException;


	public List<ControllerConfigBean> getControllerConfig(int index, UserBean user) throws NemesisException;
    String getChartJson( String objectType, String dates, String kpis, String objects, String repType, boolean b, UserBean userBean )throws NemesisException;
    
}
