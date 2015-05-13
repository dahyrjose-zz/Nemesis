package org.colombiamovil.smartkpi.client.ui.charts;

import java.util.List;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This is an utility class that handles the generation of automatic charts.
 * The ammount of charts displayed and their location is set at the MultipleChartHandler 
 * class, and the time delay between one chart set and another is a parameter at 
 * the entry point (SmartKpis.CONTROLLER_DELAY)
 * 
 * To launch this it is neccesary to call the history token: runController in the 
 * browser's URL
 * */
public class ScreenController extends Timer {

	private int state = 1;
	private boolean active = true;
	private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc();

	/**
	 * Executes the timer, in the body it handles the status of the controller 
	 * and decides either to update the status or not.
	 * */
	@Override
	public void run() {
		if(isActive()) iterateScreen();
	}

	/**
	 * Set the status of the controller
	 * 
	 * @param active Pause or Play
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return Paused or playing
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Shows the first chart set (state = 0)
	 * 
	 * If the controller is not paused and it runs at the same time that 
	 * this method is invoked, unexpected visualization problems may be 
	 * experienced
	 * */
	public void showFirst() {
		state = 1;
		iterateScreen();
	}

	/**
	 * Shows the previous chart set
	 * 
	 * If the controller is not paused and it runs at the same time that 
	 * this method is invoked, unexpected visualization problems may be 
	 * experienced
	 * */
	public void showPrevious() {
		state-=2;
		iterateScreen();
	}

	/**
	 * Show the next chart.
	 * 
	 * If the controller is not paused and it runs at the same time that 
	 * this method is invoked, unexpected visualization problems may be 
	 * experienced
	 * */
	public void showNext() {
		iterateScreen();
	}

	/**
	 * Shows the last chart set, the one that in the case clause is labeled 
	 * as default
	 * 
	 * If the controller is not paused and it runs at the same time that 
	 * this method is invoked, unexpected visualization problems may be 
	 * experienced
	 * */
	public void showLast() {
		state = 999999;
		iterateScreen();
	}

	private void iterateScreen() {
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}

		AsyncCallback<List<ControllerConfigBean>> callback = new AsyncCallback<List<ControllerConfigBean>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showWarning("An error occurred while trying to load next screen");
//				PopupMessage.showError("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}

