package org.colombiamovil.smartkpi.client.ui.charts;

import org.colombiamovil.smartkpi.client.ui.charts.fusion.FusionChartWidget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WeeklyReports extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel contentPanel = new HorizontalPanel();
	private HorizontalPanel topPanel = new HorizontalPanel();
	private HorizontalPanel aggPanel = new HorizontalPanel();
	private HorizontalPanel chartContainer;
	private VerticalPanel chartPanel = new VerticalPanel();
	private VerticalPanel leftPanel = new VerticalPanel();
	private VerticalPanel rangePanel = new VerticalPanel();
	private VerticalPanel portletPanel = new VerticalPanel();
	private VerticalPanel dummyPanel = new VerticalPanel();
	private FusionChartWidget first, second, third, fourth;
	private Label geranCat = new Label("GERAN"), utranCat = new Label("UTRAN"), coreCat = new Label("CORE Network");
	private Label networkAgg = new Label("Network"), regionAgg = new Label("Region"), prefixAgg = new Label("Prefix");
	private Label dailyRange = new Label("Last 10 days"), weeklyRange = new Label("Last 10 weeks"), 
		monthlyRange = new Label("Last 10 months"), quarterRange = new Label("Last 8 Quarters");

	private int lineWidth = 700, lineHeight = 100; 
	public WeeklyReports() {
		mainPanel.setSpacing(5);
		mainPanel.setWidth("100%");
		dummyPanel.setHeight("10px");
		dummyPanel.setWidth("100%");

		topPanel.setStyleName("oddRow");
		topPanel.addStyleName("silverBottom");
		topPanel.setSpacing(5);
		topPanel.setWidth("100%");
		topPanel.add(geranCat);
		topPanel.add(utranCat);
		topPanel.add(coreCat);

		rangePanel.setSpacing(5);
		rangePanel.setWidth("200px");
		rangePanel.setStyleName("oddRow");
		rangePanel.add(dailyRange);
		rangePanel.add(weeklyRange);
		rangePanel.add(monthlyRange);
		rangePanel.add(quarterRange);
		rangePanel.addStyleName("silverBottom");

		portletPanel.setSpacing(5);
		portletPanel.setWidth("200px");
		portletPanel.setStyleName("oddRow");
		portletPanel.add(new HTML("<p>Place portlet here please</p>"));
		portletPanel.addStyleName("silverBottom");

		leftPanel.add(rangePanel);
		leftPanel.add(dummyPanel);
		leftPanel.add(portletPanel);

		chartPanel.setWidth("100%");

		aggPanel.setSpacing(5);
		aggPanel.setStyleName("oddRow");
		aggPanel.addStyleName("silverBottom");
		aggPanel.setWidth("100%");
		aggPanel.add(networkAgg);
		aggPanel.add(regionAgg);
		aggPanel.add(prefixAgg);
		chartPanel.add(aggPanel);

		chartContainer = new HorizontalPanel();
		first = new FusionChartWidget( FusionChartWidget.TYPE_MSLINE2D, lineWidth, lineHeight );
		first.setDataUrl( "/smartkpis/repbuild?chartType=line&kpi=TCH_Full&colorIndex=0" );
		chartContainer.add(first);
		chartContainer.add(new HTML("<h1>100</h1>"));
		chartPanel.add(chartContainer);

		chartContainer = new HorizontalPanel();
		second = new FusionChartWidget( FusionChartWidget.TYPE_MSLINE2D, lineWidth, lineHeight );
		second.setDataUrl( "/smartkpis/repbuild?chartType=line&kpi=TCH_Half&colorIndex=1" );
		chartContainer.add(second);
		chartContainer.add(new HTML("<h1>100</h1>"));
		chartPanel.add(chartContainer);

		chartContainer = new HorizontalPanel();
		third = new FusionChartWidget( FusionChartWidget.TYPE_MSLINE2D, lineWidth, lineHeight );
		third.setDataUrl( "/smartkpis/repbuild?chartType=line&kpi=TCH_Lost&colorIndex=2" );
		chartContainer.add(third);
		chartContainer.add(new HTML("<h1>100</h1>"));
		chartPanel.add(chartContainer);

		chartContainer = new HorizontalPanel();
		fourth = new FusionChartWidget( FusionChartWidget.TYPE_MSLINE2D, lineWidth, lineHeight );
		fourth.setDataUrl( "/smartkpis/repbuild?chartType=line&kpi=Traff_Carr&colorIndex=3" );
		chartContainer.add(fourth);
		chartContainer.add(new HTML("<h1>100</h1>"));
		chartPanel.add(chartContainer);

		contentPanel.setWidth("100%");
		contentPanel.add(leftPanel);
		contentPanel.add(chartPanel);

		mainPanel.add(topPanel);
		mainPanel.add(contentPanel);
		initWidget(mainPanel);
	}

}
