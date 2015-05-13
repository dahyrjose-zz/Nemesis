package org.colombiamovil.smartkpi.client.ui;

import java.util.LinkedHashMap;

import org.colombiamovil.smartkpi.client.NemesisService;
import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.alarms.CfgAlarm;
import org.colombiamovil.smartkpi.client.alarms.HistoryAlarm;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AlarmsView extends Composite implements ClickHandler {

	private VerticalPanel mainPanel, northPanel;
	private HorizontalPanel menuPanel;
	private FlexTable cfgAlarmsTable;
	private FlexTable histAlarmsTable;
	private Label showAlarms, showHistory;

	private NemesisServiceAsync svc = GWT.create(NemesisService.class);
	private LinkedHashMap<String, CfgAlarm> alarmsMap;
	private LinkedHashMap<String, HistoryAlarm> historyMap;
	private String selectedAlarm, selectedHistory;

	public AlarmsView() {
		mainPanel = new VerticalPanel();
		mainPanel.setSpacing(10);
		mainPanel.setWidth("100%");

		northPanel = new VerticalPanel();
		northPanel.setWidth("100%");
		northPanel.addStyleName("watchList");

		showAlarms = new Label("Show Alarms");
		showAlarms.addClickHandler(this);
		showAlarms.addStyleName("activelink");
		showHistory = new Label("Show History");
		showHistory.addClickHandler(this);
		showHistory.addStyleName("activelink");
		menuPanel = new HorizontalPanel();
		menuPanel.setSpacing(10);
		menuPanel.add(showAlarms);
		menuPanel.add(showHistory);

		cfgAlarmsTable = new FlexTable();
		cfgAlarmsTable.setWidth("100%");
		cfgAlarmsTable.setCellPadding(2);
		cfgAlarmsTable.setCellSpacing(0);
		//cfgAlarmsTable.setWidth("100%");
		cfgAlarmsTable.addStyleName("watchList");
		cfgAlarmsTable.getRowFormatter().addStyleName(0, "watchListHeader");
		cfgAlarmsTable.setText(0, 0, "Alarm Title");
		cfgAlarmsTable.setText(0, 1, "Periodicity");
		cfgAlarmsTable.setText(0, 2, "Condition");
		cfgAlarmsTable.setText(0, 3, "Constraints");

		histAlarmsTable = new FlexTable();
		histAlarmsTable.setCellPadding(2);
		histAlarmsTable.setCellSpacing(0);
		histAlarmsTable.addStyleName("watchList");
		histAlarmsTable.getRowFormatter().addStyleName(0, "watchListHeader");
		histAlarmsTable.setText(0, 0, "Alarm Code");
		histAlarmsTable.setText(0, 1, "Alarm Title");
		//histAlarmsTable.setText(0, 2, "Time");
		histAlarmsTable.setText(0, 2, "Alarmed Elements");
		//histAlarmsTable.setText(0, 4, "Value");
		//histAlarmsTable.setVisible(false);

		northPanel.add(menuPanel);
		mainPanel.add(northPanel);
		mainPanel.add(cfgAlarmsTable);
		//mainPanel.add(histAlarmsTable);
		loadAlarms();
		initWidget(mainPanel);
	}

	private void loadAlarms() {
		MainMenuBar.setStatus(MainMenuBar.LOADING);
		if(svc == null) {
			svc = SmartKpis.getDataSvc();
		}
		AsyncCallback<LinkedHashMap<String, CfgAlarm>> callback = new AsyncCallback<LinkedHashMap<String, CfgAlarm>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}

			public void onSuccess(LinkedHashMap<String, CfgAlarm> result) {
				updateAlarmsTable(result);
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}
		};
		svc.getCfgAlarms(SmartKpis.getLoginForm().getUserBean().getUserMsisdn(), callback);
	}

	private void loadHistory(String alarmId) {
		MainMenuBar.setStatus(MainMenuBar.LOADING);
		selectedAlarm = alarmId;
		if(svc == null) {
			svc = SmartKpis.getDataSvc();
		}
		AsyncCallback<LinkedHashMap<String, HistoryAlarm>> callback = new AsyncCallback<LinkedHashMap<String, HistoryAlarm>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}

			public void onSuccess(LinkedHashMap<String, HistoryAlarm> result) {
				updateHistoryTable(result);
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}
		};
		svc.getHistoryAlarms(alarmId, SmartKpis.getLoginForm().getUserBean().getUserMsisdn(), callback);
	}

	private void updateAlarmsTable(LinkedHashMap<String, CfgAlarm> alarms) {
		clearTable(cfgAlarmsTable);
		this.alarmsMap = alarms;
		Label title;
		Label condition;
		String condStr, condTitle;
		int i = 0;
		for(CfgAlarm a : alarms.values()) {
			cfgAlarmsTable.getRowFormatter().addStyleName(i + 1, (i % 2 != 0 ? "evenRow" : "oddRow"));
			title = new Label(a.getTitle());
			title.addClickHandler(this);
			title.addStyleName("activelink");
			condStr = a.getIndicator() + " " + a.getThreshold();
			condTitle = condStr;
			if(condStr.length() > 30) condStr = condStr.substring(0, 30) + " ... ";
			condition = new Label(condStr);
			condition.setTitle(condTitle);
			cfgAlarmsTable.setWidget(i + 1, 0, title);
			cfgAlarmsTable.setText(i + 1, 1, a.getPeriod());
			cfgAlarmsTable.setWidget(i + 1, 2, condition);
			cfgAlarmsTable.setText(i + 1, 3, a.getConstraints());
			i++;
		}
	}

	private void updateHistoryTable(LinkedHashMap<String, HistoryAlarm> hist) {
		clearTable(histAlarmsTable);
		this.historyMap = hist;
		Label code;
		int i = 0;
		for(HistoryAlarm h : hist.values()) {
			code = new Label(h.getCode());
			code.addClickHandler(this);
			code.addStyleName("activelink");
			histAlarmsTable.getRowFormatter().addStyleName(i + 1, (i % 2 != 0 ? "evenRow" : "oddRow"));
			histAlarmsTable.setWidget(i + 1, 0, code);
			histAlarmsTable.setText(i + 1, 1, h.getDate());
			//histAlarmsTable.setText(i + 1, 2, h.getTime());
			histAlarmsTable.setHTML(i + 1, 2, h.getElement());
			//histAlarmsTable.setText(i + 1, 4, h.getValue());
			i++;
//			System.out.println(h.getElement());
		}
	}

	public void clearTable(FlexTable table) {
		int rows = table.getRowCount();
		rows--;
		while (rows >= 1) {
			table.removeRow(rows--);
		}
	}

	public void onClick(ClickEvent event) {
		if(event.getSource().equals(showAlarms)) {
			mainPanel.remove(histAlarmsTable);
			mainPanel.add(cfgAlarmsTable);
			//histAlarmsTable.setVisible(false);
			//cfgAlarmsTable.setVisible(true);
		} else if(event.getSource().equals(showHistory)) {
			mainPanel.remove(cfgAlarmsTable);
			mainPanel.add(histAlarmsTable);
			//cfgAlarmsTable.setVisible(false);
			//histAlarmsTable.setVisible(true);
		} else if(((Label)event.getSource()).getParent().equals(histAlarmsTable)) {
			selectedHistory = ((Label)event.getSource()).getText();
			SmartKpis.getVisualization().loadData("nms_" + alarmsMap.get(selectedAlarm).getObjectType(), 
					"date_sub(" + historyMap.get(selectedHistory).getDate().replace("-", "") + ", interval 1 month)", 
					historyMap.get(selectedHistory).getDate().replace("-", ""), 
					alarmsMap.get(selectedAlarm).getIndicator(), historyMap.get(selectedHistory).getElement(), "bh");
			SmartKpis.setScreen("viz");
		} else {
			loadHistory(((Label)event.getSource()).getText());
			mainPanel.remove(cfgAlarmsTable);
			mainPanel.add(histAlarmsTable);
			//cfgAlarmsTable.setVisible(false);
			//histAlarmsTable.setVisible(true);
			//Window.alert(((Hyperlink)event.getSource()).getTargetHistoryToken());
		}
	}
}
