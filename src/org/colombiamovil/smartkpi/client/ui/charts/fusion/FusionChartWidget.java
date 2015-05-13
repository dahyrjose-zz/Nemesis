package org.colombiamovil.smartkpi.client.ui.charts.fusion;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class FusionChartWidget extends Composite {

	public final static int TYPE_MSLINE2D = 1, TYPE_STACKEDCOLUMN2D = 2, TYPE_AREA2D = 3;
	private static int seqId = 1;
	private int chartType = TYPE_MSLINE2D;
	private int width = 0;
	private int height = 0;

	private HTML holder = null;
	private String divId = null;;
	private String dataUrl = null;

	public FusionChartWidget( int chartType, int width, int height ) {
	    this.chartType = chartType;
	    this.width = width;
	    this.height = height;
	    divId = "chart" + (seqId++);
	    holder = new HTML("<div id=\"" + divId + "\"></div>");
	    initWidget( holder );
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	@Override
	protected void initWidget(Widget widget) {
		super.initWidget(widget);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		FusionChartJS chart = null;
		switch (chartType) {
			case TYPE_AREA2D :
				chart = FusionChartJS.getAreaInstance(Integer.toString(width), Integer.toString(height));
				break;
			case TYPE_MSLINE2D :
				chart = FusionChartJS.getMSLineInstance(Integer.toString(width), Integer.toString(height));
				break;
			case TYPE_STACKEDCOLUMN2D :
				chart = FusionChartJS.getStackedColumn2DInstance(Integer.toString(width), Integer.toString(height));
				break;
		}

		chart.setDataUrl(dataUrl);
		chart.render(divId);
	}
}