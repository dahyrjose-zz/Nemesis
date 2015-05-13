package org.colombiamovil.smartkpi.client.ui.charts;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Utility class to handle the visualization of a grid of charts
 * */
public class MultipleChartHandler extends Composite {

	private VerticalPanel mainPanel;
//	private HorizontalPanel northPanel;
	private VerticalPanel centerPanel;
	private List<Widget> charts;
	private FlexTable chartsGrid;

	private int numRows = 1, numCols = 1;

	public MultipleChartHandler() {
		mainPanel = new VerticalPanel();
//		northPanel = new HorizontalPanel();
		centerPanel = new VerticalPanel();
		charts = new Vector<Widget>();
		chartsGrid = new FlexTable();
		chartsGrid.setWidth("100%");
		chartsGrid.setHeight("100%");

		centerPanel.setSpacing(10);
		centerPanel.setWidth("100%");
		centerPanel.add(chartsGrid);

		mainPanel.setSpacing(5);
		mainPanel.setWidth("100%");

//		mainPanel.add(northPanel);
		mainPanel.add(centerPanel);
		initWidget(mainPanel);
	}

	public void displayCharts() {
//		centerPanel.clear();
//		System.out.println(rows);
		chartsGrid.clear();
		for (int i=0; i<getNumRows(); i++) {
			for(int j=0; j<getNumCols(); j++) {
				chartsGrid.setWidget(i, j, charts.get(i*getNumCols()+j));
			}
		}
	}

	public void arrangeCharts() {
		for(int i=0; i<getNumRows(); i++) {
			if(i >= chartsGrid.getRowCount()) break;
			for(int j=0; j<getNumCols(); j++) {
//				if(j >= chartsGrid.getCellCount(i)) break;
				chartsGrid.getCellFormatter().setWidth(i, j, 100 / getNumCols() + "%");
			}
		}
		for (int i=0; i<chartsGrid.getRowCount(); i++) {
			for(int j=0; j<chartsGrid.getCellCount(i); j++) {
				chartsGrid.getCellFormatter().setWidth(i, j, 100 / getNumCols() + "%");
				if(chartsGrid.getWidget(i, j) != null) chartsGrid.getWidget(i, j).setVisible(false);
			}
		}
	}

	public void setChart(int row, int col, LineChartVisualization chart) {
		chartsGrid.setWidget(row, col, chart);
	}

	public LineChartVisualization getChart(int row, int col) {
//		System.out.println(chartsGrid);
		//if(row >= getNumRows() || col >= getNumCols()) throw new NemesisException("Index out of range");
		if(row >= chartsGrid.getRowCount()) {
			chartsGrid.getCellFormatter().setWidth(row, col, 100 / getNumCols() + "%");
			LineChartVisualization chart = new LineChartVisualization(LineChartVisualization.MODE_MULTIPLE);
			chartsGrid.setWidget(row, col, chart);
		} else if(chartsGrid.getCellCount(row) <= col) {
			chartsGrid.getCellFormatter().setWidth(row, col, 100 / getNumCols() + "%");
			LineChartVisualization chart = new LineChartVisualization(LineChartVisualization.MODE_MULTIPLE);
			chartsGrid.setWidget(row, col, chart);
		}
		chartsGrid.getWidget(row, col).setVisible(true);
		return (LineChartVisualization)chartsGrid.getWidget(row, col);
	}

	/**
	 * @param numRows the numRows to set
	 */
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	/**
	 * @return the numRows
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @param numCols the numCols to set
	 */
	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	/**
	 * @return the numCols
	 */
	public int getNumCols() {
		return numCols;
	}

	
}
