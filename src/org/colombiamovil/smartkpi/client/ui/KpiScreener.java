package org.colombiamovil.smartkpi.client.ui;

import java.util.ArrayList;
import java.util.Date;

import org.colombiamovil.smartkpi.client.NemesisService;
import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.screener.KpiChooser;
import org.colombiamovil.smartkpi.client.screener.KpiHistogram;
import org.colombiamovil.smartkpi.client.screener.KpiObjectValues;
import org.colombiamovil.smartkpi.client.screener.KpiScreenerOptions;
import org.colombiamovil.smartkpi.client.ui.charts.fusion.FusionChartWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KpiScreener extends VerticalPanel implements ClickHandler {

	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel topLeftPanel = new VerticalPanel();
	private FlexTable objectsFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private HorizontalPanel kpiPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Image addKpiButton = new Image("images/add.gif");
	private Label lastUpdatedLabel = new Label();
	private Button buildChart = new Button();
	// private ArrayList<String> objects = new ArrayList<String>();
	public ArrayList<String> kpis = new ArrayList<String>();
	private NemesisServiceAsync objectsSvc = GWT.create(NemesisService.class);
	private NumberFormat nformat = NumberFormat.getFormat("#,##0.00");
	private KpiHistogram histograms;
	private KpiScreenerOptions options;
	private KpiObjectValues[] objects;
	private KpiChooser chooser;
	private FusionChartWidget chart;

	public KpiScreener() {
		super();
		//this.setWidth("100%");
		mainPanel.setWidth("100%");
		this.addStyleName("kpiScreenerStyle");

		objectsFlexTable.setCellPadding(0);
		objectsFlexTable.setCellSpacing(0);
		objectsFlexTable.addStyleName("watchList");

		newSymbolTextBox.addStyleName("field_input");
		addKpiButton.addStyleName("activelink");
		buildChart.setText("Build Chart");
		buildChart.setStyleName("button");
		buildChart.setStyleName("button");
		buildChart.addClickHandler(this);

		addPanel.add(newSymbolTextBox);
		addPanel.add(addKpiButton);
		addPanel.add(buildChart);
		addPanel.addStyleName("addPanel");
		addPanel.setSpacing(8);

		//kpiPanel.setWidth("100%");

		histograms = new KpiHistogram(this);
		options = new KpiScreenerOptions(this);

		//kpiPanel.add(chart);

		topLeftPanel.add(options);
		topLeftPanel.add(histograms);
		kpiPanel.add(topLeftPanel);
		//mainPanel.add(options);
		mainPanel.add(kpiPanel);
		//mainPanel.add(addPanel);
		chooser = new KpiChooser(this);
		topLeftPanel.add(chooser);
		mainPanel.add(objectsFlexTable);
		mainPanel.add(lastUpdatedLabel);

		// Associate the Main panel with the HTML host page.
		this.add(mainPanel);

		// Setup timer to refresh list automatically.
		/*
		 * Timer refreshTimer = new Timer() {
		 * 
		 * @Override public void run() { refreshWatchList(); } };
		 * refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		 */

		// Listen for mouse events on the Add button.
		addKpiButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addKpi();
			}
		});

		// Listen for keyboard events in the input box.
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					addKpi();
				}
			}
		});
		options.onClick(null);
	}

	public void addKpi(String kpi) {
		if (kpis.contains(kpi))
			return;

		histograms.addKpi(kpi);
	}

	/**
	 * Add stock to FlexTable. Executed when the user clicks the addStockButton
	 * or presses enter in the newSymbolTextBox.
	 */
	private void addKpi() {
		final String symbol = newSymbolTextBox.getText().trim();
		newSymbolTextBox.setFocus(true);

		// Stock code must be between 1 and 10 chars that are numbers, letters,
		// or dots.
		/*
		 * if (!symbol.matches("^[0-9a-zA-Z\\.]{1,10}$")) { Window.alert("'" +
		 * symbol + "' is not a valid symbol."); newSymbolTextBox.selectAll();
		 * return; }
		 */

		newSymbolTextBox.setText("");

		// Don't add the kpi if it's already in the table.
		if (kpis.contains(symbol))
			return;
		histograms.addKpi(symbol);
		refreshWatchList();
	}

	public void loadKpis(String objectType, String date) {
		MainMenuBar.setStatus(MainMenuBar.LOADING);
		if (objectsSvc == null) {
			objectsSvc = GWT.create(NemesisService.class);
		}

		// Set up the callback object.
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}

			public void onSuccess(String[] result) {
				//updateTable(result);
				histograms.clear();
				for (String kpi : result) {
					addKpi(kpi);
				}
				refreshWatchList();
			}
		};

		// Make the call to the kpi screener service.
		objectsSvc.getScreenerKpis(objectType, callback);
		chooser.loadKpis(objectType);
	}

	public void refreshWatchList() {
		MainMenuBar.setStatus(MainMenuBar.LOADING);
		// Initialize the service proxy.
		if (objectsSvc == null) {
			objectsSvc = GWT.create(NemesisService.class);
		}

		// Set up the callback object.
		AsyncCallback<KpiObjectValues[]> callback = new AsyncCallback<KpiObjectValues[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}

			public void onSuccess(KpiObjectValues[] result) {
				updateTable(result);
			}
		};

		// Make the call to the kpi screener service.
		objectsSvc.getScreenerObjects(options.getObjType(), options.getDate(), kpis
				.toArray(new String[0]), histograms.getConstraints(), callback);
	}

	@SuppressWarnings("deprecation")
	private void updateTable(KpiObjectValues[] objects) {
		this.objects = objects;
		int rows = objectsFlexTable.getRowCount();
		rows--;
		while (rows >= 0) {
			objectsFlexTable.removeRow(rows--);
		}
		// Building header
		objectsFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		objectsFlexTable.setText(0, 0, "Element");
		for (int i = 0; i < kpis.size(); i++) {
			objectsFlexTable.getCellFormatter().addStyleName(0, i + 1,
					"watchListNumericColumn");
			objectsFlexTable.setText(0, i + 1, kpis.get(i).replaceAll("_", " "));
		}

		Hyperlink element;
		for (int i = 0; i < objects.length; i++) {

			double[] kpiValues = objects[i].getKpiValues();
			//System.out.println(objects);
			element = new Hyperlink(objects[i].getSymbol(), "buildChart");
			element.addClickHandler(this);
			element.setWidth("200px");
			objectsFlexTable.getRowFormatter().addStyleName(i + 1, (i % 2 != 0 ? "evenRow" : "oddRow"));
			objectsFlexTable.setWidget(i + 1, 0, element);
			for (int j = 1; j <= kpiValues.length; j++) {
				objectsFlexTable.getCellFormatter().addStyleName(i + 1, j, "watchListNumericColumn");
				double d = kpiValues[j - 1];
				String kpiText = nformat.format(d);
				objectsFlexTable.setText(i + 1, j, kpiText);
			}
		}

		lastUpdatedLabel.setText("Last update : "
				+ DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
		MainMenuBar.setStatus(MainMenuBar.NORMAL);
	}

	public void onClick(ClickEvent event) {
		if(kpis.size() < 1) {
			Window.alert("No loaded kpis, please select one object type, a date and click Load Indicators");
			return;
		}
		if(objects.length < 1) {
			Window.alert("No loaded objects, load kpis first and choose filters that fit any object");
			return;
		}
		//int i = 0;
		String kpiStr = "", objStr = "";
		for (String kpi : kpis) {
			kpiStr += "," + kpi;
		}
		/*for (KpiObjectValues obj : objects) {
			if(i >= 1) break;
			objStr += "," + obj.getSymbol();
			i++;
		}*/
		kpiStr = kpiStr.substring(1);
		objStr = ((Hyperlink)event.getSource()).getText();
		System.out.println(options.getDate() + " " + options.getObjType());
		System.out.println(objStr);
		System.out.println(kpiStr);
		//SmartKpis.getChart().alterChart(options.getDate().replace("-", ""), options.getObjType(), objStr, kpiStr);
		//SmartKpis.setScreen("chart");
		rebuildChart( "/smartkpis/scbuild?chartType=line&kpis="+kpiStr+"&obj="+objStr+"&dataTable="+options.getObjType()+
				"&date="+options.getDate().replace("-", "")+"&colorIndex=0" );
		//SmartKpis.getVisualization().loadData(options.getObjType(), "date_sub(" + options.getDate().replace("-", "") + ", interval 2 month)", 
		//		options.getDate().replace("-", ""), kpiStr, objStr);
		//SmartKpis.setScreen("viz");
	}

	private void rebuildChart(String url) {
		if(kpiPanel != null) if(kpiPanel.getWidgetIndex(chart) > 0) kpiPanel.remove(chart);
		chart = new FusionChartWidget( FusionChartWidget.TYPE_MSLINE2D, 500, 250 );
		chart.setDataUrl(url);
		kpiPanel.add(chart);
	}
}
