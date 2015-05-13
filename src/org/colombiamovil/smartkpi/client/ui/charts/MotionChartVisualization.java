package org.colombiamovil.smartkpi.client.ui.charts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.MotionChart;
import com.google.gwt.visualization.client.visualizations.MotionChart.Options;

/**
 * Demo for MotionChart visualization.
 */
public class MotionChartVisualization extends VerticalPanel {
	private Widget widget;

	public MotionChartVisualization() {
		//this.add(new HTML("Fusion Chart 2"));

		String protocol = Window.Location.getProtocol();
		if (protocol.startsWith("file")) {
			widget = new HTML(
					"<font color=\"blue\"><i>Note: Protocol is: "
							+ protocol
							+ ".  Note that this visualization does not work when loading the HTML from "
							+ "a local file. It works only when loading the HTML from a "
							+ "web server. </i></font>");
			return;
		}

		@SuppressWarnings("unused")
		int year, month, day;

		Options options = Options.create();
		options.setHeight(300);
		options.setWidth(600);

		DataTable data = DataTable.create();
		data.addRows(6);
		data.addColumn(ColumnType.STRING, "Fruit");
		data.addColumn(ColumnType.DATE, "Date");
		data.addColumn(ColumnType.NUMBER, "Sales");
		data.addColumn(ColumnType.NUMBER, "Expenses");
		data.addColumn(ColumnType.STRING, "Location");
		data.setValue(0, 0, "Apples");
		data.setValue(0, 2, 1000);
		data.setValue(0, 3, 300);
		data.setValue(0, 4, "East");
		data.setValue(1, 0, "Oranges");
		data.setValue(1, 2, 950);
		data.setValue(1, 3, 200);
		data.setValue(1, 4, "West");
		data.setValue(2, 0, "Bananas");
		data.setValue(2, 2, 300);
		data.setValue(2, 3, 250);
		data.setValue(2, 4, "West");
		data.setValue(3, 0, "Apples");
		data.setValue(3, 2, 1200);
		data.setValue(3, 3, 400);
		data.setValue(3, 4, "East");
		data.setValue(4, 0, "Oranges");
		data.setValue(4, 2, 900);
		data.setValue(4, 3, 150);
		data.setValue(4, 4, "West");
		data.setValue(5, 0, "Bananas");
		data.setValue(5, 2, 788);
		data.setValue(5, 3, 617);
		data.setValue(5, 4, "West");

		try {
			data.setValue(0, 1, 2004);
			data.setValue(1, 1, 2005);
			data.setValue(2, 1, 2006);
			data.setValue(3, 1, 2007);
			data.setValue(4, 1, 2008);
			data.setValue(5, 1, 2009);
		} catch (JavaScriptException ex) {
			GWT.log("Error creating data table - Date bug on mac?", ex);
		}

		widget = new MotionChart(data, options);
		this.add(widget);

	}
}
