package org.colombiamovil.smartkpi.client;

import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.repots.IpCoreReport;
import org.colombiamovil.smartkpi.client.ui.AlarmsView;
import org.colombiamovil.smartkpi.client.ui.CollectorDownloads;
import org.colombiamovil.smartkpi.client.ui.DrillDownReport;
import org.colombiamovil.smartkpi.client.ui.Infosee;
import org.colombiamovil.smartkpi.client.ui.KpiScreener;
import org.colombiamovil.smartkpi.client.ui.KpiTop;
import org.colombiamovil.smartkpi.client.ui.LoginForm;
import org.colombiamovil.smartkpi.client.ui.NemesisDownloads;
import org.colombiamovil.smartkpi.client.ui.News;
import org.colombiamovil.smartkpi.client.ui.ProcessQueueView;
import org.colombiamovil.smartkpi.client.ui.QuickChart;
import org.colombiamovil.smartkpi.client.ui.ReportForm;
import org.colombiamovil.smartkpi.client.ui.SavedReportsForm;
import org.colombiamovil.smartkpi.client.ui.charts.InfoSeeMapsHandler;
import org.colombiamovil.smartkpi.client.ui.charts.LineChartVisualization;
import org.colombiamovil.smartkpi.client.ui.charts.LineChartVisualizationContainer;
import org.colombiamovil.smartkpi.client.ui.charts.MotionChartVisualization;
import org.colombiamovil.smartkpi.client.ui.charts.MultipleChartHandler;
import org.colombiamovil.smartkpi.client.ui.charts.ScreenController;
import org.colombiamovil.smartkpi.client.ui.charts.WeeklyReports;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.Table;

/**
 * Entry point for the SmartKpis AJAX Application Client
 * 
 * @author Dahyr.Vergara
 */
public class SmartKpis implements EntryPoint, ValueChangeHandler<String> {

	// Main widgets for the applications
	private static HorizontalPanel mainPanel;
	private static VerticalPanel backPanel;
	private static HTML header;
	private static MainMenuBar mainMenu;

	// Important configuration variables
	private static final int CONTROLLER_DELAY = 60000;
	private String historyToken;

	// Screen that can be embedded at mainPanel, controlled by a history token
	public static News news = new News();
	public static KpiScreener screener;
	public static ReportForm reportForm;
	public static Infosee infosee;
	public static KpiTop kpitop;
	public static LineChartVisualization visualization;
	public static LineChartVisualizationContainer visualizationContainer;
	public static MotionChartVisualization motionViz;
	public static WeeklyReports weekReps;
	public static QuickChart chart;
	public static AlarmsView alarmsView;
	public static SavedReportsForm savedReportsForm;
	public static CollectorDownloads collectorDownloads;
	public static NemesisDownloads nemesisDownloads;
	public static LoginForm loginForm;
	public static ProcessQueueView processQueueView;
	public static DrillDownReport drillDownReport;
	public static MultipleChartHandler multipleChartHandler;
	public static InfoSeeMapsHandler infoSeeMapsHandler;
	public static ScreenController screenController;
	public static IpCoreReport ipCoreReport;

	// The data service to use accross the application
	private static NemesisServiceAsync dataSvc;

