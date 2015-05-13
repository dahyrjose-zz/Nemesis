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

public class NemesisDownloads extends Composite implements ClickHandler {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel northPanel = new HorizontalPanel();
	private HorizontalPanel southPanel = new HorizontalPanel();
	private HorizontalPanel datePanel = new HorizontalPanel();
	private VerticalPanel leftTablesPanel = new VerticalPanel();
	private VerticalPanel leftFieldsPanel = new VerticalPanel();

	private TextBox consBox = new TextBox();
	private DateBox iniDate = new DateBox();
	private DateBox endDate = new DateBox();
	private Label downloadFile = new Label("Generate File");
	private Label exportToFM = new Label("Export to Tigo File Manager");
	private Label leftTablesHead = new Label("Left Table:");
	private Label leftFieldsHead = new Label("Left Table Fields:");

	private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc();
	private String selectedLeftTable;
	private String selectedLeftFields;

	public NemesisDownloads() {
		mainPanel.setSpacing(10);
		mainPanel.setWidth("100%");

//		northPanel.setSpacing(5);
		northPanel.setWidth("100%");
		northPanel.setStyleName("oddRow");
		northPanel.addStyleName("silverBottom");

		datePanel.setSpacing(6);

		southPanel.setSpacing(12);

		leftTablesPanel.setSpacing(6);
		leftTablesPanel.setWidth("200px");
		//dbPanel.addStyleName("silverBottom");

		leftFieldsPanel.setSpacing(6);

		iniDate.addStyleName("field_input");
		iniDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
		iniDate.setValue(new Date());
		endDate.addStyleName("field_input");
		endDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
		endDate.setValue(new Date());
		consBox.addStyleName("field_input");

		downloadFile.addClickHandler(this);
		downloadFile.addStyleName("activelink");

		exportToFM.addClickHandler(this);
		exportToFM.addStyleName("activelink");

		datePanel.add(new Label("Initial Date: "));
		datePanel.add(iniDate);
		datePanel.add(new Label("End Date: "));
		datePanel.add(endDate);
		//northPanel.add(new Label("Constraints: "));
		//northPanel.add(consBox);
		datePanel.add(downloadFile);
		//northPanel.add(exportToFM);

		northPanel.add(datePanel);

		southPanel.add(leftTablesPanel);
		southPanel.add(leftFieldsPanel);

		mainPanel.add(northPanel);
		mainPanel.add(southPanel);
		initWidget(mainPanel);

		loadTables();

//		dbsHead.addStyleName("oddRow");
		leftTablesHead.addStyleName("silverBottom");
		leftTablesHead.setHeight("20px");
		leftTablesHead.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		tablesHead.addStyleName("oddRow");
		leftFieldsHead.addStyleName("silverBottom");
		leftFieldsHead.setWidth("200px");
		leftFieldsHead.setHeight("20px");
		leftFieldsHead.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * Loads the Collector's DataBases List
	 * */
	private void loadTables() {
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR Loading Database List: " + caught.getMessage());
			}
			public void onSuccess(List<String> result) {
				leftTablesPanel.clear();
				leftTablesPanel.add(leftTablesHead);
				Label db;
				for (String t : result) {
					db =  new Label(t);
					db.addStyleName("activelink");
					db.addClickHandler(NemesisDownloads.this);
					leftTablesPanel.add(db);
				}
			}
		};
		dataSvc.getExportableTables("nemesis", callback);
	}

	/**
	 * Loads the Collector's DataBases List
	 * */
	private void loadFields(String selectedTable) {
		this.selectedLeftTable = selectedTable;
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR Getting Tables List: " + caught.getMessage());
			}
			public void onSuccess(List<String> result) {
				leftFieldsPanel.clear();
				leftFieldsPanel.add(leftFieldsHead);
				RadioButton tbl;
				for (String t : result) {
					tbl =  new RadioButton("Tables", t);
					tbl.addStyleName("activelink");
					tbl.addClickHandler(NemesisDownloads.this);
					leftFieldsPanel.add(tbl);
				}
			}
		};
		dataSvc.getExportableTables(selectedTable, callback);
	}

	public void onClick(ClickEvent event) {
		if(((Widget)event.getSource()).getParent().equals(leftTablesPanel)) {
			loadFields(((Label)event.getSource()).getText());
		} else if(((Widget)event.getSource()).getParent().equals(leftFieldsPanel)) {
			selectedLeftFields = ((RadioButton)event.getSource()).getText();
		} else if(event.getSource().equals(downloadFile)) {
			if(iniDate.getValue() == null) {
				PopupMessage.showError("Invalid date format (correct format yyyy-MM-dd), please select the date from the drop down box");
				return;
			}
			Window.open("/smartkpis/zipdatasup?requestType=collectorDownload&format=tsvzip&dbName="+selectedLeftTable+"&tableName="+selectedLeftFields+"&date="+iniDate.getTextBox().getText().replace("-", ""), "dataVw", "");
		} else if(event.getSource().equals(exportToFM)) {
			Window.open("/smartkpis/zipdatasup?requestType=collectorDownload&format=tsvzip&dbName="+selectedLeftTable+"&tableName="+selectedLeftFields+"&date="+iniDate.getTextBox().getText().replace("-", ""), "dataVw", "");
		}
	}
}
