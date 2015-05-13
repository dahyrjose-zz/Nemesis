package org.colombiamovil.smartkpi.client.ui;

import java.util.Date;

import org.colombiamovil.smartkpi.client.ui.drill.DrillableWidget;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DrillDownReport extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel contentPanel = new HorizontalPanel();
	private HorizontalPanel topPanel = new HorizontalPanel();
	private HorizontalPanel aggPanel = new HorizontalPanel();
	private VerticalPanel chartPanel = new VerticalPanel();
	private VerticalPanel leftPanel = new VerticalPanel();
	private VerticalPanel rangePanel = new VerticalPanel();
	private VerticalPanel portletPanel = new VerticalPanel();
	private VerticalPanel dummyPanel = new VerticalPanel();
	// General options
	private Label saveProcess = new Label("Save Report"), timeLineMode = new Label("Time Line Mode"), reportConfig = new Label("Back to Report Configuration");
	// Analysis configuration links
	private Label changeCounter = new Label("Change Counter"), changeObject = new Label("Change Object"), 
		changeRange = new Label("Change Time Range"), changeAnalysisRange = new Label("Change Analysis Range");
	// Analysis parameters labels
	private Label counterLabel = new Label(), objectLabel = new Label(), timeRangeLabel = new Label(), timeAnalysisLabel = new Label();
	private Date analysisStartTime, analysisEndTime;
	private String objectType, iniDate, endDate, kpis, object, tableType;

	public DrillDownReport() {
		mainPanel.setSpacing(5);
		mainPanel.setWidth("100%");
		dummyPanel.setHeight("10px");
		dummyPanel.setWidth("100%");

		topPanel.setStyleName("oddRow");
		topPanel.addStyleName("silverBottom");
		topPanel.setSpacing(5);
		topPanel.setWidth("100%");
		topPanel.add(saveProcess);
		topPanel.add(timeLineMode);
		topPanel.add(reportConfig);

		changeCounter.addStyleName("activelink");
		changeObject.addStyleName("activelink");
		changeRange.addStyleName("activelink");
		changeAnalysisRange.addStyleName("activelink");

		rangePanel.setSpacing(5);
		rangePanel.setWidth("200px");
		rangePanel.setStyleName("oddRow");
		rangePanel.add(changeCounter);
		rangePanel.add(changeObject);
		rangePanel.add(changeRange);
		rangePanel.add(changeAnalysisRange);
		rangePanel.addStyleName("silverBottom");

		portletPanel.setSpacing(5);
		portletPanel.setWidth("200px");
		portletPanel.setStyleName("oddRow");
//		portletPanel.add(new HTML("<p>Place portlet here please</p>"));
		portletPanel.add(counterLabel);
		portletPanel.add(objectLabel);
		portletPanel.add(timeRangeLabel);
		portletPanel.add(timeAnalysisLabel);
		portletPanel.addStyleName("silverBottom");

		leftPanel.add(rangePanel);
		leftPanel.add(dummyPanel);
		leftPanel.add(portletPanel);

		chartPanel.setWidth("100%");

		contentPanel.setWidth("100%");
//		contentPanel.add(leftPanel);
		contentPanel.add(chartPanel);

		mainPanel.add(topPanel);
		mainPanel.add(contentPanel);
		initWidget(mainPanel);
	}

	public void setAnalysisRange(Date analysisStartTime, Date analysisEndTime) {
		this.analysisStartTime = analysisStartTime;
		this.analysisEndTime = analysisEndTime;
	}

	public void setParameters(String objectType, String iniDate, String endDate, String kpis, String object, String tableType) {
		this.objectType = objectType;
		this.iniDate = iniDate;
		this.endDate = endDate;
		this.kpis = kpis;
		this.object = object;
		this.tableType = tableType;
	}

	public void loadMainChart() {
		chartPanel.clear();
		chartPanel.add(aggPanel);
		chartPanel.add(DrillableWidget.getInstance(0).setDataParams(objectType, iniDate, endDate, kpis, object, tableType, "-1"));
		counterLabel.setText("Counter: " + kpis);
		objectLabel.setText("Object: " + object);
		timeRangeLabel.setText("Range: " + iniDate + " - " + endDate);
		if(analysisStartTime != null && analysisEndTime != null) 
		timeAnalysisLabel.setText("Analysis Range: " + DateTimeFormat.getFormat("yyyyMMdd").format(analysisStartTime) + " - " + DateTimeFormat.getFormat("yyyyMMdd").format(analysisEndTime));

	}

	public void loadDrillChart(int callerIndex, String objectType, String kpi, String object, String groupBy) {
		chartPanel.add(DrillableWidget.getInstance(callerIndex+1).setDataParams(objectType, iniDate, endDate, kpi, object, tableType, groupBy));
	}
}