	/**
	 * This is the entry point method.
	 * The GUI elements are loaded only when they're necessary 
	 * to avoid delay of the first time load excess.
	 */
	public void onModuleLoad() {
		mainPanel = new HorizontalPanel();
		backPanel = new VerticalPanel();
		backPanel.setWidth("100%");
		header = new HTML("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">" +
			"<tr width=\"100%\"><td align=\"right\" valign=\"top\" width=\"100%\"><img src=\"images/logo.bmp\" border=\"0\" /></td></tr></table>");
		header.setStyleName("headerStyle");
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");

		mainMenu = new MainMenuBar();
		VisualizationUtils.loadVisualizationApi(//onLoad, packages)
		//AjaxLoader.loadVisualizationApi(
				new Runnable() {
					public void run() {
						//getMotionChart();
						//getVisualization().setLocked(true);
						//mainPanel.add(visualization.buildALC());
						//ArrayList<String> list = new ArrayList<String>();
						//list.add("carried_traffic");
						//list.add("traffic_capacity");
						//visualization.loadData("nms_route", "", "", list);
						//getVisualization().setLocked(false);
					}
				}, AnnotatedTimeLine.PACKAGE, LineChart.PACKAGE, Table.PACKAGE);
				/*, AreaChart.PACKAGE,
				BarChart.PACKAGE,
				ColumnChart.PACKAGE, Gauge.PACKAGE, GeoMap.PACKAGE,
				IntensityMap.PACKAGE, MotionChart.PACKAGE,
				MapVisualization.PACKAGE, MotionChart.PACKAGE,
				OrgChart.PACKAGE, PieChart.PACKAGE, ScatterChart.PACKAGE*/


		// Associate the Main panel with the HTML host page.
		backPanel.add(header);
		backPanel.add(mainMenu);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		backPanel.add(mainPanel);
		//mainPanel.add(getReportForm());
		//LoginForm.getInstance().center();
		//mainPanel.add(getDataTable());
		//Window.open("SmartWidgets/SmartDataTable.swf?objectType=route&iniDate=20091101&endDate=20091102&kpis=carried_traffic,on_service_lines&objects='DCOME1 (MSSMED)','DBSH03 (MSSMED)'&tableType=det", "", "");
		//Window.open("/xmldatasup?endDate=20091102&iniDate=20091101&kpis=carried%5Ftraffic%2Con%5Fservice%5Flines&tableType=det&objects=%27DCOME1%20%28MSSMED%29%27%2C%27DBSH03%20%28MSSMED%29%27&objectType=route&format=xlsxml", "_self", "");
		//Window.open("/xmldatasup?iniDate=20091104&endDate=20091105&kpis=carried%5Ftraffic%2Con%5Fservice%5Flines&tableType=det&objects='DCOME1 (MSSMED)','DBSH03 (MSSMED)'&objectType=route&format=xlsxml", "_self", "");

		historyToken = History.getToken();
		if(historyToken.length() == 0) {
			History.newItem("loginForm");
		}
		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();

		RootPanel.get("stockList").add(backPanel);

		getLoginForm().checkSessionData();
	}

	/**
	 * View controller with key words to load one of the mapped screens.
	 * 
	 * @param screen History token for the desired screen
	 * */
	public static void setScreen(String screen) {
		header.setVisible(true);
		mainMenu.buildStandardMenu();
		getScreenController().cancel();
		if(!getLoginForm().isAuthenticated() && !screen.equalsIgnoreCase("loginForm")) {
			//PopupMessage.showWarning("Session Expired!");
			screen = "loginForm";
		}
		if(mainPanel.getWidgetCount() > 0) mainPanel.remove(0);
		if(screen.equalsIgnoreCase("viz")) {
			mainPanel.add(getVisualizationContainer());
		} else if(screen.equalsIgnoreCase("savedreps")) {
			mainPanel.add(getSavedReportsForm());
		} else if(screen.equalsIgnoreCase("weekly")) {
			mainPanel.add(getWeeklyReports());
		} else if(screen.equalsIgnoreCase("kpiscr")) {
			mainPanel.add(getScreener());
		} else if(screen.equalsIgnoreCase("news")) {
			mainPanel.add(news);
		} else if(screen.equalsIgnoreCase("chart")) {
			mainPanel.add(getChart());
		} else if(screen.equalsIgnoreCase("newRep")) {
			mainPanel.add(getReportForm());
		} else if(screen.equalsIgnoreCase("alarms")) {
			mainPanel.add(getAlarmsView());
		} else if(screen.equalsIgnoreCase("colDlds")) {
			mainPanel.add(getCollectorDownloads());
		} else if(screen.equalsIgnoreCase("nemDlds")) {
			mainPanel.add(getNemesisDownloads());
		} else if(screen.equalsIgnoreCase("loginForm")) {
			mainPanel.add(getLoginForm());
		} else if(screen.equalsIgnoreCase("processQueue")) {
			mainPanel.add(getProcessQueueView());
		} else if(screen.equalsIgnoreCase("drill")) {
			mainPanel.add(getDrillDownReport());
		} else if(screen.equalsIgnoreCase("infosee")) {
            mainPanel.add(getInfosee());
        }else if(screen.equalsIgnoreCase("kpitop")) {
            mainPanel.add(getKpiTop());
        } else if(screen.equalsIgnoreCase("runController")) {
			// Show the first visualization
			header.setVisible(false);
			mainMenu.buildControllerMenu();
			mainPanel.add(getMultipleChartHandler());
			getScreenController().showFirst();
			// Start the controller
			getScreenController().scheduleRepeating(CONTROLLER_DELAY);
		} else if(screen.equalsIgnoreCase("ipCoreReport")){
		    mainPanel.add(getIpCoreRport());
		} else {
			mainPanel.add(getLoginForm());
		}
		History.newItem(screen);
		//History.fireCurrentHistoryState();
	}

