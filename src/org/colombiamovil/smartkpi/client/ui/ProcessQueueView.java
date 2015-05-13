package org.colombiamovil.smartkpi.client.ui;

import java.util.Date;
import java.util.Map;

import org.colombiamovil.smartkpi.client.NemesisService;
import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProcessQueueView extends Composite implements ClickHandler {

	private VerticalPanel mainPanel, northPanel;
	private HorizontalPanel menuPanel;
	private FlexTable cfgWebFilejobsTable;
	private Label refresh, lastUpdate;

	private NemesisServiceAsync svc = GWT.create(NemesisService.class);
	private Map<String, String[]> filejobsMap;

	public ProcessQueueView() {
		mainPanel = new VerticalPanel();
		mainPanel.setSpacing(10);
		mainPanel.setWidth("100%");

		northPanel = new VerticalPanel();
		northPanel.setWidth("100%");
		northPanel.addStyleName("watchList");

		refresh = new Label("Refresh List");
		refresh.addClickHandler(this);
		refresh.addStyleName("activelink");
		lastUpdate = new Label();
		menuPanel = new HorizontalPanel();
		menuPanel.setSpacing(10);
		menuPanel.add(refresh);
		menuPanel.add(lastUpdate);

		cfgWebFilejobsTable = new FlexTable();
		cfgWebFilejobsTable.setWidth("100%");
		cfgWebFilejobsTable.setCellPadding(2);
		cfgWebFilejobsTable.setCellSpacing(0);
		//cfgAlarmsTable.setWidth("100%");
		cfgWebFilejobsTable.addStyleName("watchList");
		cfgWebFilejobsTable.getRowFormatter().addStyleName(0, "watchListHeader");
		cfgWebFilejobsTable.setText(0, 0, "Job Status");
		cfgWebFilejobsTable.setText(0, 1, "File Name");
		cfgWebFilejobsTable.setText(0, 2, "Description");

		northPanel.add(menuPanel);
		mainPanel.add(northPanel);
		mainPanel.add(cfgWebFilejobsTable);
		//mainPanel.add(histAlarmsTable);
		loadFilejobs();
		initWidget(mainPanel);
	}

	private void loadFilejobs() {
		MainMenuBar.setStatus(MainMenuBar.LOADING);
		if(svc == null) {
			svc = SmartKpis.getDataSvc();
		}
		AsyncCallback<Map<String, String[]>> callback = new AsyncCallback<Map<String, String[]>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}

			public void onSuccess(Map<String, String[]> result) {
				filejobsMap = result;
				updateFilejobsTable();
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}
		};
		svc.getFilejobs(SmartKpis.getLoginForm().getUserBean(), callback);
	}

	public void updateFilejobsTable(Map<String, String[]> filejobs) {
		this.filejobsMap = filejobs;
		updateFilejobsTable();
	}

	private void updateFilejobsTable() {
		clearTable(cfgWebFilejobsTable);
		Label fileName;
		int i = 0;
		for(String a : filejobsMap.keySet()) {
			cfgWebFilejobsTable.getRowFormatter().addStyleName(i + 1, (i % 2 != 0 ? "evenRow" : "oddRow"));
			fileName = new Label(a);
			if(filejobsMap.get(a)[0].equals("Sent")) {
				fileName.addClickHandler(this);
				fileName.addStyleName("activelink");
			}
			cfgWebFilejobsTable.setText(i + 1, 0, filejobsMap.get(a)[0]);
			cfgWebFilejobsTable.setWidget(i + 1, 1, fileName);
			cfgWebFilejobsTable.setHTML(i + 1, 2, filejobsMap.get(a)[1].replace("; ", "<br/>"));
			i++;
		}
		lastUpdate.setText("Loaded "+filejobsMap.size()+" jobs (Last Update: " + 
				DateTimeFormat.getMediumDateTimeFormat().format(new Date()) + ")");
	}

	public void clearTable(FlexTable table) {
		int rows = table.getRowCount();
		rows--;
		while (rows >= 1) {
			table.removeRow(rows--);
		}
	}

	public void onClick(ClickEvent event) {
		if(event.getSource().equals(refresh)) {
			loadFilejobs();
		} else if(event.getSource() instanceof Label) {
			Window.open("http://10.65.136.17/nemesisgen/" + ((Label)event.getSource()).getText() + ".zip", "dataExport", "");
		}
	}
}
