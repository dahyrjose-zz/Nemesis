package org.colombiamovil.smartkpi.client.ui.drill;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.ui.charts.fusion.FusionChartWidget;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DrillableWidget extends VerticalPanel implements ClickHandler {
	private HorizontalPanel optionsContainer, chartContainer;
	private FusionChartWidget chart;
	// Drill options
	private Label doDrill = new Label("Drill Down!");
	private ListBox drillThroughBox = new ListBox(), drillCounterBox = new ListBox(), drillMethodBox = new ListBox(), drillGroupByBox = new ListBox();
	private int lineWidth = 700, lineHeight = 300;
	private int widgetIndex;

	// Instances Map
	private static Map<Integer, DrillableWidget> instances = new LinkedHashMap<Integer, DrillableWidget>();
	private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc();
	private String objectType, iniDate, endDate, kpi, object, tableType, groupBy;

	private FlexTable correlationTable;

	private DrillableWidget() {
		super();
		optionsContainer = new HorizontalPanel();
		optionsContainer.setSpacing(5);
		chartContainer = new HorizontalPanel();
		chartContainer.setSpacing(5);

		drillThroughBox.addStyleName("field_input");
		drillCounterBox.addStyleName("field_input");
		drillMethodBox.addStyleName("field_input");
		drillGroupByBox.addStyleName("field_input");
		doDrill.addStyleName("activelink");
		doDrill.addClickHandler(this);
		setSpacing(5);
//		setWidth("100%");
		addStyleName("silverBottom");
		optionsContainer.setStyleName("oddRow");
		optionsContainer.setWidth("100%");
		optionsContainer.add(drillThroughBox);
		optionsContainer.add(drillCounterBox);
		optionsContainer.add(drillMethodBox);
		optionsContainer.add(drillGroupByBox);
		optionsContainer.add(doDrill);

		chart = new FusionChartWidget( FusionChartWidget.TYPE_MSLINE2D, lineWidth, lineHeight );
//		first.setDataUrl( "/smartkpis/xmldatasup?objectType=msc&iniDate=20100110&endDate=20100117&kpis=ASR&objects='MSSBOG2'&tableType=det&format=fusionxml" );
//		chart.setDataUrl( "/smartkpis/xmldatasup?objectType="+objectType+"&iniDate="+iniDate.replace("-", "")+"&endDate="+endDate.replace("-", "")+"&kpis="+kpis+"&objects='"+object+"'&tableType="+tableType+"&format=fusionxml" );
		chartContainer.add(chart);
//		chartContainer.add(new HTML("<h1>100</h1>"));
		add(optionsContainer);
		add(chartContainer);
	}

	public static DrillableWidget getInstance() {
		return instances.get(instances.size());
	}

	public static DrillableWidget getInstance(int index) {
		if(instances.get(index) == null) {
			instances.put(index, new DrillableWidget());
			instances.get(index).setWidgetIndex(index);
		}
		return instances.get(index);
	}

	public void setWidgetIndex(int widgetIndex) {
		this.widgetIndex = widgetIndex;
	}

	public DrillableWidget setDataParams(String objectType, String iniDate, String endDate, 
			String kpi, String object, String tableType, String groupBy) {
		this.objectType = objectType;
		this.iniDate = iniDate;
		this.endDate = endDate;
		this.kpi = kpi;
		this.object = object;
		this.tableType = tableType;
		this.groupBy = groupBy;
		// TODO Get this from DB
		drillThroughBox.clear();
		drillCounterBox.clear();
		drillMethodBox.clear();
		drillGroupByBox.clear();

		drillThroughBox.addItem("Drill Through", "-1");
		drillThroughBox.addItem("Routes", "route");
		drillThroughBox.addItem("Linksets", "linkset");
		drillCounterBox.addItem("Drill Counter", "-1");
		drillCounterBox.addItem("sum(carried_calls_with_answer)/sum(carried_calls)", "route_asr");
		drillMethodBox.addItem("Correlation Ratio", "correlation_ratio");
		drillGroupByBox.addItem("Group By", "-1");
		drillGroupByBox.addItem("traffic_type", "traffic_type");

		// Build chart
		chart.setDataUrl("/smartkpis/xmldatasup?objectType="+objectType+"&iniDate="+iniDate.replace("-", "")+"&endDate="+endDate.replace("-", "")+"&kpis="+kpi+"&objects='"+object+"'&tableType="+tableType+"&groupBy="+groupBy+"&format=fusionxml");
		return this;
	}

	public void reload() {
		chart.setDataUrl("/smartkpis/xmldatasup?objectType="+objectType+"&iniDate="+iniDate.replace("-", "")+"&endDate="+endDate.replace("-", "")+"&kpis="+kpi+"&objects='"+object+"'&tableType="+tableType+"&groupBy="+groupBy+"&format=fusionxml");
	}

	public void onClick(ClickEvent event) {
		if(event.getSource().equals(doDrill)) {
			getCorrelationTable();
		} else if(event.getSource() instanceof Label) {
			if(((Label)event.getSource()).getParent().equals(correlationTable)) {
				SmartKpis.getDrillDownReport().loadDrillChart(widgetIndex, drillThroughBox.getValue(drillThroughBox.getSelectedIndex()), 
						drillCounterBox.getItemText(drillCounterBox.getSelectedIndex()), ((Label)event.getSource()).getText(), 
						drillGroupByBox.getValue(drillGroupByBox.getSelectedIndex()));
			}
		}
	}

	private void getCorrelationTable() {
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}
		AsyncCallback<List<CorrelationBean>> callback = new AsyncCallback<List<CorrelationBean>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR Getting Report Menu: " + caught.getMessage());
			}
			public void onSuccess(List<CorrelationBean> result) {
				buildCorrelationTable(result);
			}
		};