	/**
	 * Directly set some widget in the main screen
	 * 
	 * @param screen The screen widget
	 * */
	public static void setScreen(Widget screen) {
		if(mainPanel.getWidget(0) != null) mainPanel.remove(0);
		mainPanel.add(screen);
	}


	// Here start the instantiation methods for the different views

	public static KpiScreener getScreener() {
		if(screener == null) {
			screener = new KpiScreener();
		}
		return screener;
	}
    public static Infosee getInfosee() {
        if(infosee == null) {
            infosee = new Infosee();
        }
        return infosee;
    }
    public static KpiTop getKpiTop() {
        if(kpitop == null) {
            kpitop = new KpiTop();
        }
        return kpitop;
    }
	public static LineChartVisualization getVisualization() {
		if(visualization == null) {
			visualization = new LineChartVisualization();
		}
		return visualization;
	}
	public static LineChartVisualizationContainer getVisualizationContainer() {
		if(visualizationContainer == null) {
			visualizationContainer = new LineChartVisualizationContainer();
		}
		return visualizationContainer;
	}
	public static void setDeleteVisualizationContainer()
	{
		visualizationContainer.clear();
	}
	/*public static MotionChartVisualization getMotionChart() {
		if(motionViz == null) {
			motionViz = new MotionChartVisualization();
		}
		return motionViz;
	}*/

	public static LoginForm getLoginForm() {
		if(loginForm == null) {
			loginForm = new LoginForm();
		}
		return loginForm;
	}

	public static WeeklyReports getWeeklyReports() {
		if(weekReps == null) {
			weekReps = new WeeklyReports();
		}
		return weekReps;
	}

	public static QuickChart getChart() {
		if(chart == null) {
			chart = new QuickChart();
		}
		return chart;
	}

	public static ReportForm getReportForm() {
		if(reportForm == null) {
			reportForm = new ReportForm();
		}
		return reportForm;
	}

	public static AlarmsView getAlarmsView() {
		if(alarmsView == null) {
			alarmsView = new AlarmsView();
		}
		return alarmsView;
	}

	public static SavedReportsForm getSavedReportsForm() {
		if(savedReportsForm == null) {
			savedReportsForm = new SavedReportsForm();
		}
		return savedReportsForm;
	}

	public static CollectorDownloads getCollectorDownloads() {
		if(collectorDownloads == null) {
			collectorDownloads = new CollectorDownloads();
		}
		return collectorDownloads;
	}

	public static NemesisDownloads getNemesisDownloads() {
		if(nemesisDownloads == null) {
			nemesisDownloads = new NemesisDownloads();
		}
		return nemesisDownloads;
	}

	public static ProcessQueueView getProcessQueueView() {
		if(processQueueView == null) {
			processQueueView = new ProcessQueueView();
		}
		return processQueueView;
	}

	public static DrillDownReport getDrillDownReport() {
		if(drillDownReport == null) {
			drillDownReport = new DrillDownReport();
		}
		return drillDownReport;
	}

	public static MultipleChartHandler getMultipleChartHandler() {
		if(multipleChartHandler == null) {
			multipleChartHandler = new MultipleChartHandler();
		}
		return multipleChartHandler;
	}

	public static InfoSeeMapsHandler getInfoSeeMapsHandler() {
		if(infoSeeMapsHandler == null) {
			infoSeeMapsHandler = new InfoSeeMapsHandler();
		}
		return infoSeeMapsHandler;
	}

	public static ScreenController getScreenController() {
		if(screenController == null) {
			screenController = new ScreenController();
		}
		return screenController;
	}

	public static NemesisServiceAsync getDataSvc() {
		dataSvc = GWT.create(NemesisService.class);
		return dataSvc;
	}
   public static IpCoreReport getIpCoreRport() {
        if(true){//if(ipCoreReport == null) {//para que siempre arme uno nuevo
            ipCoreReport = new IpCoreReport();
        }
        return ipCoreReport;
    }

	/**
	 * Handler for the history
	 * */
	public void onValueChange(ValueChangeEvent<String> event) {
//		System.out.println("UI Changed: " + event.getValue());
		setScreen(event.getValue());
	}
}