			public void onSuccess(List<ControllerConfigBean> result) {
				try {
					System.out.println(" Answered: ("+state+")" + result);
					updateStatus(result);
				} catch (Exception e) {
//					PopupMessage.showWarning("No data found for the given report configuration");
//					PopupMessage.showError("ERROR: " + e.getMessage());
					MainMenuBar.setStatus(MainMenuBar.NORMAL);
					e.printStackTrace();
				}
			}
		};

		dataSvc.getControllerConfig(state, SmartKpis.getLoginForm().getUserBean(), callback);
	}

	/**
	 * Produces an iteration in the controller.
	 * Depending on the value of the status variable, a set of charts will be shown.
	 * In order to make modifications it is neccesary to know the underlying tables 
	 * and the LineChartVisualization.loadJsonData() method to set the correct parameters.
	 * For example:
	 * To show a chart for all Ericsson MSC's, in it's detailed report type, for the counter 
	 * LocationUpdate_SuccessRate in the last week, you will have to call, for one of the 
	 * visualizations variable the following:
	 * 		loadJsonData("mss", "date_add(current_date, interval -1 week)", "current_date", 
	 * 			"LocationUpdate_SuccessRate", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
	 * Being:
	 * 	mss: the report type
	 * 	date_add(current_date, interval -1 week): the initial (variable) date
	 * 	current_date: the final (variable) date
	 * 	LocationUpdate_SuccessRate: the indicator
	 * 	MSSBAR!MSSBOG2!MSSBOG3!MSSMED: the objects
	 * 	det: the report type (detailed)
	 * 
	 * In this method the configuration is stored at nemesis.cfg_web_controllerconfig, 
	 * the program loads the charts according to the state variable.
	 * */
	private void updateStatus(final List<ControllerConfigBean> charts) {
		if(charts.size() < 1) return;
		state = charts.get(0).getConfIndex() + 1;
		if(charts.get(0).getConfHeader().startsWith("InfoSeeMap:")) {
			SmartKpis.setScreen(SmartKpis.getInfoSeeMapsHandler());
			SmartKpis.getInfoSeeMapsHandler().getFrame().setUrl(charts.get(0).getConfHeader().substring(11));
			return;
		}
		SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
		SmartKpis.getMultipleChartHandler().setNumRows(2);
		SmartKpis.getMultipleChartHandler().setNumCols(2);
		SmartKpis.getMultipleChartHandler().arrangeCharts();
		final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
		viz1.loadJsonData(charts.get(0).getObjectType(), charts.get(0).getIniDate(), charts.get(0).getEndDate(), 
				charts.get(0).getKpis(), charts.get(0).getObjects(), charts.get(0).getTableType());
		if(charts.size() < 2) return;
		final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
		new Timer() {
			@Override
			public void run() {
				if(!viz1.isLocked()) {
					viz2.loadJsonData(charts.get(1).getObjectType(), charts.get(1).getIniDate(), charts.get(1).getEndDate(), 
							charts.get(1).getKpis(), charts.get(1).getObjects(), charts.get(1).getTableType());
					this.cancel();
				}
			}
		}.scheduleRepeating(1000);
		if(charts.size() < 3) return;
		final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
		new Timer() {
			@Override
			public void run() {
				if(!viz1.isLocked() && !viz2.isLocked()) {
					viz3.loadJsonData(charts.get(2).getObjectType(), charts.get(2).getIniDate(), charts.get(2).getEndDate(), 
							charts.get(2).getKpis(), charts.get(2).getObjects(), charts.get(2).getTableType());
					this.cancel();
				}
			}
		}.scheduleRepeating(1000);
		if(charts.size() < 4) return;
		final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
		new Timer() {
			@Override
			public void run() {
				if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
					viz4.loadJsonData(charts.get(3).getObjectType(), charts.get(3).getIniDate(), charts.get(3).getEndDate(), 
							charts.get(3).getKpis(), charts.get(3).getObjects(), charts.get(3).getTableType());
					this.cancel();
				}
			}
		}.scheduleRepeating(1000);
	}


	/**
	 * Produces an iteration in the controller.
	 * Depending on the value of the status variable, a set of charts will be shown.
	 * In order to make modifications it is neccesary to know the underlying tables 
	 * and the LineChartVisualization.loadJsonData() method to set the correct parameters.
	 * For example:
	 * To show a chart for all Ericsson MSC's, in it's detailed report type, for the counter 
	 * LocationUpdate_SuccessRate in the last week, you will have to call, for one of the 
	 * visualizations variable the following:
	 * 		loadJsonData("mss", "date_add(current_date, interval -1 week)", "current_date", 
	 * 			"LocationUpdate_SuccessRate", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
	 * Being:
	 * 	mss: the report type
	 * 	date_add(current_date, interval -1 week): the initial (variable) date
	 * 	current_date: the final (variable) date
	 * 	LocationUpdate_SuccessRate: the indicator
	 * 	MSSBAR!MSSBOG2!MSSBOG3!MSSMED: the objects
	 * 	det: the report type (detailed)
	 * 
	 * An easy way to set this is to save a report and copy the parameters to set this.
	 * 
	 * It is important wait for a report to finish before launching a new one 
	 * this is achieved by using timers that iterate until previous visualizations 
	 * are fully loaded and the current one is launched. This is the recommendation, 
	 * but any other similar or better method is allowed.
	 * */
	@SuppressWarnings("unused")
	private void updateStatus() {
//		System.out.println(new Date() + " Iteration: " + state);
		switch (state) {
		// Radio Analysis - Rates
		case 0:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
			viz1.loadJsonData("network", "date_add(current_date, interval -1 week)", 
					"current_date", "AssSuccRate,ImmAssSuccRate,CSSuccRate,SSSProcSuccRateCS", "Tigo Colombia", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("network", "date_add(current_date, interval -1 week)", 
								"current_date", "TCH_LOSS_RATE,SDCCH_LOSS_RATE,TCH_DROP_RATE,SDCCH_DROP_RATE", "Tigo Colombia", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("network", "date_add(current_date, interval -1 week)", 
								"current_date", "PDCH_BLOCK_RATE,TBF_Drop_Rate_DL,TBF_Drop_Rate_UL", "Tigo Colombia", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
						viz4.loadJsonData("network", "date_add(current_date, interval -1 week)", 
								"current_date", "HO_INTRACELL_SUCC_RATE,INTCELL_IN_HO_SUCC_RATE,INTERBSC_HO_SUCC_RATE", "Tigo Colombia", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
		} break;

		// Radio Analysis - Volume
		case 1:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
			viz1.loadJsonData("network", "date_add(current_date, interval -1 week)", 
					"current_date", "TCH_TRAFF_CARRIED,SDCCH_TRAFF_CARRIED", "Tigo Colombia", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("network", "date_add(current_date, interval -1 week)", 
								"current_date", "AssAtt,ImmAssAtt,AssSucc,SUCTCHSE", "Tigo Colombia", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("network", "date_add(current_date, interval -1 week)", 
								"current_date", "PDCH_ALLOCATED,PDCHs_Assi_EDGE_DL,PDCHs_Assi_EDGE_UL,PDCHs_Assi_GPRS_DL,PDCHs_Assi_GPRS_UL", "Tigo Colombia", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
						viz4.loadJsonData("network", "date_add(current_date, interval -1 week)", 
								"current_date", "INTERBSC_HO_ATT,INTERCELL_IN_HO_ATT,INTERCELL_OUT_HO_ATT,INTRACELL_HO_ATT", "Tigo Colombia", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
		} break;

		case 2:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
			viz1.loadJsonData("sgsn3g", "date_add(current_date, interval -1 week)", 
					"current_date", "attach_succ_rate,Gb_attach_succ_rate,Iu_attach_succ_rate", "SGSN", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("sgsn3g", "date_add(current_date, interval -1 week)", 
								"current_date", "PDP_ctx_act_succ_rate,Gb_PDP_ctx_act_succ_rate,Iu_PDP_ctx_act_succ_rate", "SGSN", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("sgsn3g", "date_add(current_date, interval -1 week)", 
								"current_date", "SGSN_RAU_succ_rate,Gb_SGSN_RAU_succ_rate,Iu_SGSN_RAU_succ_rate", "SGSN", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
						viz4.loadJsonData("sgsn3g", "date_add(current_date, interval -1 week)", 
								"current_date", "Gn_mean_rec_thr,Gn_mean_sent_thr", "SGSN", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
		} break;

		case 3:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			viz1.loadJsonData("ggsn3g", "date_add(current_date, interval -1 week)", 
					"current_date", "PDP_ctx_activation_succ_ratio", "GGSN", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("ggsn3g", "date_add(current_date, interval -1 week)", 
								"current_date", "Avg_PDP_ctx_act", "GGSN", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("ggsn3g", "date_add(current_date, interval -1 week)", 
								"current_date", "Gi_dl_Avg_thr_in_MB,Gi_up_Avg_thr_in_MB", "GGSN", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
		} break;

		case 4:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
			viz1.loadJsonData("mss", "date_add(current_date, interval -1 week)", 
					"current_date", "LocationUpdate_SuccessRate", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("mss", "date_add(current_date, interval -1 week)", 
								"current_date", "Paging_SuccessRate", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
						this.cancel();
					}
					System.out.println("Waiting");
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("mss", "date_add(current_date, interval -1 week)", 
								"current_date", "SM_SuccessRate", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
						this.cancel();
					}
					System.out.println("Waiting");
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
						viz4.loadJsonData("mss", "date_add(current_date, interval -1 week)", 
								"current_date", "VLR_Subs_Att", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
		} break;

		case 5:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
			viz1.loadJsonData("msx", "date_add(current_date, interval -1 week)", 
					"current_date", "Call_Setup_Success_Rate", "MSXBAQ1!MSXBAQ2!MSXBOG3!MSXMED1", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("msx", "date_add(current_date, interval -1 week)", 
								"current_date", "Paging_SuccessRate", "MSXBAQ1!MSXBAQ2!MSXBOG3!MSXMED1", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("msx", "date_add(current_date, interval -1 week)", 
								"current_date", "SM_SuccessRate", "MSXBAQ1!MSXBAQ2!MSXBOG3!MSXMED1", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
						viz4.loadJsonData("msx", "date_add(current_date, interval -1 week)", 
								"current_date", "VLR_Subs_Total", "MSXBAQ1!MSXBAQ2!MSXBOG3!MSXMED1", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
		} break;

		case 6:
			SmartKpis.setScreen(SmartKpis.getInfoSeeMapsHandler());
			SmartKpis.getInfoSeeMapsHandler().getFrame().setUrl("http://10.65.35.215:8080/InfoSee/Tigo_Colombia_PYB_COS.html");
		break;

		case 7:
			SmartKpis.setScreen(SmartKpis.getInfoSeeMapsHandler());
			SmartKpis.getInfoSeeMapsHandler().getFrame().setUrl("http://10.65.35.215:8080/InfoSee/Tigo_Colombia_PYB_CEN.html");
		break;

		case 8:
			SmartKpis.setScreen(SmartKpis.getInfoSeeMapsHandler());
			SmartKpis.getInfoSeeMapsHandler().getFrame().setUrl("http://10.65.35.215:8080/InfoSee/Tigo_Colombia_PYB_NOR.html");
		break;

		case 9:
			SmartKpis.setScreen(SmartKpis.getInfoSeeMapsHandler());
			SmartKpis.getInfoSeeMapsHandler().getFrame().setUrl("http://10.65.35.215:8080/InfoSee/Tigo_Colombia_PYB_SUR.html");
		break;

		case 10:
			SmartKpis.setScreen(SmartKpis.getInfoSeeMapsHandler());
			SmartKpis.getInfoSeeMapsHandler().getFrame().setUrl("http://10.65.35.215:8080/InfoSee/Tigo_Colombia_PYB_ORI.html");
		break;

		// TCH_TRAFF_CARRIED,SDCCH_TRAFF_CARRIED
		default:
		{
			SmartKpis.setScreen(SmartKpis.getMultipleChartHandler());
			SmartKpis.getMultipleChartHandler().setNumRows(2);
			SmartKpis.getMultipleChartHandler().setNumCols(2);
			SmartKpis.getMultipleChartHandler().arrangeCharts();
			final LineChartVisualization viz1 = SmartKpis.getMultipleChartHandler().getChart(0, 0);
			final LineChartVisualization viz2 = SmartKpis.getMultipleChartHandler().getChart(0, 1);
			final LineChartVisualization viz3 = SmartKpis.getMultipleChartHandler().getChart(1, 0);
			final LineChartVisualization viz4 = SmartKpis.getMultipleChartHandler().getChart(1, 1);
			viz1.loadJsonData("msc", "date_add(current_date, interval -1 week)", 
					"current_date", "ASR", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked()) {
						viz2.loadJsonData("msc", "date_add(current_date, interval -1 week)", 
								"current_date", "TOTAL_CALLS_ATTEMPTS", "MSSBAR!MSSBOG2!MSSBOG3!MSSMED", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked()) {
						viz3.loadJsonData("msc", "date_add(current_date, interval -1 week)", 
								"current_date", "ASR", "MSXBAQ1!MSXBAQ2!MSXBOG3!MSXMED1", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			new Timer() {
				@Override
				public void run() {
					if(!viz1.isLocked() && !viz2.isLocked() && !viz3.isLocked()) {
						viz4.loadJsonData("msc", "date_add(current_date, interval -1 week)", 
								"current_date", "TOTAL_CALLS_ATTEMPTS", "MSXBAQ1!MSXBAQ2!MSXBOG3!MSXMED1", "det");
						this.cancel();
					}
				}
			}.scheduleRepeating(1000);
			state = -1;
		} break;
		}
		state ++;
	}
}