//		String xObjectType, String yObjectType, String tableType, 
//		String groupBy, String yObject, String iniDate, String endDate, String kpi, String xFilter, String yFilter
		String xObjectType = drillThroughBox.getValue(drillThroughBox.getSelectedIndex());
		String yObjectType = xObjectType;
		String groupBy = drillGroupByBox.getValue(drillGroupByBox.getSelectedIndex());
		String kpi = drillCounterBox.getItemText(drillCounterBox.getSelectedIndex());
		String xFilter = widgetIndex == 0 ? "msc" : groupBy;
		String yFilter = xFilter;
		dataSvc.getCorrelationTable(xObjectType, yObjectType, tableType, 
				groupBy, object, "20100114", "20100114", kpi, xFilter, yFilter, callback);
	}

	private void buildCorrelationTable(List<CorrelationBean> data) {
		if(chartContainer.getWidgetCount() > 1) chartContainer.remove(1);
		correlationTable = new FlexTable();
		correlationTable.setWidth("100%");
		correlationTable.setCellPadding(4);
		correlationTable.setCellSpacing(0);
		correlationTable.addStyleName("watchList");
		correlationTable.getRowFormatter().addStyleName(0, "watchListHeader");
		correlationTable.setText(0, 0, "X");
		correlationTable.setText(0, 1, "Y");
		correlationTable.setText(0, 2, "Correlation Ratio");
		correlationTable.setText(0, 3, "Weight");

		Label xObject;
		for (int i = 0; i < data.size(); i++) {
			xObject = new Label(data.get(i).getXObject());
			xObject.addStyleName("activelink");
			xObject.addClickHandler(this);
			correlationTable.getRowFormatter().addStyleName(i + 1, (i % 2 != 0 ? "evenRow" : "oddRow"));
			correlationTable.setWidget(i + 1, 0, xObject);
			correlationTable.setText(i + 1, 1, data.get(i).getYObject());
			correlationTable.setText(i + 1, 2, data.get(i).getCorrelationRatio().toString());
			correlationTable.setText(i + 1, 3, data.get(i).getWeight().toString());
		}
		chartContainer.add(correlationTable);
	}
}
