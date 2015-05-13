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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async part of the service.
 */
public interface NemesisServiceAsync {

	// Session initiation method
	public void initSession(String userName, String password, boolean withDomain, AsyncCallback<UserBean> callback);
	public void checkSession(String sessionId, AsyncCallback<String> callback);

	public void getScreenerKpis(String objectType, AsyncCallback<String[]> callback);
	public void getScreenerObjects(String dataTable, String date, String[] kpis, String[] constraints, AsyncCallback<KpiObjectValues[]> callback);
	public void getChartData(String alarmId, UserBean user, AsyncCallback<String[][]> callback);
	public void getChartData(String objType, String iniDate, String endDate, String kpis, String objects, UserBean user, AsyncCallback<String[][]> callback);
	public void getChartData(String objType, String iniDate, String endDate, String kpis, String objects, String reportType, UserBean user, AsyncCallback<String[][]> callback);
	public void getChartJson(String objType, String iniDate, String endDate, String kpis, String objects, String reportType, UserBean user, AsyncCallback<String> callback);
	public void getChartJson(String objType, String iniDate, String endDate, String kpis, String objects, String reportType, boolean toAppend, UserBean user, AsyncCallback<String> callback);
	public void getKpis(String objectTable, AsyncCallback<LinkedHashMap<String, ArrayList<String[]>>> callback);
	public void getObjects(String objectTable, AsyncCallback<String[]> callback);
	public void getReportTypes(String objectTable, AsyncCallback<String[]> callback);
	public void getObjectsListBox(String objectTable, AsyncCallback<String> callback);
	public void getDatesListBox(String objectTable, AsyncCallback<String> callback);
	public void getObjectsListBox(String objectTable, String filter, AsyncCallback<String> callback);
	public void saveChart(String objectType, String reportType, String iniDate, String endDate, String objects, String kpis, String owner, AsyncCallback<String> callback);

	public void getCfgAlarms(String msisdn, AsyncCallback<LinkedHashMap<String, CfgAlarm>> callback);
	public void getHistoryAlarms(String alarmId, String msisdn, AsyncCallback<LinkedHashMap<String, HistoryAlarm>> callback);
	public void getReportMenu(AsyncCallback<List<ReportCategory>> callback);
	public void getReactiveCategory(ReportCategory category, AsyncCallback<ReportCategory> callback);

	public void getSavedReports(UserBean user, AsyncCallback<List<SavedReportBean>> callback);

	/**
	 * Downloads menu services
	 * */
	public void getExportableDbs(String tableType, AsyncCallback<List<String>> callback);
	public void getExportableTables(String db, AsyncCallback<List<String>> callback);

	public void reportExport(String objectType, String iniDate, String endDate, String kpis, String objects, String tableType, UserBean user, AsyncCallback<Map<String, String[]>> callback);
	public void getFilejobs(UserBean user, AsyncCallback<Map<String, String[]>> callback);

	/**
	 * Drill methods
	 * */
	public void getCorrelationTable(String xObjectType, String yObjectType, String tableType, String groupBy, String yObject, String iniDate, String endDate, String kpi, String xFilter, String yFilter, AsyncCallback<List<CorrelationBean>> callback);


	public void getControllerConfig(int index, UserBean user, AsyncCallback<List<ControllerConfigBean>> callback);
    public void getChartJson( String objectType, String dates, String kpis, String objects, String repType, boolean b, UserBean userBean, AsyncCallback<String> callback );
    
}
