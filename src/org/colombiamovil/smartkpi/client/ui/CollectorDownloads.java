package org.colombiamovil.smartkpi.client.ui;

import java.util.Date;
import java.util.List;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class CollectorDownloads extends Composite implements ClickHandler {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel northPanel = new HorizontalPanel();
	private HorizontalPanel southPanel = new HorizontalPanel();
	private HorizontalPanel datePanel = new HorizontalPanel();
	private VerticalPanel dbPanel = new VerticalPanel();
	private VerticalPanel tablePanel = new VerticalPanel();

	private TextBox consBox = new TextBox();
	private DateBox dateBox = new DateBox();
	private Label downloadFile = new Label("Generate File");
	private Label exportToFM = new Label("Export to Tigo File Manager");
	private Label dbsHead = new Label("Pick a database");
	private Label tablesHead = new Label("Pick a table");

	private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc();
	private String selectedDb;
	private String selectedTable;

	public CollectorDownloads() {
		mainPanel.setSpacing(10);
		mainPanel.setWidth("100%");

//		northPanel.setSpacing(5);
		northPanel.setWidth("100%");
		northPanel.setStyleName("oddRow");
		northPanel.addStyleName("silverBottom");

		datePanel.setSpacing(6);

		southPanel.setSpacing(12);

		dbPanel.setSpacing(6);
		dbPanel.setWidth("200px");
		//dbPanel.addStyleName("silverBottom");

		tablePanel.setSpacing(6);

		dateBox.addStyleName("field_input");
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
		dateBox.setValue(new Date());
		consBox.addStyleName("field_input");

		downloadFile.addClickHandler(this);
		downloadFile.addStyleName("activelink");

		exportToFM.addClickHandler(this);
		exportToFM.addStyleName("activelink");

		datePanel.add(new Label("Date: "));
		datePanel.add(dateBox);
		//northPanel.add(new Label("Constraints: "));
		//northPanel.add(consBox);
		datePanel.add(downloadFile);
		//northPanel.add(exportToFM);

		northPanel.add(datePanel);

		southPanel.add(dbPanel);
		southPanel.add(tablePanel);

		mainPanel.add(northPanel);
		mainPanel.add(southPanel);
		initWidget(mainPanel);

		loadDataBases();

//		dbsHead.addStyleName("oddRow");
		dbsHead.addStyleName("silverBottom");
		dbsHead.setHeight("20px");
		dbsHead.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		tablesHead.addStyleName("oddRow");
		tablesHead.addStyleName("silverBottom");
		tablesHead.setWidth("200px");
		tablesHead.setHeight("20px");
		tablesHead.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * Loads the Collector's DataBases List
	 * */
	private void loadDataBases() {
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR Loading Database List: " + caught.getMessage());
			}
			public void onSuccess(List<String> result) {
				dbPanel.clear();
				dbPanel.add(dbsHead);
				Label db;
				for (String t : result) {
					db =  new Label(t);
					db.addStyleName("activelink");
					db.addClickHandler(CollectorDownloads.this);
					dbPanel.add(db);
				}
			}
		};
		dataSvc.getExportableDbs("COLLECTOR", callback);
	}

	/**
	 * Loads the Collector's DataBases List
	 * */
	private void loadTables(String selectedDb) {
		this.selectedDb = selectedDb;
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR Getting Tables List: " + caught.getMessage());
			}
			public void onSuccess(List<String> result) {
				tablePanel.clear();
				tablePanel.add(tablesHead);
				RadioButton tbl;
				for (String t : result) {
					tbl =  new RadioButton("Tables", t);
					tbl.addStyleName("activelink");
					tbl.addClickHandler(CollectorDownloads.this);
					tablePanel.add(tbl);
				}
			}
		};
		dataSvc.getExportableTables(selectedDb, callback);
	}

	public void onClick(ClickEvent event) {
		if(((Widget)event.getSource()).getParent().equals(dbPanel)) {
			loadTables(((Label)event.getSource()).getText());
		} else if(((Widget)event.getSource()).getParent().equals(tablePanel)) {
			selectedTable = ((RadioButton)event.getSource()).getText();
		} else if(event.getSource().equals(downloadFile)) {
			if(dateBox.getValue() == null) {
				PopupMessage.showError("Invalid date format (correct format yyyy-MM-dd), please select the date from the drop down box");
				return;
			}
			Window.open("/smartkpis/zipdatasup?requestType=collectorDownload&format=tsvzip&dbName="+selectedDb+"&tableName="+selectedTable+"&date="+dateBox.getTextBox().getText().replace("-", ""), "dataVw", "");
		} else if(event.getSource().equals(exportToFM)) {
			Window.open("/smartkpis/zipdatasup?requestType=collectorDownload&format=tsvzip&dbName="+selectedDb+"&tableName="+selectedTable+"&date="+dateBox.getTextBox().getText().replace("-", ""), "dataVw", "");
		}
	}
}
