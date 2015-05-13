package org.colombiamovil.smartkpi.client.ui.charts;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Utility class to handle the visualization of a grid of charts
 * */
public class InfoSeeMapsHandler extends Composite {

	private VerticalPanel mainPanel;
//	private HorizontalPanel northPanel;
	private VerticalPanel centerPanel;
	private Frame map;

	private int numRows = 1, numCols = 1;

	public InfoSeeMapsHandler() {
		mainPanel = new VerticalPanel();
//		northPanel = new HorizontalPanel();
		centerPanel = new VerticalPanel();
		map = new Frame();
//		IFrameElement.as(map.getElement()).setFrameBorder(0);
		DOM.setElementAttribute(map.getElement(), "frameborder", "0");
		DOM.setElementAttribute(map.getElement(), "scrolling", "no");
		DOM.setElementAttribute(map.getElement(), "style", "margin:0 auto;visibility:visible");
		map.setWidth("1260px");
		map.setHeight("680px");

		centerPanel.setSpacing(0);
		centerPanel.setWidth("100%");
		centerPanel.add(map);

		mainPanel.setSpacing(5);
		mainPanel.setWidth("100%");

//		mainPanel.add(northPanel);
		mainPanel.add(centerPanel);
		initWidget(mainPanel);
	}

	public void displayCharts() {
	}

	public void arrangeCharts() {
	}

	public void setChart(int row, int col, LineChartVisualization chart) {
	}

	public Frame getFrame() {
		return map;
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
